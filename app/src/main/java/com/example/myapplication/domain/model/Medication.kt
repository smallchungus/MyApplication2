package com.example.myapplication.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val schedule: MedicationSchedule,
    val parentId: String,
    val instructions: String? = null,
    val isActive: Boolean = true
) {
    init {
        require(name.isNotBlank()) { "Medication name cannot be empty" }
        require(dosage.isNotBlank()) { "Medication dosage cannot be empty" }
    }

    fun getNextDoseTime(): LocalDateTime? {
        if (!isActive || schedule.times.isEmpty()) return null
        
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        
        // Find next dose time today
        val todayDoses = schedule.times
            .map { today.atTime(it) }
            .filter { it.isAfter(now) }
            .minOrNull()
        
        // If found, return it; otherwise return first dose tomorrow
        return todayDoses ?: today.plusDays(1).atTime(schedule.times.first())
    }

    fun isDueNow(toleranceMinutes: Int = 10): Boolean {
        if (!isActive) return false
        
        val now = LocalTime.now()
        return schedule.times.any { scheduleTime ->
            val diff = Math.abs(now.toSecondOfDay() - scheduleTime.toSecondOfDay()) / 60
            diff <= toleranceMinutes
        }
    }
}