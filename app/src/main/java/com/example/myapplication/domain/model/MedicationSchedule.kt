package com.example.myapplication.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class MedicationSchedule(
    val frequency: ScheduleFrequency,
    val times: List<LocalTime>,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
) {
    init {
        if (frequency == ScheduleFrequency.DAILY && times.isEmpty()) {
            throw IllegalArgumentException("Daily medication must have at least one scheduled time")
        }
    }
}