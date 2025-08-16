package com.example.myapplication

interface MedicationRepository {
    suspend fun addMedication(medication: Medication)
    suspend fun getAllMedications(): List<Medication>
}

class MedicationRepositoryImpl : MedicationRepository {
    private val medications = mutableListOf<Medication>()
    
    override suspend fun addMedication(medication: Medication) {
        medications.add(medication)
    }
    
    override suspend fun getAllMedications(): List<Medication> {
        return medications.toList()
    }
}
