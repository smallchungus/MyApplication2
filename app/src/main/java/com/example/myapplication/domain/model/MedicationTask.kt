package com.example.myapplication.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

data class MedicationTask(
    val medicationId: String,
    val medicationName: String,
    val scheduledTimes: List<LocalTime>,
    val status: TaskStatus,
    val completedAt: LocalDateTime? = null
) {
    fun markAsTaken(timestamp: LocalDateTime): MedicationTask {
        return copy(
            status = TaskStatus.COMPLETED,
            completedAt = timestamp
        )
    }

    fun wasOnTime(toleranceMinutes: Int = 30): Boolean {
        val completionTime = completedAt ?: return false
        val completionLocalTime = completionTime.toLocalTime()
        
        return scheduledTimes.any { scheduleTime ->
            val diffMinutes = Math.abs(
                completionLocalTime.toSecondOfDay() - scheduleTime.toSecondOfDay()
            ) / 60
            diffMinutes <= toleranceMinutes
        }
    }
}