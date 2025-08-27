package com.example.myapplication.domain.model

import java.time.LocalDate

data class Assignment(
    val id: String,
    val familyId: String,
    val assignedTo: String,
    val date: LocalDate,
    val medicationTasks: List<MedicationTask>
) {
    init {
        require(medicationTasks.isNotEmpty()) { "Assignment must have at least one medication task" }
    }

    val status: TaskStatus
        get() = when {
            medicationTasks.all { it.status == TaskStatus.COMPLETED } -> TaskStatus.COMPLETED
            medicationTasks.any { it.status == TaskStatus.MISSED } -> TaskStatus.MISSED
            else -> TaskStatus.PENDING
        }

    fun getCompletionRate(): Float {
        val completedTasks = medicationTasks.count { it.status == TaskStatus.COMPLETED }
        return (completedTasks.toFloat() / medicationTasks.size) * 100f
    }

    fun isCompleted(): Boolean = status == TaskStatus.COMPLETED

    fun isOverdue(): Boolean = date.isBefore(LocalDate.now()) && status != TaskStatus.COMPLETED
}