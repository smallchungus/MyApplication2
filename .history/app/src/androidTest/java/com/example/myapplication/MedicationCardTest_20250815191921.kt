package com.example.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MedicationCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun medicationCard_displaysAllRequiredElements() {
        // Arrange
        val medication = Medication(
            id = 1,
            name = "Test Medication",
            dosage = "50mg",
            timeToTake = "Morning",
            isTaken = false
        )
        var clickCount = 0

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = { clickCount++ }
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("medication_card_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_name_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dosage_label_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dosage_value_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("time_icon_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("time_value_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("take_button_1").assertIsDisplayed()
    }

    @Test
    fun medicationCard_displaysCorrectMedicationData() {
        // Arrange
        val medication = Medication(
            id = 2,
            name = "Aspirin",
            dosage = "100mg",
            timeToTake = "Evening",
            isTaken = false
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Aspirin").assertIsDisplayed()
        composeTestRule.onNodeWithText("100mg").assertIsDisplayed()
        composeTestRule.onNodeWithText("Evening").assertIsDisplayed()
        composeTestRule.onNodeWithText("Take").assertIsDisplayed()
    }

    @Test
    fun medicationCard_takeButtonIsEnabled_whenMedicationNotTaken() {
        // Arrange
        val medication = Medication(
            id = 3,
            name = "Vitamin C",
            dosage = "500mg",
            timeToTake = "Afternoon",
            isTaken = false
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("take_button_3").assertIsEnabled()
    }

    @Test
    fun medicationCard_takeButtonIsDisabled_whenMedicationTaken() {
        // Arrange
        val medication = Medication(
            id = 4,
            name = "Iron Supplement",
            dosage = "30mg",
            timeToTake = "Morning",
            isTaken = true
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("take_button_4").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Taken").assertIsDisplayed()
    }

    @Test
    fun medicationCard_showsStatusIndicator_whenMedicationTaken() {
        // Arrange
        val medication = Medication(
            id = 5,
            name = "Calcium",
            dosage = "1000mg",
            timeToTake = "Night",
            isTaken = true
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("status_indicator_5").assertIsDisplayed()
        composeTestRule.onNodeWithText("✓ Medication taken").assertIsDisplayed()
    }

    @Test
    fun medicationCard_takeButtonClick_triggersCallback() {
        // Arrange
        val medication = Medication(
            id = 6,
            name = "Test Med",
            dosage = "25mg",
            timeToTake = "Noon",
            isTaken = false
        )
        var clickCount = 0

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = { clickCount++ }
            )
        }

        composeTestRule.onNodeWithTag("take_button_6").performClick()

        // Assert
        assert(clickCount == 1) { "Click callback should be triggered once" }
    }

    @Test
    fun medicationCard_hasCorrectAccessibilityContent() {
        // Arrange
        val medication = Medication(
            id = 7,
            name = "Accessibility Test",
            dosage = "10mg",
            timeToTake = "Test Time",
            isTaken = false
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert - Check that all interactive elements have proper test tags
        composeTestRule.onNodeWithTag("medication_card_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_icon_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("medication_name_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dosage_label_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dosage_value_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("time_icon_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("time_value_7").assertIsDisplayed()
        composeTestRule.onNodeWithTag("take_button_7").assertIsDisplayed()
    }

    @Test
    fun medicationCard_dosageLabelAndValueAreDisplayed() {
        // Arrange
        val medication = Medication(
            id = 8,
            name = "Dosage Test",
            dosage = "75mg",
            timeToTake = "Test",
            isTaken = false
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Dosage:").assertIsDisplayed()
        composeTestRule.onNodeWithText("75mg").assertIsDisplayed()
    }

    @Test
    fun medicationCard_timeIconAndValueAreDisplayed() {
        // Arrange
        val medication = Medication(
            id = 9,
            name = "Time Test",
            dosage = "Test",
            timeToTake = "Special Time",
            isTaken = false
        )

        // Act
        composeTestRule.setContent {
            MedicationCard(
                medication = medication,
                onTakeMedication = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("time_icon_9").assertIsDisplayed()
        composeTestRule.onNodeWithText("Special Time").assertIsDisplayed()
    }
}
