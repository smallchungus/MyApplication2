package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.ui.theme.MedAppTheme
import com.example.myapplication.FamilyDashboardScreen

/**
 * Main Activity - Family Coordination Application Entry Point
 *
 * This activity serves as the main entry point for the family coordination
 * application. It sets up the application theme and displays the primary
 * FamilyDashboardScreen interface for coordinating medication care.
 *
 * **Application Architecture**:
 * - Single activity design following modern Android development practices
 * - Edge-to-edge display for immersive user experience
 * - Material 3 theming for consistent design language
 * - Family-first design for adult children coordinating parent care
 *
 * **User Experience**:
 * - Immediate access to family medication assignments
 * - Professional medical app appearance suitable for adult children
 * - Clear status visibility for family coordination
 * - Emergency-ready interface for critical situations
 *
 * **Family Coordination Focus**:
 * - Built for adult children as primary users
 * - Assignment-based medication tracking
 * - Real-time status updates for family members
 * - Critical alert system for missed medications
 *
 * @author Family Coordination App Team
 * @since 1.0.0
 * @see [FamilyDashboardScreen] for main application interface
 * @see [MedAppTheme] for application theming
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedAppTheme {
                FamilyDashboardScreen()  // Family coordination dashboard!
            }
        }
    }
}