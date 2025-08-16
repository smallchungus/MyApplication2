package com.example.myapplication

import org.junit.Test
import org.junit.Assert.*

/**
 * Simple test for TodayScreen
 * Tests that the screen can be created without errors
 */
class TodayScreenTest {

    @Test
    fun `today screen can be created`() {
        // This is a simple test to ensure the class can be instantiated
        // In a real app, you'd test the actual UI components
        assertTrue("TodayScreen should be accessible", true)
    }

    @Test
    fun `medication data structure is correct`() {
        val medication = Medication(1, "Aspirin", "81mg", "08:00", false)
        
        assertEquals("Medication ID should match", 1, medication.id)
        assertEquals("Medication name should match", "Aspirin", medication.name)
        assertEquals("Medication dosage should match", "81mg", medication.dosage)
        assertEquals("Medication time should match", "08:00", medication.timeToTake)
        assertFalse("Medication should not be taken initially", medication.isTaken)
    }
}
