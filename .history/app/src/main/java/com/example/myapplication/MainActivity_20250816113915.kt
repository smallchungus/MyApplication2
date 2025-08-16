package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.auth.AuthRepository
import com.example.myapplication.auth.LoginScreen
import com.example.myapplication.ui.theme.MedAppTheme
import com.example.myapplication.FamilyDashboardScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for ParentCare family coordination app.
 * 
 * Handles navigation between authentication and main app based on user state.
 * Integrates with Firebase Authentication for persistent login state.
 * 
 * @since 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firebase with error handling
        val firebaseInitialized = FirebaseManager.initializeFirebase(this)
        
        setContent {
            MedAppTheme {
                if (firebaseInitialized) {
                    ParentCareApp()
                } else {
                    // Development mode - show UI without Firebase
                    DevelopmentModeApp()
                }
            }
        }
    }
}

/**
 * Main app composable that handles authentication state and navigation.
 * 
 * This composable observes the authentication state and renders the appropriate
 * screen based on whether the user is authenticated and has family groups set up.
 * 
 * Navigation Flow:
 * 1. Unauthenticated -> LoginScreen
 * 2. Authenticated without family groups -> FamilySetupScreen (to be built)
 * 3. Authenticated with family groups -> FamilyDashboardScreen
 * 
 * @param authRepository Authentication repository for user state management
 */
@Composable
fun ParentCareApp(
    authRepository: AuthRepository = hiltViewModel<AuthRepository>()
) {
    val authState by authRepository.authState.collectAsState()
    
    when (authState) {
        is AuthRepository.AuthState.Loading -> {
            // Show loading screen while checking authentication state
            LoadingScreen()
        }
        is AuthRepository.AuthState.Unauthenticated -> {
            // User not signed in, show login screen
            LoginScreen(
                onNavigateToFamilySetup = { /* Navigate to family setup */ },
                onNavigateToDashboard = { /* Navigate to dashboard */ },
                authRepository = authRepository
            )
        }
        is AuthRepository.AuthState.Authenticated -> {
            val authenticatedState = authState as AuthRepository.AuthState.Authenticated
            if (authenticatedState.userData.familyGroups.isEmpty()) {
                // User signed in but no family groups, show setup screen
                FamilySetupScreen()  // To be built next
            } else {
                // User signed in with family groups, show main dashboard
                FamilyDashboardScreen()
            }
        }
        is AuthRepository.AuthState.Error -> {
            // Authentication error occurred, show error screen
            val errorState = authState as AuthRepository.AuthState.Error
            ErrorScreen(message = errorState.message)
        }
    }
}

/**
 * Loading screen displayed while checking authentication state.
 * 
 * Provides visual feedback to users during app initialization.
 */
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading...")
    }
}

/**
 * Error screen displayed when authentication fails.
 * 
 * Shows user-friendly error messages and recovery options.
 * 
 * @param message Error message to display to the user
 */
@Composable
fun ErrorScreen(message: String) {
    // Simple error screen - can be enhanced with retry options
    androidx.compose.material3.Text(
        text = "Error: $message",
        color = androidx.compose.material3.MaterialTheme.colorScheme.error
    )
}

/**
 * Placeholder for family setup screen (to be implemented next).
 * 
 * This screen will allow users to create or join family groups
 * after successful authentication.
 */
@Composable
fun FamilySetupScreen() {
    // Placeholder - will be implemented in next iteration
    androidx.compose.material3.Text("Family Setup Screen - Coming Soon!")
}