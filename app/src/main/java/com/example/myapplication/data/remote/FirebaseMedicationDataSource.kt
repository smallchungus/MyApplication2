package com.example.myapplication.data.remote

import com.example.myapplication.domain.model.Medication

interface FirebaseMedicationDataSource {
    suspend fun saveMedication(medication: Medication): Result<Unit>
    suspend fun deleteMedication(medicationId: String): Result<Unit>
    suspend fun scheduleMedicationSync(medicationId: String)
}