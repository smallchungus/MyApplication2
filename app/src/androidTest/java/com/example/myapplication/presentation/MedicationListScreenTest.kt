package com.example.myapplication.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.domain.model.Medication
import com.example.myapplication.domain.model.MedicationSchedule
import com.example.myapplication.domain.model.ScheduleFrequency
import com.example.myapplication.presentation.medication.MedicationListScreen
import com.example.myapplication.presentation.medication.MedicationUiState
import com.example.myapplication.ui.theme.MedAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI integration tests for medication list screen
 * Testing user interactions and UI state rendering
 */
@RunWith(AndroidJUnit4::class)
class MedicationListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysEmptyStateWhenNoMedications() {
        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = emptyList(),
                        isLoading = false,
                        error = null
                    ),
                    onLoadMedications = { },
                    onAddMedication = { },
                    onNavigateToAdd = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("No medications yet")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add your first medication to get started")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Add medication")
            .assertIsDisplayed()
    }

    @Test
    fun displaysMedicationList() {
        val testMedications = listOf(
            createTestMedication("1", "Aspirin", "100mg", "8:00 AM"),
            createTestMedication("2", "Vitamin D", "1000 IU", "12:00 PM")
        )

        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = testMedications,
                        isLoading = false,
                        error = null
                    ),
                    onLoadMedications = { },
                    onAddMedication = { },
                    onNavigateToAdd = { }
                )
            }
        }

        // Verify both medications are displayed
        composeTestRule.onNodeWithText("Aspirin").assertIsDisplayed()
        composeTestRule.onNodeWithText("100mg").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vitamin D").assertIsDisplayed()
        composeTestRule.onNodeWithText("1000 IU").assertIsDisplayed()
    }

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = emptyList(),
                        isLoading = true,
                        error = null
                    ),
                    onLoadMedications = { },
                    onAddMedication = { },
                    onNavigateToAdd = { }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Loading medications")
            .assertIsDisplayed()
    }

    @Test
    fun showsErrorState() {
        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = emptyList(),
                        isLoading = false,
                        error = "Failed to load medications"
                    ),
                    onLoadMedications = { },
                    onAddMedication = { },
                    onNavigateToAdd = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Failed to load medications")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Try Again")
            .assertIsDisplayed()
    }

    @Test
    fun triggersAddMedicationNavigation() {
        var addClicked = false
        
        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = emptyList(),
                        isLoading = false,
                        error = null
                    ),
                    onLoadMedications = { },
                    onAddMedication = { },
                    onNavigateToAdd = { addClicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Add medication")
            .performClick()

        assert(addClicked)
    }

    @Test
    fun triggersRetryOnError() {
        var retryClicked = false
        
        composeTestRule.setContent {
            MedAppTheme {
                MedicationListScreen(
                    uiState = MedicationUiState(
                        medications = emptyList(),
                        isLoading = false,
                        error = "Network error"
                    ),
                    onLoadMedications = { retryClicked = true },
                    onAddMedication = { },
                    onNavigateToAdd = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Try Again")
            .performClick()

        assert(retryClicked)
    }

    private fun createTestMedication(
        id: String,
        name: String,
        dosage: String,
        timeDisplay: String
    ): Medication {
        return Medication(
            id = id,
            name = name,
            dosage = dosage,
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = listOf(LocalTime.of(8, 0)),
                startDate = LocalDate.now()
            ),
            parentId = "parent-123"
        )
    }
}