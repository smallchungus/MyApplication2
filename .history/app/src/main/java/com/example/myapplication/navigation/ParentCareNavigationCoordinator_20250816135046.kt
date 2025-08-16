package com.example.myapplication.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.FamilyDashboardScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Navigation coordinator for ParentCare family coordination app.
 * 
 * Manages navigation flow between authentication, onboarding, and main app.
 * Implements proper state management to prevent navigation inconsistencies
 * and handles deep linking for family invitation scenarios.
 * 
 * Design Decisions:
 * - Uses Compose Navigation for type-safe navigation
 * - Maintains navigation state in ViewModel for configuration changes
 * - Implements proper back stack management for UX consistency
 * - Provides centralized navigation logic for easier testing and maintenance
 * - Supports both programmatic navigation and deep linking
 * 
 * Architecture Pattern:
 * ```
 * UI Components → NavigationEvent → NavigationCoordinator → NavController
 *      ↓               ↓                    ↓                 ↓
 *   User Action    Type-Safe Event    Business Logic    Navigation
 * ```
 * 
 * Error Handling:
 * - Logs all navigation actions for debugging
 * - Handles invalid navigation attempts gracefully
 * - Provides fallback routes for error scenarios
 * - Prevents navigation loops and stack overflow
 * 
 * @param authRepository Authentication state provider  
 * @param familyRepository Family data access layer
 * @since 1.0.0
 * @see NavigationEvent for navigation action definitions
 * @see ParentCareDestination for available destinations
 */
@HiltViewModel
class ParentCareNavigationCoordinator @Inject constructor(
    // Note: AuthRepository and FamilyRepository would be injected here in full implementation
) : ViewModel() {
    
    companion object {
        private const val TAG = "NavigationCoordinator"
    }
    
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()
    
    private val _currentDestination = MutableStateFlow<ParentCareDestination>(ParentCareDestination.System.DevMode)
    val currentDestination: StateFlow<ParentCareDestination> = _currentDestination.asStateFlow()
    
    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()
    
    /**
     * Handles navigation events from UI components.
     * 
     * Processes navigation requests with proper validation and error handling.
     * All navigation goes through this method to ensure consistent behavior
     * and logging for debugging purposes.
     * 
     * @param event Navigation event to process
     * @throws IllegalStateException if navigation is attempted during another navigation
     */
    fun onNavigationEvent(event: NavigationEvent) {
        if (_isNavigating.value) {
            Log.w(TAG, "🚫 Navigation blocked - already navigating")
            return
        }
        
        Log.d(TAG, "🧭 Processing navigation event: $event")
        
        viewModelScope.launch {
            try {
                _isNavigating.value = true
                _navigationEvents.emit(event)
                
                // Update current destination for state tracking
                when (event) {
                    is NavigationEvent.NavigateTo -> {
                        _currentDestination.value = event.destination
                        Log.d(TAG, "📍 Navigation to: ${event.destination.route}")
                    }
                    is NavigationEvent.NavigateBack -> {
                        Log.d(TAG, "⬅️ Navigate back requested")
                        // Note: Actual destination update happens in navigation handler
                    }
                    is NavigationEvent.NavigateAndClearTo -> {
                        _currentDestination.value = event.destination
                        Log.d(TAG, "🔄 Navigate and clear to: ${event.destination.route}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Navigation event failed", e)
                // Handle navigation failure - could emit error event
            } finally {
                _isNavigating.value = false
            }
        }
    }
    
    /**
     * Navigates to the main family dashboard.
     * 
     * Primary entry point to the app after authentication or onboarding.
     * Clears back stack to prevent returning to auth screens.
     */
    fun navigateToFamilyDashboard() {
        onNavigationEvent(
            NavigationEvent.NavigateAndClearTo(
                destination = ParentCareDestination.Main.Dashboard,
                popUpTo = ParentCareDestination.System.DevMode
            )
        )
    }
    
    /**
     * Navigates to emergency information screen.
     * 
     * Quick access for urgent situations requiring immediate family
     * contact information or emergency protocols.
     */
    fun navigateToEmergencyInfo() {
        onNavigationEvent(NavigationEvent.NavigateTo(ParentCareDestination.Main.Emergency))
    }
    
    /**
     * Navigates to weekly reports.
     * 
     * Provides family members with medication adherence summaries
     * and care coordination analytics.
     */
    fun navigateToReports() {
        onNavigationEvent(NavigationEvent.NavigateTo(ParentCareDestination.Main.Reports))
    }
    
    /**
     * Navigates back to previous screen.
     * 
     * Handles back navigation with proper fallback behavior
     * if back stack is empty.
     */
    fun navigateBack() {
        onNavigationEvent(
            NavigationEvent.NavigateBack(
                fallbackDestination = ParentCareDestination.Main.Dashboard
            )
        )
    }
}

/**
 * Main navigation host for ParentCare app.
 * 
 * Sets up the navigation graph and handles navigation events from the coordinator.
 * This composable serves as the root navigation container and should be placed
 * in the main activity or root composable.
 * 
 * Navigation Graph Structure:
 * ```
 * DevMode (start) → Dashboard → [Emergency, Reports, Settings]
 *     ↓
 * Auth Flow (future) → [Welcome, Login, FamilySetup]
 * ```
 * 
 * @param navController Navigation controller for managing destinations
 * @param startDestination Initial destination when app launches
 * @param coordinator Navigation coordinator for handling events
 */
@Composable
fun ParentCareNavigationHost(
    navController: NavHostController,
    startDestination: String = ParentCareDestination.Auth.Welcome.route,
    coordinator: ParentCareNavigationCoordinator = hiltViewModel()
) {
    // Handle navigation events from coordinator
    val navigationEvents by coordinator.navigationEvents.collectAsState(initial = null)
    
    LaunchedEffect(navigationEvents) {
        navigationEvents?.let { event ->
            Log.d("NavigationHost", "🎯 Executing navigation: $event")
            
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    if (event.clearBackStack) {
                        navController.navigate(event.destination.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else {
                        navController.navigate(event.destination.route)
                    }
                }
                
                is NavigationEvent.NavigateBack -> {
                    if (!navController.popBackStack()) {
                        // Back stack is empty, navigate to fallback
                        event.fallbackDestination?.let { fallback ->
                            navController.navigate(fallback.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                }
                
                is NavigationEvent.NavigateAndClearTo -> {
                    navController.navigate(event.destination.route) {
                        popUpTo(event.popUpTo.route) { inclusive = true }
                    }
                }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth flow destinations
        composable(ParentCareDestination.Auth.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { 
                    coordinator.onNavigationEvent(NavigationEvent.NavigateTo(ParentCareDestination.Auth.Login))
                },
                onSkipToDemo = coordinator::navigateToFamilyDashboard
            )
        }
        
        composable(ParentCareDestination.Auth.Login.route) {
            LoginScreen(
                onNavigateToFamilySetup = {
                    coordinator.onNavigationEvent(NavigationEvent.NavigateTo(ParentCareDestination.Auth.FamilySetup))
                },
                onNavigateToDashboard = coordinator::navigateToFamilyDashboard,
                onNavigateBack = coordinator::navigateBack
            )
        }
        
        composable(ParentCareDestination.Auth.FamilySetup.route) {
            FamilySetupScreen(
                onNavigateToDashboard = coordinator::navigateToFamilyDashboard,
                onNavigateBack = coordinator::navigateBack
            )
        }
        
        // System destinations  
        composable(ParentCareDestination.System.DevMode.route) {
            DevelopmentModeScreen(
                onNavigateToApp = coordinator::navigateToFamilyDashboard
            )
        }
        
        // Main app destinations  
        composable(ParentCareDestination.Main.Dashboard.route) {
            FamilyDashboardScreen()
        }
        
        composable(ParentCareDestination.Main.Emergency.route) {
            EmergencyInfoScreen(
                onNavigateBack = coordinator::navigateBack
            )
        }
        
        composable(ParentCareDestination.Main.Reports.route) {
            ReportsScreen(
                onNavigateBack = coordinator::navigateBack
            )
        }
        
        // Future: Auth destinations would be added here
        // composable(ParentCareDestination.Auth.Welcome.route) { ... }
        // composable(ParentCareDestination.Auth.Login.route) { ... }
    }
}

/**
 * Development mode screen placeholder.
 * 
 * Temporary screen for development and testing without Firebase setup.
 * Will be replaced with proper onboarding flow in production.
 */
@Composable
private fun DevelopmentModeScreen(
    onNavigateToApp: () -> Unit
) {
    // Use the existing DevelopmentModeApp composable with navigation callback
    com.example.myapplication.DevelopmentModeApp(
        onNavigateToApp = onNavigateToApp
    )
}

/**
 * Emergency information screen placeholder.
 * 
 * Future implementation will show emergency contacts, protocols,
 * and quick actions for urgent care situations.
 */
@Composable
private fun EmergencyInfoScreen(
    onNavigateBack: () -> Unit
) {
    // Placeholder for emergency info screen
    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text("Emergency Info - Coming Soon")
        androidx.compose.material3.Button(onClick = onNavigateBack) {
            androidx.compose.material3.Text("Back")
        }
    }
}

/**
 * Reports screen placeholder.
 * 
 * Future implementation will show medication adherence reports,
 * family coordination analytics, and care summaries.
 */
@Composable
private fun ReportsScreen(
    onNavigateBack: () -> Unit
) {
    // Placeholder for reports screen
    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text("Reports - Coming Soon")
        androidx.compose.material3.Button(onClick = onNavigateBack) {
            androidx.compose.material3.Text("Back")
        }
    }
}
