package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Development mode app that displays when Firebase isn't configured.
 * 
 * This provides a fallback UI for development and testing without requiring
 * a real Firebase project setup. Shows mock family dashboard for UI development.
 * 
 * @since 1.0.0
 */
@Composable
fun DevelopmentModeApp() {
    var showMockDashboard by remember { mutableStateOf(false) }
    
    if (showMockDashboard) {
        FamilyDashboardScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "DEVELOPMENT MODE",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Firebase not configured. Add google-services.json for full functionality.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { showMockDashboard = true }
            ) {
                Text("View Mock Dashboard")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Could open Firebase setup guide */ }
            ) {
                Text("Setup Firebase")
            }
        }
    }
}
