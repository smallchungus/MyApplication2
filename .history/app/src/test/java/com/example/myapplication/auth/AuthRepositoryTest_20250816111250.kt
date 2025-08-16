package com.example.myapplication.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Comprehensive test suite for AuthRepository authentication operations.
 * 
 * These tests verify the complete authentication flow including user sign-in,
 * account creation, error handling, and state management. Critical for family
 * coordination app where authentication security and reliability are paramount.
 * 
 * Test Strategy:
 * - Mock Firebase services for isolated testing
 * - Test all authentication states and transitions
 * - Verify error handling for common failure scenarios
 * - Test coroutine-based async operations
 * - Validate user data persistence in Firestore
 * 
 * @author ParentCare Development Team
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {
    
    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth
    
    @Mock
    private lateinit var mockFirestore: FirebaseFirestore
    
    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser
    
    @Mock
    private lateinit var mockDocumentReference: DocumentReference
    
    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    
    private lateinit var authRepository: AuthRepository
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(mockFirebaseAuth, mockFirestore)
    }
    
    /**
     * Test: Successful email sign-in returns success result.
     * 
     * Verifies that valid credentials result in successful authentication
     * and proper user data handling.
     */
    @Test
    fun `signInWithEmail with valid credentials returns success`() = runTest {
        // Given: Valid email and password
        val email = "test@example.com"
        val password = "password123"
        
        // And: Mock successful Firebase authentication
        val mockAuthResult = Mockito.mock(com.google.firebase.auth.AuthResult::class.java)
        Mockito.`when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockk())
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password).await()).thenReturn(mockAuthResult)
        
        // When: Attempting to sign in
        val result = authRepository.signInWithEmail(email, password)
        
        // Then: Should return success result
        assertTrue("Sign-in should succeed with valid credentials", result.isSuccess)
        assertNotNull("User should not be null", result.user)
    }
    
    /**
     * Test: Invalid email format returns appropriate error message.
     * 
     * Verifies that malformed email addresses are properly validated
     * and user-friendly error messages are returned.
     */
    @Test
    fun `signInWithEmail with invalid email returns error`() = runTest {
        // Given: Invalid email format
        val email = "invalid-email"
        val password = "password123"
        
        // And: Mock Firebase authentication exception
        val mockException = mockk<FirebaseAuthException>()
        every { mockException.errorCode } returns "ERROR_INVALID_EMAIL"
        coEvery { mockFirebaseAuth.signInWithEmailAndPassword(email, password).await() } throws mockException
        
        // When: Attempting to sign in
        val result = authRepository.signInWithEmail(email, password)
        
        // Then: Should return error result
        assertFalse("Sign-in should fail with invalid email", result.isSuccess)
        assertTrue("Result should be error type", result.isError)
        assertEquals(
            "Should return user-friendly error message",
            "Please enter a valid email address",
            result.message
        )
    }
    
    /**
     * Test: Wrong password returns appropriate error message.
     * 
     * Verifies that incorrect passwords are handled gracefully
     * with actionable error messages for users.
     */
    @Test
    fun `signInWithEmail with wrong password returns error`() = runTest {
        // Given: Valid email but wrong password
        val email = "test@example.com"
        val password = "wrongpassword"
        
        // And: Mock Firebase authentication exception
        val mockException = mockk<FirebaseAuthException>()
        every { mockException.errorCode } returns "ERROR_WRONG_PASSWORD"
        coEvery { mockFirebaseAuth.signInWithEmailAndPassword(email, password).await() } throws mockException
        
        // When: Attempting to sign in
        val result = authRepository.signInWithEmail(email, password)
        
        // Then: Should return error result
        assertFalse("Sign-in should fail with wrong password", result.isSuccess)
        assertTrue("Result should be error type", result.isError)
        assertEquals(
            "Should return user-friendly error message",
            "Incorrect password. Please try again.",
            result.message
        )
    }
    
    /**
     * Test: Account creation with valid data returns success.
     * 
     * Verifies that new user accounts are properly created in both
     * Firebase Authentication and Firestore.
     */
    @Test
    fun `createAccountWithEmail with valid data returns success`() = runTest {
        // Given: Valid account creation data
        val email = "newuser@example.com"
        val password = "securepassword123"
        val displayName = "New User"
        
        // And: Mock successful Firebase account creation
        val mockAuthResult = mockk<com.google.firebase.auth.AuthResult>()
        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "new-user-uid"
        every { mockFirebaseAuth.createUserWithEmailAndPassword(email, password) } returns mockk()
        coEvery { mockFirebaseAuth.createUserWithEmailAndPassword(email, password).await() } returns mockAuthResult
        
        // And: Mock successful Firestore document creation
        coEvery { mockFirestore.collection("users").document("new-user-uid").set(any()) } returns mockk()
        coEvery { mockFirestore.collection("users").document("new-user-uid").set(any()).await() } returns Unit
        
        // When: Creating new account
        val result = authRepository.createAccountWithEmail(email, password, displayName)
        
        // Then: Should return success result
        assertTrue("Account creation should succeed", result.isSuccess)
        assertNotNull("User should not be null", result.user)
    }
    
    /**
     * Test: Weak password returns appropriate error message.
     * 
     * Verifies that password strength requirements are enforced
     * and clear guidance is provided to users.
     */
    @Test
    fun `createAccountWithEmail with weak password returns error`() = runTest {
        // Given: Weak password
        val email = "test@example.com"
        val password = "123"
        val displayName = "Test User"
        
        // And: Mock Firebase authentication exception
        val mockException = mockk<FirebaseAuthException>()
        every { mockException.errorCode } returns "ERROR_WEAK_PASSWORD"
        coEvery { mockFirebaseAuth.createUserWithEmailAndPassword(email, password).await() } throws mockException
        
        // When: Attempting to create account
        val result = authRepository.createAccountWithEmail(email, password, displayName)
        
        // Then: Should return error result
        assertFalse("Account creation should fail with weak password", result.isSuccess)
        assertTrue("Result should be error type", result.isError)
        assertEquals(
            "Should return user-friendly error message",
            "Password is too weak. Please choose a stronger password.",
            result.message
        )
    }
    
    /**
     * Test: Duplicate email returns appropriate error message.
     * 
     * Verifies that existing email addresses are properly detected
     * and users are guided to sign in instead.
     */
    @Test
    fun `createAccountWithEmail with duplicate email returns error`() = runTest {
        // Given: Email that already exists
        val email = "existing@example.com"
        val password = "password123"
        val displayName = "Test User"
        
        // And: Mock Firebase authentication exception
        val mockException = mockk<FirebaseAuthException>()
        every { mockException.errorCode } returns "ERROR_EMAIL_ALREADY_IN_USE"
        coEvery { mockFirebaseAuth.createUserWithEmailAndPassword(email, password).await() } throws mockException
        
        // When: Attempting to create account
        val result = authRepository.createAccountWithEmail(email, password, displayName)
        
        // Then: Should return error result
        assertFalse("Account creation should fail with duplicate email", result.isSuccess)
        assertTrue("Result should be error type", result.isError)
        assertEquals(
            "Should return user-friendly error message",
            "An account with this email already exists",
            result.message
        )
    }
    
    /**
     * Test: Sign out clears authentication state.
     * 
     * Verifies that users can properly sign out and their
     * authentication state is cleared.
     */
    @Test
    fun `signOut clears authentication state`() = runTest {
        // Given: User is signed in
        every { mockFirebaseAuth.signOut() } returns Unit
        
        // When: Signing out
        val result = authRepository.signOut()
        
        // Then: Should succeed
        assertTrue("Sign out should succeed", result.isSuccess)
        
        // And: Firebase sign out should be called
        verify { mockFirebaseAuth.signOut() }
    }
    
    /**
     * Test: Authentication state flow emits correct states.
     * 
     * Verifies that the authentication state flow properly
     * emits state changes based on Firebase auth state.
     */
    @Test
    fun `authState flow emits correct states`() = runTest {
        // Given: Mock authentication state listener
        val authStateListenerSlot = slot<FirebaseAuth.AuthStateListener>()
        every { mockFirebaseAuth.addAuthStateListener(any()) } answers { 
            authStateListenerSlot.captured = firstArg()
        }
        every { mockFirebaseAuth.removeAuthStateListener(any()) } returns Unit
        
        // When: Starting to observe auth state
        val authStateFlow = authRepository.authState
        
        // Then: Should start with loading state
        val initialState = authStateFlow.first()
        assertTrue("Initial state should be loading", initialState is AuthRepository.AuthState.Loading)
        
        // When: Simulating unauthenticated state
        authStateListenerSlot.captured.onAuthStateChanged(mockk { every { currentUser } returns null })
        
        // Then: Should emit unauthenticated state
        val unauthenticatedState = authStateFlow.first()
        assertTrue("Should emit unauthenticated state", unauthenticatedState is AuthRepository.AuthState.Unauthenticated)
    }
}
