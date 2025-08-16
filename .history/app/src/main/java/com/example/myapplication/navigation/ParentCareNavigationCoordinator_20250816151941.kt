@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.navigation.compose.rememberNavController
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
 * 
 * @author ParentCare Navigation Team
 * @since 1.0.0
 */
@HiltViewModel
class ParentCareNavigationCoordinator @Inject constructor() : ViewModel() {
    
    // Navigation event flow for handling navigation actions
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()
    
    // Current destination tracking
    private val _currentDestination = MutableStateFlow<ParentCareDestination>(ParentCareDestination.Auth.Welcome)
    val currentDestination: StateFlow<ParentCareDestination> = _currentDestination.asStateFlow()
    
    // Navigation loading state
    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()
    
    /**
     * Handles navigation events with proper error handling and logging.
     * 
     * @param event Navigation event to process
     */
    fun onNavigationEvent(event: NavigationEvent) {
        viewModelScope.launch {
            try {
                _isNavigating.value = true
                _navigationEvents.emit(event)
                Log.d("Navigation", "Navigation event emitted: $event")
            } catch (e: Exception) {
                Log.e("Navigation", "Failed to emit navigation event", e)
            } finally {
                _isNavigating.value = false
            }
        }
    }
    
    /**
     * Navigates to login screen.
     */
    fun navigateToLogin() {
        onNavigationEvent(NavigationEvent.NavigateToLogin)
        _currentDestination.value = ParentCareDestination.Auth.Login
    }
    
    /**
     * Navigates to family setup.
     */
    fun navigateToFamilySetup() {
        onNavigationEvent(NavigationEvent.NavigateToFamilySetup)
        _currentDestination.value = ParentCareDestination.Auth.FamilySetup
    }
    
    /**
     * Navigates to family dashboard and clears back stack.
     */
    fun navigateToFamilyDashboard() {
        onNavigationEvent(NavigationEvent.NavigateToFamilyDashboard)
        _currentDestination.value = ParentCareDestination.Main.Dashboard
    }
    
    /**
     * Navigates to create account flow.
     */
    fun navigateToCreateAccount() {
        onNavigationEvent(NavigationEvent.NavigateToFamilySetup)
        _currentDestination.value = ParentCareDestination.Auth.FamilySetup
    }
    
    /**
     * Navigates to emergency info screen.
     */
    fun navigateToEmergencyInfo() {
        onNavigationEvent(NavigationEvent.NavigateToEmergencyInfo)
        _currentDestination.value = ParentCareDestination.Main.Emergency
    }
    
    /**
     * Navigates to reports screen.
     */
    fun navigateToReports() {
        onNavigationEvent(NavigationEvent.NavigateToReports)
        _currentDestination.value = ParentCareDestination.Main.Reports
    }
    
    /**
     * Navigates back in the navigation stack.
     */
    fun navigateBack() {
        onNavigationEvent(NavigationEvent.NavigateBack)
    }
}

/**
 * Main navigation host for the ParentCare app.
 * 
 * @param navController Navigation controller
 * @param coordinator Navigation coordinator
 */
@Composable
fun ParentCareNavigationHost(
    navController: NavHostController = rememberNavController(),
    coordinator: ParentCareNavigationCoordinator = hiltViewModel()
) {
    // Handle navigation events
    LaunchedEffect(coordinator) {
        coordinator.navigationEvents.collect { event ->
            when (event) {
                NavigationEvent.NavigateToLogin -> {
                    navController.navigate(ParentCareDestination.Auth.Login.route)
                }
                NavigationEvent.NavigateToFamilySetup -> {
                    navController.navigate(ParentCareDestination.Auth.FamilySetup.route)
                }
                NavigationEvent.NavigateToFamilyDashboard -> {
                    navController.navigate(ParentCareDestination.Main.Dashboard.route) {
                        popUpTo(ParentCareDestination.Auth.Welcome.route) { inclusive = true }
                    }
                }
                NavigationEvent.NavigateToEmergencyInfo -> {
                    navController.navigate(ParentCareDestination.Main.Emergency.route)
                }
                NavigationEvent.NavigateToReports -> {
                    navController.navigate(ParentCareDestination.Main.Reports.route)
                }
                NavigationEvent.NavigateToDevMode -> {
                    navController.navigate(ParentCareDestination.System.DevMode.route)
                }
                NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.destination.route)
                }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = ParentCareDestination.Auth.Login.route
    ) {
        // Auth flow
        composable(ParentCareDestination.Auth.Welcome.route) {
            WelcomeScreen(
                onContinue = { coordinator.navigateToLogin() },
                onDevMode = { 
                    coordinator.onNavigationEvent(NavigationEvent.NavigateToDevMode)
                }
            )
        }
        
        composable(ParentCareDestination.Auth.Login.route) {
            LoginScreen(
                onLoginSuccess = { coordinator.navigateToFamilyDashboard() },
                onCreateAccount = { coordinator.navigateToCreateAccount() }
            )
        }
        
        composable(ParentCareDestination.Auth.FamilySetup.route) {
            FamilySetupScreen(
                onSetupComplete = { coordinator.navigateToFamilyDashboard() },
                onBack = { coordinator.navigateBack() }
            )
        }
        
        // Main app flow
        composable(ParentCareDestination.Main.Dashboard.route) {
            FamilyDashboardScreen(
                onNavigateToEmergency = { coordinator.navigateToEmergencyInfo() },
                onNavigateToReports = { coordinator.navigateToReports() }
            )
        }
        
        composable(ParentCareDestination.Main.Emergency.route) {
            EmergencyInfoScreen(
                onBack = { coordinator.navigateBack() }
            )
        }
        
        composable(ParentCareDestination.Main.Reports.route) {
            ReportsScreen(
                onBack = { coordinator.navigateBack() }
            )
        }
        
        // System screens
        composable(ParentCareDestination.System.DevMode.route) {
            DevelopmentModeScreen(
                onBack = { coordinator.navigateBack() }
            )
        }
    }
}

/**
 * Welcome screen with app introduction.
 */
@Composable
private fun WelcomeScreen(
    onContinue: () -> Unit,
    onDevMode: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ParentCare") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Welcome to ParentCare",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Helping families coordinate senior care",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Continue to App")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onDevMode) {
                Text("Development Mode")
            }
        }
    }
}

/**
 * Login screen for user authentication with working buttons.
 * 
 * Provides Sign In and Create Account functionality with proper validation
 * and navigation to appropriate next screens based on user selection.
 * 
 * Design Decisions:
 * - Sign In button navigates existing users directly to dashboard
 * - Create Account button takes new users through family setup
 * - Basic form validation prevents empty submissions
 * - Clear visual feedback for button actions
 * 
 * @param onLoginSuccess Callback for successful login navigation
 * @param onCreateAccount Callback for new account creation flow
 */
@Composable
private fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome Back") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App branding
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "ParentCare",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Family medication coordination",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign In Button - for existing users
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        // Simulate login process
                        onLoginSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Create Account Button - for new users
            OutlinedButton(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading
            ) {
                Text("Create Account")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(
                onClick = { /* TODO: Forgot password */ },
                enabled = !isLoading
            ) {
                Text("Forgot Password?")
            }
        }
    }
}

/**
 * Family setup screen for new user onboarding.
 * 
 * Collects essential family information for new accounts and provides
 * options to complete setup or skip for later configuration.
 * 
 * Design Decisions:
 * - Minimal required fields to reduce friction
 * - Skip option for users who want to explore first
 * - Clear progress indication for multi-step setup
 * - Validation to ensure meaningful family names
 * 
 * @param onSetupComplete Callback when user completes full setup
 * @param onSkip Callback when user chooses to skip setup
 */
@Composable
private fun FamilySetupScreen(
    onSetupComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    var seniorName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Your Family") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Welcome to ParentCare",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Let's set up your family care network",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = familyName,
                onValueChange = { familyName = it },
                label = { Text("Family Name") },
                placeholder = { Text("The Smith Family") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = seniorName,
                onValueChange = { seniorName = it },
                label = { Text("Senior's Name") },
                placeholder = { Text("Who needs medication help?") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Complete Setup Button
            Button(
                onClick = {
                    if (familyName.isNotBlank() && seniorName.isNotBlank()) {
                        isLoading = true
                        onSetupComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && familyName.isNotBlank() && seniorName.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Setup")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Skip for Now Button
            TextButton(
                onClick = onSkip,
                enabled = !isLoading
            ) {
                Text("Skip for Now")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "You can always update this information later in settings",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Emergency information screen.
 */
@Composable
private fun EmergencyInfoScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Info") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Emergency Contacts",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Doctor: Dr. Smith - (555) 123-4567")
                        Text("Pharmacy: Main Pharmacy - (555) 987-6543")
                        Text("Emergency: 911")
                    }
                }
            }
        }
    }
}

/**
 * Reports screen showing medication history.
 */
@Composable
private fun ReportsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Report") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "This Week's Progress",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Medications taken: 12/14")
                        Text("Missed doses: 2")
                        Text("On-time rate: 85.7%")
                    }
                }
            }
        }
    }
}

/**
 * Development mode screen for testing.
 */
@Composable
private fun DevelopmentModeScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Development Mode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Development Mode",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("This is development mode for testing UI components.")
        }
    }
}
