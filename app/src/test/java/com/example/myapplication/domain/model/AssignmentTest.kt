package com.example.myapplication.domain.model

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Test-first development for Assignment domain model
 * Testing daily medication assignment distribution
 */
class AssignmentTest {

    @Test
    fun `should create assignment with medication tasks`() {
        // Given
        val medications = listOf(
            createTestMedication("med-1", "Aspirin", listOf(LocalTime.of(8, 0))),
            createTestMedication("med-2", "Vitamin D", listOf(LocalTime.of(12, 0)))
        )

        // When
        val assignment = Assignment(
            id = "assign-123",
            familyId = "family-123",
            assignedTo = "user-456",
            date = LocalDate.now(),
            medicationTasks = medications.map { medication ->
                MedicationTask(
                    medicationId = medication.id,
                    medicationName = medication.name,
                    scheduledTimes = medication.schedule.times,
                    status = TaskStatus.PENDING
                )
            }
        )

        // Then
        assertEquals("assign-123", assignment.id)
        assertEquals("family-123", assignment.familyId)
        assertEquals("user-456", assignment.assignedTo)
        assertEquals(2, assignment.medicationTasks.size)
        assertEquals(TaskStatus.PENDING, assignment.status)
    }

    @Test
    fun `should calculate assignment completion rate`() {
        // Given
        val tasks = listOf(
            MedicationTask("med-1", "Aspirin", listOf(LocalTime.of(8, 0)), TaskStatus.COMPLETED),
            MedicationTask("med-2", "Vitamin D", listOf(LocalTime.of(12, 0)), TaskStatus.COMPLETED),
            MedicationTask("med-3", "Calcium", listOf(LocalTime.of(18, 0)), TaskStatus.PENDING),
            MedicationTask("med-4", "Fish Oil", listOf(LocalTime.of(20, 0)), TaskStatus.MISSED)
        )

        val assignment = Assignment(
            id = "assign-123",
            familyId = "family-123",
            assignedTo = "user-456",
            date = LocalDate.now(),
            medicationTasks = tasks
        )

        // When
        val completionRate = assignment.getCompletionRate()

        // Then
        assertEquals(50.0f, completionRate, 0.01f) // 2 of 4 tasks completed = 50%
    }

    @Test
    fun `should mark assignment as completed when all tasks done`() {
        // Given
        val tasks = listOf(
            MedicationTask("med-1", "Aspirin", listOf(LocalTime.of(8, 0)), TaskStatus.COMPLETED),
            MedicationTask("med-2", "Vitamin D", listOf(LocalTime.of(12, 0)), TaskStatus.COMPLETED)
        )

        val assignment = Assignment(
            id = "assign-123",
            familyId = "family-123",
            assignedTo = "user-456",
            date = LocalDate.now(),
            medicationTasks = tasks
        )

        // When/Then
        assertEquals(TaskStatus.COMPLETED, assignment.status)
        assertTrue(assignment.isCompleted())
    }

    @Test
    fun `should identify overdue assignments`() {
        // Given
        val yesterdayAssignment = Assignment(
            id = "assign-123",
            familyId = "family-123",
            assignedTo = "user-456",
            date = LocalDate.now().minusDays(1), // Yesterday
            medicationTasks = listOf(
                MedicationTask("med-1", "Aspirin", listOf(LocalTime.of(8, 0)), TaskStatus.PENDING)
            )
        )

        // When/Then
        assertTrue(yesterdayAssignment.isOverdue())
    }

    @Test
    fun `should mark medication task as taken with timestamp`() {
        // Given
        val scheduledTime = LocalTime.of(8, 0)
        val task = MedicationTask(
            medicationId = "med-1",
            medicationName = "Aspirin",
            scheduledTimes = listOf(scheduledTime),
            status = TaskStatus.PENDING
        )

        // When - taken at scheduled time
        val completedTask = task.markAsTaken(LocalDate.now().atTime(scheduledTime))

        // Then
        assertEquals(TaskStatus.COMPLETED, completedTask.status)
        assertNotNull(completedTask.completedAt)
        assertTrue(completedTask.wasOnTime(toleranceMinutes = 30))
    }

    @Test
    fun `should detect late medication taking`() {
        // Given
        val scheduledTime = LocalTime.of(8, 0)
        val task = MedicationTask(
            medicationId = "med-1",
            medicationName = "Aspirin",
            scheduledTimes = listOf(scheduledTime),
            status = TaskStatus.PENDING
        )

        // When - taken 2 hours late
        val lateTime = LocalDateTime.now().with(scheduledTime.plusHours(2))
        val completedTask = task.markAsTaken(lateTime)

        // Then
        assertEquals(TaskStatus.COMPLETED, completedTask.status)
        assertFalse(completedTask.wasOnTime(toleranceMinutes = 30))
    }

    @Test
    fun `should validate assignment must have at least one medication task`() {
        // Given/When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Assignment(
                id = "assign-123",
                familyId = "family-123",
                assignedTo = "user-456",
                date = LocalDate.now(),
                medicationTasks = emptyList() // No tasks should fail
            )
        }
        assertEquals("Assignment must have at least one medication task", exception.message)
    }

    private fun createTestMedication(id: String, name: String, times: List<LocalTime>): Medication {
        return Medication(
            id = id,
            name = name,
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = times,
                startDate = LocalDate.now()
            ),
            parentId = "parent-123"
        )
    }
}