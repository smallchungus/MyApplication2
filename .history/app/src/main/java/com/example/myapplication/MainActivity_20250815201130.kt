package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.ui.theme.MedAppTheme

/**
 * Main Activity - Application Entry Point
 * 
 * This activity serves as the main entry point for the medication reminder application.
 * It sets up the application theme and displays the primary TodayScreen interface.
 * 
 * **Application Architecture**:
 * - Single activity design following modern Android development practices
 * - Edge-to-edge display for immersive user experience
 * - Material 3 theming for consistent design language
 * 
 * **User Experience**:
 * - Immediate access to today's medication schedule
 * - Professional medical app appearance
 * - Accessibility-first design principles
 * 
 * @author Medication Reminder App Team
 * @since 1.0.0
 * @see [TodayScreen] for main application interface
 * @see [MedAppTheme] for application theming
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedAppTheme {
                TodayScreen()  // This displays the full medication interface!
            }
        }
    }
}