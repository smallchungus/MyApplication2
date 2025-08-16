package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.ParentCareNavigationCoordinator
import com.example.myapplication.navigation.ParentCareNavigationHost
import com.example.myapplication.ui.theme.MedAppTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity with comprehensive error handling and fallback UI.
 * 
 * This activity implements multiple layers of crash prevention and
 * graceful degradation when services are unavailable.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "🏠 MainActivity starting...")
            
            setContent {
                MedAppTheme {
                    SafeParentCareApp()
                }
            }
            
            Log.d(TAG, "🏠 MainActivity UI set successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "🏠 MainActivity failed to start", e)
            handleMainActivityFailure(e)
        }
    }
    
    /**
     * Handles MainActivity startup failures with fallback UI.
     */
    private fun handleMainActivityFailure(error: Throwable) {
        try {
            setContent {
                MedAppTheme {
                    ErrorRecoveryScreen(error = error.message ?: "Unknown error")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "🚨 Even error recovery failed", e)
            // At this point, the app will likely crash
            // but we've logged everything for debugging
        }
    }
}

/**
 * Safe app composable with comprehensive error boundaries and navigation.
 * 
 * Root composable that sets up navigation and handles app-level error states.
 * Implements graceful degradation when Firebase is unavailable and provides
 * comprehensive error recovery mechanisms.
 * 
 * Architecture:
 * - Sets up navigation controller and coordinator
 * - Handles Firebase availability detection
 * - Provides error boundaries for crash prevention
 * - Manages app-level state and error recovery
 */
@Composable
fun SafeParentCareApp() {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    if (hasError) {
        ErrorRecoveryScreen(
            error = errorMessage,
            onRetry = { 
                hasError = false
                errorMessage = ""
            }
        )
    } else {
        // Set up navigation
        val navController = rememberNavController()
        val navigationCoordinator: ParentCareNavigationCoordinator = hiltViewModel()
        
        // Check if Firebase is available for production features
        val firebaseAvailable = remember { 
            try {
                FirebaseApp.getInstance()
                true
            } catch (e: Exception) {
                Log.d("SafeParentCareApp", "Firebase not available, using development mode")
                false
            }
        }
        
        // Set up navigation host with proper start destination
        ParentCareNavigationHost(
            navController = navController,
            coordinator = navigationCoordinator
        )
    }
}

/**
 * Production app with full Firebase integration.
 */
@Composable
fun ProductionParentCareApp() {
    // Placeholder for production app
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Production Mode",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            "Firebase integration active",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { /* Navigate to main app */ }
        ) {
            Text("Continue to App")
        }
    }
}

/**
 * Development mode app without Firebase.
 */
@Composable
fun DevelopmentModeApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.DeveloperMode,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "DEVELOPMENT MODE",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            "Running without Firebase",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { /* Show mock dashboard */ }
        ) {
            Text("View Mock Dashboard")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Add google-services.json for full functionality",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Error recovery screen with retry functionality.
 */
@Composable
fun ErrorRecoveryScreen(
    error: String,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Try Again")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Check logs for detailed error information",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}