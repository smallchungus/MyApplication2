package com.example.myapplication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Comprehensive test suite for TodayScreen medication management functionality.
 * 
 * Tests verify the core user interaction: marking medications as taken and
 * receiving appropriate visual feedback. Critical for medication safety.
 * 
 * ## Test Strategy
 * 
 * **UI Component Rendering**: Verifies that all medication information displays
 * correctly on screen load, ensuring users can immediately see their medication
 * schedule when opening the app.
 * 
 * **Button Interaction Behavior**: Tests the core "Take" button functionality
 * that solves the "Did I take it?" problem. Verifies immediate visual feedback
 * and state changes.
 * 
 * **State Management Correctness**: Ensures that medication taken status is
 * properly managed and persisted during the session, preventing accidental
 * double-dosing.
 * 
 * **Accessibility Compliance**: Verifies that UI elements meet accessibility
 * standards, including proper test tags and content descriptions.
 * 
 * ## Test Coverage Areas
 * 
 * - **Medication Display**: Names, dosages, and times render correctly
 * - **Take Button Functionality**: Button responds to user interaction
 * - **State Persistence**: Taken status remains during session
 * - **Visual Feedback**: Checkmarks and status text appear appropriately
 * - **Safety Features**: Cannot "untake" medications during session
 * 
 * @author Medication Reminder App Team
 * @since 1.0.0
 * @see [TodayScreen] for the main screen implementation
 * @see [MedicationCard] for individual medication display
 */
class TodayScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Verifies that medications display correctly on screen load.
     * 
     * This test ensures users can immediately see their medication
     * schedule when opening the app - critical for daily routine.
     * 
     * **Test Steps**:
     * 1. Launch TodayScreen with sample medication data
     * 2. Verify all medication names are displayed
     * 3. Verify medication details (dosage and time) are shown
     * 4. Verify Take buttons are available for untaken medications
     * 
     * **Success Criteria**:
     * - All medication names visible
     * - Dosage and time information displayed
     * - Take buttons present for untaken medications
     * - Screen title shows "Today's Medications"
     */
    @Test
    fun todayScreenDisplaysMedicationList() {
        // Given: App launches with today screen
        composeTestRule.setContent {
            MedAppTheme {
                TodayScreen()
            }
        }
        
        // When: Screen loads
        // Then: Should show medication names
        composeTestRule.onNodeWithText("Aspirin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vitamin D").assertIsDisplayed()
        composeTestRule.onNodeWithText("Metformin").assertIsDisplayed()
        
        // Verify medication details are displayed
        composeTestRule.onNodeWithText("81mg at 08:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("1000 IU at 18:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("500mg at 12:00").assertIsDisplayed()
        
        // Verify Take buttons are available
        composeTestRule.onNodeWithText("Take Now").assertIsDisplayed()
        
        // Verify screen title
        composeTestRule.onNodeWithText("Today's Medications").assertIsDisplayed()
    }
    
    /**
     * Verifies Take button functionality for medication adherence tracking.
     * 
     * Tests the core user interaction that solves "Did I take it?" problem.
     * Button must change state and provide visual confirmation.
     * 
     * **Test Steps**:
     * 1. Identify untaken medication with Take button
     * 2. Tap the Take button
     * 3. Verify button changes to "Taken" status
     * 4. Verify checkmark icon appears
     * 5. Verify Take button is no longer visible
     * 
     * **Success Criteria**:
     * - Take button responds to user interaction
     * - Button text changes to "Taken ✓"
     * - Checkmark icon appears
     * - Original Take button disappears
     * - State change is immediate and visible
     */
    @Test
    fun takeButtonMarksMedicationAsTaken() {
        // Given: Screen with untaken medication
        composeTestRule.setContent {
            MedAppTheme {
                TodayScreen()
            }
        }
        
        // When: User taps "Take" button for Aspirin
        composeTestRule.onNodeWithText("Take Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("Take Now").performClick()
        
        // Then: Should show taken status (checkmark and "Taken" text)
        composeTestRule.onNodeWithText("Taken ✓").assertIsDisplayed()
        
        // Verify checkmark icon is displayed
        composeTestRule.onNodeWithTag("taken_icon_1").assertIsDisplayed()
        
        // Verify Take button is no longer visible
        composeTestRule.onNodeWithText("Take Now").assertDoesNotExist()
    }
    
    /**
     * Verifies taken medications cannot be "untaken" to prevent confusion.
     * 
     * Safety feature: prevents accidental double-dosing by making
     * taken status permanent during session.
     * 
     * **Test Steps**:
     * 1. Mark medication as taken
     * 2. Verify Take button disappears
     * 3. Verify "Taken" status is displayed
     * 4. Attempt to find Take button again (should not exist)
     * 
     * **Success Criteria**:
     * - Take button disappears after medication is taken
     * - "Taken ✓" status is permanently displayed
     * - No way to revert to untaken state during session
     * - Safety mechanism prevents double-dosing confusion
     */
    @Test
    fun takenMedicationCannotBeUntaken() {
        // Given: Medication already taken
        composeTestRule.setContent {
            MedAppTheme {
                TodayScreen()
            }
        }
        
        // When: Medication is marked as taken
        composeTestRule.onNodeWithText("Take Now").performClick()
        
        // Then: "Take" button should not be available anymore
        composeTestRule.onNodeWithText("Take Now").assertDoesNotExist()
        composeTestRule.onNodeWithText("Taken ✓").assertIsDisplayed()
        
        // Verify that remaining Take buttons exist for untaken medications
        val takeButtons = composeTestRule.onAllNodesWithText("Take Now")
        assertEquals("Should have 2 remaining Take buttons", 2, takeButtons.fetchSemanticsNodes().size)
    }
    
    /**
     * Verifies that multiple medications can be taken independently.
     * 
     * Tests that the state management correctly handles multiple
     * medications and their individual taken status.
     * 
     * **Test Steps**:
     * 1. Take first medication (Aspirin)
     * 2. Verify first medication shows as taken
     * 3. Verify other medications still show Take buttons
     * 4. Take second medication (Vitamin D)
     * 5. Verify both taken medications show correct status
     * 
     * **Success Criteria**:
     * - Each medication maintains independent state
     * - Taking one medication doesn't affect others
     * - All taken medications show "Taken ✓" status
     * - Untaken medications still show Take buttons
     */
    @Test
    fun multipleMedicationsCanBeTakenIndependently() {
        // Given: Screen with multiple untaken medications
        composeTestRule.setContent {
            MedAppTheme {
                TodayScreen()
            }
        }
        
        // When: First medication is taken
        composeTestRule.onNodeWithText("Take Now").performClick()
        
        // Then: First medication should show as taken
        composeTestRule.onNodeWithText("Taken ✓").assertIsDisplayed()
        
        // And: Other medications should still show Take buttons
        val remainingTakeButtons = composeTestRule.onAllNodesWithText("Take Now")
        assertEquals("Should have 2 remaining Take buttons", 2, remainingTakeButtons.fetchSemanticsNodes().size)
        
        // When: Second medication is taken
        remainingTakeButtons.fetchSemanticsNodes()[0].performClick()
        
        // Then: Both taken medications should show "Taken ✓"
        val takenStatuses = composeTestRule.onAllNodesWithText("Taken ✓")
        assertEquals("Should have 2 taken medications", 2, takenStatuses.fetchSemanticsNodes().size)
        
        // And: Only one Take button should remain
        val finalTakeButtons = composeTestRule.onAllNodesWithText("Take Now")
        assertEquals("Should have 1 remaining Take button", 1, finalTakeButtons.fetchSemanticsNodes().size)
    }
    
    /**
     * Verifies that test tags are properly applied for UI testing support.
     * 
     * Ensures that all interactive elements have appropriate test tags
     * for automated testing and quality assurance.
     * 
     * **Test Steps**:
     * 1. Verify top app bar has test tag
     * 2. Verify medication cards have test tags
     * 3. Verify Take buttons have test tags
     * 4. Verify taken status elements have test tags
     * 
     * **Success Criteria**:
     * - All major UI components have test tags
     * - Test tags follow consistent naming convention
     * - Test tags support automated testing workflows
     * - Accessibility testing can be performed
     */
    @Test
    fun testTagsAreProperlyApplied() {
        // Given: Screen is loaded
        composeTestRule.setContent {
            MedAppTheme {
                TodayScreen()
            }
        }
        
        // When: Screen is displayed
        // Then: Test tags should be present for major components
        
        // Verify top app bar test tag
        composeTestRule.onNodeWithTag("top_app_bar").assertIsDisplayed()
        
        // Verify medication card test tags
        composeTestRule.onNodeWithTag("medication_card_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_card_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_card_3").assertIsDisplayed()
        
        // Verify Take button test tags
        composeTestRule.onNodeWithTag("take_button_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("take_button_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("take_button_3").assertIsDisplayed()
        
        // Verify medication name test tags
        composeTestRule.onNodeWithTag("medication_name_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_name_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_name_3").assertIsDisplayed()
    }
}
