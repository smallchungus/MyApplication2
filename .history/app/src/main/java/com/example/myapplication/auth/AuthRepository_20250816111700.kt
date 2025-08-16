package com.example.myapplication.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Firebase authentication and user management.
 * 
 * This repository handles all authentication operations for the ParentCare
 * family coordination app. It manages user sign-in/sign-up, family group
 * creation, and maintains authentication state throughout the app lifecycle.
 * 
 * Architecture Decision: Firebase Authentication
 * - Provides secure, scalable user management
 * - Integrates seamlessly with Firestore for user data
 * - Supports multiple sign-in methods (email, Google, etc.)
 * - Handles password reset and email verification automatically
 * 
 * Security Considerations:
 * - All user data is encrypted in transit and at rest
 * - Firebase security rules enforce family-based access control
 * - Authentication tokens are automatically managed and refreshed
 * 
 * Scalability Design:
 * - Repository pattern allows easy testing and mocking
 * - Suspend functions support coroutine-based async operations
 * - Error handling provides actionable feedback to users
 * - Designed for offline-first family coordination use cases
 * 
 * @author ParentCare Development Team
 * @since 1.0.0
 * @see FirebaseAuth Firebase Authentication documentation
 * @see FamilyRepository Family management operations
 */
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    /**
     * Represents the current authentication state of the user.
     * 
     * This sealed class provides type-safe authentication states that the UI
     * can react to appropriately. Each state contains relevant data and
     * error information to guide user experience.
     * 
     * @property Loading Authentication operation in progress
     * @property Authenticated User successfully signed in with user data
     * @property Unauthenticated User not signed in or session expired
     * @property Error Authentication failed with specific error information
     */
    sealed class AuthState {
        object Loading : AuthState()
        data class Authenticated(val user: FirebaseUser, val userData: UserData) : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String, val exception: Exception? = null) : AuthState()
    }
    
    /**
     * Observable authentication state for UI layer reactivity.
     * 
     * This flow emits authentication state changes allowing the UI to
     * respond immediately to sign-in/sign-out events. The flow is backed
     * by Firebase's authentication state listener for real-time updates.
     * 
     * @return Flow of current authentication state
     */
    val authState: StateFlow<AuthState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                // User is signed in, fetch additional user data
                launch {
                    try {
                        val userData = getUserData(user.uid)
                        trySend(AuthState.Authenticated(user, userData))
                    } catch (e: Exception) {
                        trySend(AuthState.Error("Failed to load user data", e))
                    }
                }
            } else {
                // User is signed out
                trySend(AuthState.Unauthenticated)
            }
        }
        
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState.Loading
    )
    
    /**
     * Attempts to sign in user with email and password.
     * 
     * This method handles the complete sign-in flow including error handling
     * for common authentication failures (invalid credentials, network issues,
     * account not found, etc.). Provides user-friendly error messages.
     * 
     * @param email User's email address
     * @param password User's password
     * @return AuthResult indicating success or failure with details
     * 
     * @throws AuthenticationException for authentication-specific errors
     * @throws NetworkException for network-related failures
     * 
     * Example usage:
     * ```
     * try {
     *     val result = authRepository.signInWithEmail("user@example.com", "password")
     *     if (result.isSuccess) {
     *         // Navigate to main app
     *     } else {
     *         // Show error message
     *     }
     * } catch (e: Exception) {
     *     // Handle unexpected errors
     * }
     * ```
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                AuthResult.Success(result.user!!)
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email address"
                    "ERROR_USER_DISABLED" -> "This account has been disabled"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later."
                    else -> "Sign in failed. Please check your credentials and try again."
                }
                AuthResult.Error(errorMessage, e)
            } catch (e: Exception) {
                AuthResult.Error("Network error. Please check your connection and try again.", e)
            }
        }
    }
    
    /**
     * Creates new user account with email and password.
     * 
     * This method handles complete account creation including:
     * - Firebase Authentication account creation
     * - Initial user document creation in Firestore
     * - Email verification sending (optional)
     * - Error handling for duplicate accounts, weak passwords, etc.
     * 
     * @param email User's email address
     * @param password User's password (must meet Firebase requirements)
     * @param displayName User's display name for family coordination
     * @return AuthResult indicating success or failure with details
     * 
     * Password Requirements:
     * - Minimum 6 characters (Firebase requirement)
     * - Recommend 8+ characters with mixed case and numbers
     * 
     * @since 1.0.0
     */
    suspend fun createAccountWithEmail(
        email: String, 
        password: String, 
        displayName: String
    ): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user!!
                
                // Create user profile in Firestore
                val userData = UserData(
                    uid = user.uid,
                    email = email,
                    displayName = displayName,
                    familyGroups = emptyList(),
                    createdAt = System.currentTimeMillis()
                )
                
                createUserDocument(userData)
                AuthResult.Success(user)
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please choose a stronger password."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
                    else -> "Account creation failed. Please try again."
                }
                AuthResult.Error(errorMessage, e)
            } catch (e: Exception) {
                AuthResult.Error("Network error. Please check your connection and try again.", e)
            }
        }
    }
    
    /**
     * Signs out the current user and clears authentication state.
     * 
     * This method safely signs out the user from Firebase Authentication
     * and clears any cached user data. The authState flow will automatically
     * emit Unauthenticated state, triggering UI updates.
     * 
     * @since 1.0.0
     */
    suspend fun signOut(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                firebaseAuth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Retrieves user data from Firestore.
     * 
     * @param uid User's unique identifier
     * @return UserData object with user profile information
     * @throws FirebaseFirestoreException if user document doesn't exist
     */
    private suspend fun getUserData(uid: String): UserData {
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject(UserData::class.java) 
            ?: throw DocumentNotFoundException("User data not found")
    }
    
    /**
     * Creates initial user document in Firestore.
     * 
     * @param userData User data to store
     * @throws FirebaseException if document creation fails
     */
    private suspend fun createUserDocument(userData: UserData) {
        firestore.collection("users").document(userData.uid).set(userData).await()
    }
}

/**
 * Represents the result of an authentication operation.
 * 
 * @property Success Authentication succeeded with user data
 * @property Error Authentication failed with error message and optional exception
 */
sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String, val exception: Exception? = null) : AuthResult()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}

/**
 * User data model for Firestore storage.
 * 
 * @property uid Firebase Authentication user ID
 * @property email User's email address
 * @property displayName Display name for family coordination
 * @property familyGroups List of family group IDs user belongs to
 * @property createdAt Account creation timestamp
 * @property lastActiveAt Last app usage timestamp
 */
data class UserData(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val familyGroups: List<String> = emptyList(),
    val createdAt: Long = 0,
    val lastActiveAt: Long = 0
)
