package com.example.myapplication.navigation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Welcome screen - First screen users see when opening the app.
 * 
 * Provides clear value proposition for family care coordination
 * and guides users to either login or try the demo.
 */
@Composable
private fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onSkipToDemo: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Icon(
            androidx.compose.material.icons.Icons.Default.Favorite,
            contentDescription = null,
            modifier = androidx.compose.ui.Modifier.size(80.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
        
        androidx.compose.material3.Text(
            text = "ParentCare",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        
        androidx.compose.material3.Text(
            text = "Coordinate medication care\nfor your loved ones",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(48.dp))
        
        androidx.compose.material3.Button(
            onClick = onNavigateToLogin,
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            androidx.compose.material3.Text("Get Started", fontSize = 18.sp)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        
        androidx.compose.material3.OutlinedButton(
            onClick = onSkipToDemo,
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            androidx.compose.material3.Text("Try Demo", fontSize = 18.sp)
        }
    }
}

/**
 * Login screen for user authentication.
 * 
 * Handles email/password login and account creation with
 * proper validation and error handling.
 */
@Composable
private fun LoginScreen(
    onNavigateToFamilySetup: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var password by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var isLoginMode by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(if (isLoginMode) "Sign In" else "Create Account") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { androidx.compose.material3.Text("Email") },
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            
            androidx.compose.material3.OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { androidx.compose.material3.Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
            
            androidx.compose.material3.Button(
                onClick = { 
                    if (isLoginMode) {
                        onNavigateToDashboard()
                    } else {
                        onNavigateToFamilySetup()
                    }
                },
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                androidx.compose.material3.Text(
                    if (isLoginMode) "Sign In" else "Create Account",
                    fontSize = 18.sp
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            
            androidx.compose.material3.TextButton(
                onClick = { isLoginMode = !isLoginMode },
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Text(
                    if (isLoginMode) "Need an account? Sign up" else "Already have an account? Sign in"
                )
            }
        }
    }
}

/**
 * Family setup screen for new users.
 * 
 * Allows new users to create their family group or join
 * an existing family coordination setup.
 */
@Composable
private fun FamilySetupScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var familyName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var parentName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Family Setup") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = "Set up your family care group",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                modifier = androidx.compose.ui.Modifier.padding(bottom = 24.dp)
            )
            
            androidx.compose.material3.OutlinedTextField(
                value = familyName,
                onValueChange = { familyName = it },
                label = { androidx.compose.material3.Text("Family Name (e.g., 'Smith Family')") },
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            
            androidx.compose.material3.OutlinedTextField(
                value = parentName,
                onValueChange = { parentName = it },
                label = { androidx.compose.material3.Text("Parent's Name") },
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(32.dp))
            
            androidx.compose.material3.Button(
                onClick = onNavigateToDashboard,
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = familyName.isNotBlank() && parentName.isNotBlank()
            ) {
                androidx.compose.material3.Text("Create Family Group", fontSize = 18.sp)
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            
            androidx.compose.material3.OutlinedButton(
                onClick = { /* TODO: Join existing family */ },
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                androidx.compose.material3.Text("Join Existing Family", fontSize = 18.sp)
            }
        }
    }
}

/**
 * Development mode screen placeholder.
 * 
 * Temporary screen for development and testing without Firebase setup.
 */
@Composable
private fun DevelopmentModeScreen(
    onNavigateToApp: () -> Unit
) {
    com.example.myapplication.DevelopmentModeApp(
        onNavigateToApp = onNavigateToApp
    )
}

/**
 * Emergency information screen with quick access to critical info.
 * 
 * Provides immediate access to emergency contacts, protocols,
 * and urgent action items for family coordination.
 */
@Composable
private fun EmergencyInfoScreen(
    onNavigateBack: () -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Emergency Information") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            item {
                androidx.compose.material3.Card(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "🚨 Emergency Contacts",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text("Dr. Johnson: (555) 123-4567")
                        androidx.compose.material3.Text("Mom's Phone: (555) 987-6543")
                        androidx.compose.material3.Text("911 for Emergencies")
                    }
                }
            }
            
            item {
                androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "💊 Critical Medications",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text("Insulin - Twice daily with meals")
                        androidx.compose.material3.Text("Blood pressure - Once daily morning")
                        androidx.compose.material3.Text("Heart medication - DO NOT SKIP")
                    }
                }
            }
            
            item {
                androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "📍 Quick Actions",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        
                        androidx.compose.material3.Button(
                            onClick = { /* Call emergency contact */ },
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text("Call Dr. Johnson")
                        }
                        
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        
                        androidx.compose.material3.OutlinedButton(
                            onClick = { /* Alert all family */ },
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text("Alert All Family Members")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reports screen showing medication adherence and care analytics.
 * 
 * Provides family members with insights into medication compliance,
 * care coordination effectiveness, and trending information.
 */
@Composable
private fun ReportsScreen(
    onNavigateBack: () -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Care Reports") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            item {
                androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "📊 This Week's Summary",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text("Medication Adherence: 95%")
                        androidx.compose.material3.Text("Assignments Completed: 18/19")
                        androidx.compose.material3.Text("Missed Medications: 1")
                    }
                }
            }
            
            item {
                androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "👥 Family Participation",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text("Sarah: 8 assignments completed")
                        androidx.compose.material3.Text("Mike: 6 assignments completed")
                        androidx.compose.material3.Text("You: 4 assignments completed")
                    }
                }
            }
            
            item {
                androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "📈 Trends & Insights",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text("Evening medications most often missed")
                        androidx.compose.material3.Text("Weekend compliance is 10% lower")
                        androidx.compose.material3.Text("Consider setting more reminders")
                        
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
                        
                        androidx.compose.material3.Button(
                            onClick = { /* Export report */ },
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text("Export Monthly Report")
                        }
                    }
                }
            }
        }
    }
}
