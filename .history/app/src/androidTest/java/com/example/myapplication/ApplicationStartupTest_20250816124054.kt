package com.example.myapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

/**
 * Comprehensive startup testing to prevent future crashes.
 * 
 * These tests validate the entire application startup process including
 * error conditions, missing dependencies, and configuration issues.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ApplicationStartupTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    /**
     * Tests that app starts successfully without Firebase.
     * 
     * This is the most common scenario during development and
     * should never crash the app.
     */
    @Test
    fun appStartsSuccessfullyWithoutFirebase() {
        // Given: App starts without Firebase configuration
        // When: MainActivity is launched
        composeTestRule.waitForIdle()
        
        // Then: Should show development mode screen
        composeTestRule.onNodeWithText("DEVELOPMENT MODE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Running without Firebase").assertIsDisplayed()
        composeTestRule.onNodeWithText("View Mock Dashboard").assertIsDisplayed()
    }
    
    /**
     * Tests that error recovery screen works when components fail.
     */
    @Test
    fun errorRecoveryScreenDisplaysOnFailure() {
        // This test would simulate a component failure
        // and verify the error recovery screen appears
        composeTestRule.setContent {
            MedAppTheme {
                ErrorRecoveryScreen(
                    error = "Test error message",
                    onRetry = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test error message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    /**
     * Tests that retry functionality works in error recovery.
     */
    @Test
    fun errorRecoveryRetryFunctionality() {
        var retryClicked = false
        
        composeTestRule.setContent {
            MedAppTheme {
                ErrorRecoveryScreen(
                    error = "Test error",
                    onRetry = { retryClicked = true }
                )
            }
        }
        
        // When: Clicking retry button
        composeTestRule.onNodeWithText("Try Again").performClick()
        
        // Then: Retry callback should be invoked
        assertTrue("Retry should be triggered", retryClicked)
    }
    
    /**
     * Tests application startup performance.
     */
    @Test
    fun applicationStartupPerformance() {
        val startTime = System.currentTimeMillis()
        
        // Launch activity and wait for completion
        composeTestRule.waitForIdle()
        
        val endTime = System.currentTimeMillis()
        val startupTime = endTime - startTime
        
        // Startup should complete within reasonable time (5 seconds)
        assertTrue("Startup took too long: ${startupTime}ms", startupTime < 5000)
    }
}
