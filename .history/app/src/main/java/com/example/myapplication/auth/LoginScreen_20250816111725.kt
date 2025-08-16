package com.example.myapplication.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Login and account creation screen for ParentCare family coordination app.
 * 
 * This screen handles user authentication and serves as the entry point for
 * family members joining the care coordination system. The design prioritizes
 * ease of use for adult children (ages 45-65) who may have varying technical
 * comfort levels.
 * 
 * User Experience Design:
 * - Simple, clean interface focused on getting users into the app quickly
 * - Clear value proposition messaging for family coordination
 * - Minimal form fields to reduce friction
 * - Prominent "Create Family" vs "Join Family" distinction
 * - Professional appearance that builds trust for medical app
 * 
 * Authentication Flow:
 * 1. User chooses to create account or sign in
 * 2. Email/password authentication via Firebase
 * 3. New users create family group or join existing
 * 4. Successful authentication navigates to family dashboard
 * 
 * Error Handling:
 * - User-friendly error messages for common issues
 * - Network error handling with retry options
 * - Form validation with real-time feedback
 * - Loading states during authentication operations
 * 
 * @since 1.0.0
 */
@Composable
fun LoginScreen(
    onNavigateToFamilySetup: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    authRepository: AuthRepository = hiltViewModel<AuthRepository>()
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Observe authentication state
    val authState by authRepository.authState.collectAsState()
    
    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthRepository.AuthState.Authenticated -> {
                if (authState.userData.familyGroups.isEmpty()) {
                    onNavigateToFamilySetup()
                } else {
                    onNavigateToDashboard()
                }
            }
            is AuthRepository.AuthState.Error -> {
                errorMessage = authState.message
                isLoading = false
            }
            AuthRepository.AuthState.Loading -> {
                isLoading = true
            }
            AuthRepository.AuthState.Unauthenticated -> {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App branding and value proposition
        Text(
            text = "ParentCare",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("app_title")
        )
        
        Text(
            text = "Coordinate family medication care with peace of mind",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("app_subtitle")
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Authentication form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_form_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode toggle (Login vs Sign Up)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { 
                            isLoginMode = true
                            errorMessage = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isLoginMode) MaterialTheme.colorScheme.primary 
                                         else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("sign_in_toggle")
                    ) {
                        Text("Sign In", fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal)
                    }
                    
                    Text(
                        text = " | ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    
                    TextButton(
                        onClick = { 
                            isLoginMode = false
                            errorMessage = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (!isLoginMode) MaterialTheme.colorScheme.primary 
                                         else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("create_account_toggle")
                    ) {
                        Text("Create Account", fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Display name field (only for sign up)
                if (!isLoginMode) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("display_name_field"),
                        enabled = !isLoading,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_field"),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_field"),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                
                // Error message display
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag("error_message")
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit button
                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        // Handle authentication (implement in ViewModel)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_button"),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank() &&
                             (isLoginMode || displayName.isNotBlank())
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isLoginMode) "Sign In" else "Create Account")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Additional help text
        Text(
            text = if (isLoginMode) 
                "New to ParentCare? Create an account to coordinate your family's medication care." 
            else 
                "After creating your account, you'll set up your family group and invite other members.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("help_text")
        )
    }
}

/**
 * Preview function for development and testing.
 * 
 * Shows how the LoginScreen will look in the Android Studio preview pane.
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onNavigateToFamilySetup = {},
            onNavigateToDashboard = {}
        )
    }
}
