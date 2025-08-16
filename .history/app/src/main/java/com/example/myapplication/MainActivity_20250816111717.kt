package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        setContent {
            MedAppTheme {
                ParentCareApp()
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
            if (authState.userData.familyGroups.isEmpty()) {
                // User signed in but no family groups, show setup screen
                FamilySetupScreen()  // To be built next
            } else {
                // User signed in with family groups, show main dashboard
                FamilyDashboardScreen()
            }
        }
        is AuthRepository.AuthState.Error -> {
            // Authentication error occurred, show error screen
            ErrorScreen(message = authState.message)
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
    // Simple loading screen - can be enhanced with app branding
    androidx.compose.material3.CircularProgressIndicator()
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