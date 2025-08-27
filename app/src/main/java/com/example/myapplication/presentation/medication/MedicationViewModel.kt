package com.example.myapplication.presentation.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.MedicationRepositoryImpl
import com.example.myapplication.domain.model.Medication
import com.example.myapplication.domain.model.MedicationSchedule
import com.example.myapplication.domain.model.ScheduleFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    fun loadMedications(parentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val medications = repository.getMedications(parentId)
                _uiState.value = _uiState.value.copy(
                    medications = medications,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load medications"
                )
            }
        }
    }

    fun addMedication(
        name: String,
        dosage: String,
        times: List<LocalTime>,
        parentId: String
    ) {
        viewModelScope.launch {
            // Validation
            when {
                name.isBlank() -> {
                    _uiState.value = _uiState.value.copy(error = "Medication name cannot be empty")
                    return@launch
                }
                dosage.isBlank() -> {
                    _uiState.value = _uiState.value.copy(error = "Dosage cannot be empty")
                    return@launch
                }
                times.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(error = "At least one dose time is required")
                    return@launch
                }
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val medication = Medication(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    dosage = dosage,
                    schedule = MedicationSchedule(
                        frequency = ScheduleFrequency.DAILY,
                        times = times,
                        startDate = LocalDate.now()
                    ),
                    parentId = parentId
                )

                val result = repository.saveMedication(medication)
                if (result.isSuccess) {
                    // Reload medications to show updated list
                    loadMedications(parentId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to save medication"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save medication"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}