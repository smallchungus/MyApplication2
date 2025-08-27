package com.example.myapplication.presentation.medication

import com.example.myapplication.domain.model.Medication

data class MedicationUiState(
    val medications: List<Medication> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)