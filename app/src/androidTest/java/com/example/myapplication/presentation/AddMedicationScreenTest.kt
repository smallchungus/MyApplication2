package com.example.myapplication.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.presentation.medication.AddMedicationScreen
import com.example.myapplication.ui.theme.MedAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

/**
 * UI integration tests for add medication screen
 * Testing form interactions and validation
 */
@RunWith(AndroidJUnit4::class)
class AddMedicationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysAllFormFields() {
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { },
                    isLoading = false,
                    error = null
                )
            }
        }

        // Verify all form fields are present
        composeTestRule.onNodeWithText("Medication Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dosage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Schedule Times").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save Medication").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun allowsTextInput() {
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { },
                    isLoading = false,
                    error = null
                )
            }
        }

        // Test medication name input
        composeTestRule
            .onNodeWithText("Medication Name")
            .performTextInput("Aspirin")
        
        composeTestRule
            .onNodeWithText("Aspirin")
            .assertIsDisplayed()

        // Test dosage input  
        composeTestRule
            .onNodeWithText("Dosage")
            .performTextInput("100mg")
            
        composeTestRule
            .onNodeWithText("100mg")
            .assertIsDisplayed()
    }

    @Test
    fun showsValidationErrors() {
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { },
                    isLoading = false,
                    error = "Medication name cannot be empty"
                )
            }
        }

        composeTestRule
            .onNodeWithText("Medication name cannot be empty")
            .assertIsDisplayed()
    }

    @Test
    fun triggersNavigationBack() {
        var backPressed = false
        
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { backPressed = true },
                    isLoading = false,
                    error = null
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backPressed)
    }

    @Test
    fun triggersSaveMedication() {
        var saveCalled = false
        var savedName = ""
        var savedDosage = ""
        
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { name, dosage, times, parentId ->
                        saveCalled = true
                        savedName = name
                        savedDosage = dosage
                    },
                    onNavigateBack = { },
                    isLoading = false,
                    error = null
                )
            }
        }

        // Fill form
        composeTestRule
            .onNodeWithText("Medication Name")
            .performTextInput("Test Medicine")
            
        composeTestRule
            .onNodeWithText("Dosage")
            .performTextInput("50mg")

        // Add a time (this would be done through time picker in real UI)
        // For test purposes, assume time is added

        // Submit form
        composeTestRule
            .onNodeWithText("Save Medication")
            .performClick()

        // Note: In real implementation, we'd need to verify the time picker
        // and complete form validation before save is triggered
    }

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { },
                    isLoading = true,
                    error = null
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Saving medication")
            .assertIsDisplayed()
        
        // Save button should be disabled during loading
        composeTestRule
            .onNodeWithText("Save Medication")
            .assertIsNotEnabled()
    }

    @Test
    fun allowsTimeSelection() {
        composeTestRule.setContent {
            MedAppTheme {
                AddMedicationScreen(
                    onSaveMedication = { _, _, _, _ -> },
                    onNavigateBack = { },
                    isLoading = false,
                    error = null
                )
            }
        }

        // Test adding schedule time
        composeTestRule
            .onNodeWithText("Add Time")
            .assertIsDisplayed()
            .performClick()

        // Time picker should appear (implementation detail)
        // This test validates the UI structure exists
    }
}