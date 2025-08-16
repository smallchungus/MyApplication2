package com.example.myapplication

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class MedicationRepositoryTest {

    @Test
    fun `addMedication adds medication to repository`() = runTest {
        // Arrange
        val repository: MedicationRepository = MedicationRepositoryImpl()
        val medication = Medication(
            name = "Aspirin",
            dosage = "100mg",
            timeToTake = "Morning"
        )
        
        // Act
        repository.addMedication(medication)
        val allMedications = repository.getAllMedications()
        
        // Assert
        assertEquals("Repository should contain 1 medication", 1, allMedications.size)
        assertEquals("Added medication should match", medication.name, allMedications[0].name)
        assertEquals("Added medication dosage should match", medication.dosage, allMedications[0].dosage)
        assertEquals("Added medication time should match", medication.timeToTake, allMedications[0].timeToTake)
    }
}
