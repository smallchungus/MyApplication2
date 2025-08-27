package com.example.myapplication.domain.model

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Test-first development for Medication domain model
 * Following FAANG standards for comprehensive domain testing
 */
class MedicationTest {

    @Test
    fun `should create medication with valid data`() {
        // Given
        val medication = Medication(
            id = "med-123",
            name = "Aspirin",
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(30)
            ),
            parentId = "parent-123",
            instructions = "Take with food",
            isActive = true
        )

        // Then
        assertEquals("med-123", medication.id)
        assertEquals("Aspirin", medication.name)
        assertEquals("100mg", medication.dosage)
        assertEquals(2, medication.schedule.times.size)
        assertTrue(medication.isActive)
    }

    @Test
    fun `should validate medication name is not empty`() {
        // Given/When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Medication(
                id = "med-123",
                name = "", // Empty name should fail
                dosage = "100mg",
                schedule = createValidSchedule(),
                parentId = "parent-123"
            )
        }
        assertEquals("Medication name cannot be empty", exception.message)
    }

    @Test
    fun `should validate dosage is not empty`() {
        // Given/When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Medication(
                id = "med-123",
                name = "Aspirin",
                dosage = "", // Empty dosage should fail
                schedule = createValidSchedule(),
                parentId = "parent-123"
            )
        }
        assertEquals("Medication dosage cannot be empty", exception.message)
    }

    @Test
    fun `should validate schedule has at least one time for daily frequency`() {
        // Given/When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Medication(
                id = "med-123",
                name = "Aspirin",
                dosage = "100mg",
                schedule = MedicationSchedule(
                    frequency = ScheduleFrequency.DAILY,
                    times = emptyList(), // Empty times should fail for daily
                    startDate = LocalDate.now()
                ),
                parentId = "parent-123"
            )
        }
        assertEquals("Daily medication must have at least one scheduled time", exception.message)
    }

    @Test
    fun `should generate next dose time correctly`() {
        // Given
        val medication = Medication(
            id = "med-123",
            name = "Aspirin",
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)),
                startDate = LocalDate.now()
            ),
            parentId = "parent-123"
        )

        // When
        val nextDose = medication.getNextDoseTime()

        // Then
        assertNotNull(nextDose)
        assertTrue(nextDose!!.toLocalTime() == LocalTime.of(8, 0) || 
                  nextDose!!.toLocalTime() == LocalTime.of(20, 0))
    }

    @Test
    fun `should check if medication is due now`() {
        // Given
        val now = LocalTime.now()
        val medication = Medication(
            id = "med-123",
            name = "Aspirin",
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = listOf(now.minusMinutes(5)), // 5 minutes ago
                startDate = LocalDate.now()
            ),
            parentId = "parent-123"
        )

        // When/Then
        assertTrue(medication.isDueNow(toleranceMinutes = 10))
        assertFalse(medication.isDueNow(toleranceMinutes = 2))
    }

    private fun createValidSchedule(): MedicationSchedule {
        return MedicationSchedule(
            frequency = ScheduleFrequency.DAILY,
            times = listOf(LocalTime.of(8, 0)),
            startDate = LocalDate.now()
        )
    }
}