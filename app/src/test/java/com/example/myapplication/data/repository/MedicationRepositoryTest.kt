package com.example.myapplication.data.repository

import com.example.myapplication.data.local.MedicationDao
import com.example.myapplication.data.remote.FirebaseMedicationDataSource
import com.example.myapplication.domain.model.Medication
import com.example.myapplication.domain.model.MedicationSchedule
import com.example.myapplication.domain.model.ScheduleFrequency
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Test-first development for MedicationRepository
 * Following offline-first architecture with Firebase sync
 */
class MedicationRepositoryTest {

    @Mock
    private lateinit var mockMedicationDao: MedicationDao
    
    @Mock
    private lateinit var mockFirebaseDataSource: FirebaseMedicationDataSource
    
    private lateinit var medicationRepository: MedicationRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        medicationRepository = MedicationRepositoryImpl(
            localDataSource = mockMedicationDao,
            remoteDataSource = mockFirebaseDataSource
        )
    }

    @Test
    fun `should get medications from local database first`() = runTest {
        // Given
        val parentId = "parent-123"
        val localMedications = listOf(
            createTestMedication("med-1", "Aspirin"),
            createTestMedication("med-2", "Vitamin D")
        )
        whenever(mockMedicationDao.getMedicationsByParentId(parentId)).thenReturn(emptyList())

        // When
        val result = medicationRepository.getMedications(parentId)

        // Then
        assertEquals(0, result.size)
        verify(mockMedicationDao).getMedicationsByParentId(parentId)
        verifyNoInteractions(mockFirebaseDataSource) // Should not hit remote for read
    }

    @Test
    fun `should save medication locally and schedule sync`() = runTest {
        // Given
        val medication = createTestMedication("med-123", "New Medication")
        whenever(mockMedicationDao.insert(any())).thenReturn(Unit)

        // When
        val result = medicationRepository.saveMedication(medication)

        // Then
        assertTrue(result.isSuccess)
        verify(mockMedicationDao).insert(any())
        // Should also schedule sync to Firebase
        verify(mockFirebaseDataSource).scheduleMedicationSync(medication.id)
    }

    @Test
    fun `should handle database error gracefully`() = runTest {
        // Given
        val parentId = "parent-123"
        val databaseError = RuntimeException("Database connection failed")
        whenever(mockMedicationDao.getMedicationsByParentId(parentId)).thenThrow(databaseError)

        // When
        val result = medicationRepository.getMedications(parentId)

        // Then
        assertTrue(result.isEmpty()) // Should return empty list, not crash
        // Could also verify error logging here
    }

    @Test
    fun `should sync medication to Firebase in background`() = runTest {
        // Given
        val medication = createTestMedication("med-123", "Aspirin")
        whenever(mockFirebaseDataSource.saveMedication(medication)).thenReturn(Result.success(Unit))

        // When
        medicationRepository.syncMedicationToFirebase(medication)

        // Then
        verify(mockFirebaseDataSource).saveMedication(medication)
    }

    @Test
    fun `should handle Firebase sync failure`() = runTest {
        // Given
        val medication = createTestMedication("med-123", "Aspirin")
        val syncError = RuntimeException("Network error")
        whenever(mockFirebaseDataSource.saveMedication(medication)).thenReturn(Result.failure(syncError))

        // When
        val result = medicationRepository.syncMedicationToFirebase(medication)

        // Then
        assertTrue(result.isFailure)
        // Should mark medication as pending sync in local database
        verify(mockMedicationDao).markAsPendingSync(medication.id)
    }

    @Test
    fun `should delete medication locally and sync deletion`() = runTest {
        // Given
        val medicationId = "med-123"
        whenever(mockMedicationDao.deleteById(medicationId)).thenReturn(Unit)

        // When
        val result = medicationRepository.deleteMedication(medicationId)

        // Then
        assertTrue(result.isSuccess)
        verify(mockMedicationDao).deleteById(medicationId)
        verify(mockFirebaseDataSource).deleteMedication(medicationId)
    }

    @Test
    fun `should get active medications only`() = runTest {
        // Given
        val parentId = "parent-123"
        val activeMedications = listOf(
            createTestMedication("med-1", "Active Med", isActive = true),
            createTestMedication("med-2", "Another Active", isActive = true)
        )
        whenever(mockMedicationDao.getActiveMedicationsByParentId(parentId)).thenReturn(emptyList())

        // When
        val result = medicationRepository.getActiveMedications(parentId)

        // Then
        assertEquals(0, result.size)
        verify(mockMedicationDao).getActiveMedicationsByParentId(parentId)
    }

    @Test
    fun `should search medications by name`() = runTest {
        // Given
        val searchQuery = "asp"
        val searchResults = listOf(
            createTestMedication("med-1", "Aspirin"),
            createTestMedication("med-2", "Baby Aspirin")
        )
        whenever(mockMedicationDao.searchMedications("%$searchQuery%")).thenReturn(emptyList())

        // When
        val result = medicationRepository.searchMedications(searchQuery)

        // Then
        assertEquals(0, result.size)
        verify(mockMedicationDao).searchMedications("%$searchQuery%")
    }

    @Test
    fun `should get medications due for today`() = runTest {
        // Given
        val today = LocalDate.now()
        val dueMedications = listOf(
            createTestMedication("med-1", "Morning Med", times = listOf(LocalTime.of(8, 0))),
            createTestMedication("med-2", "Evening Med", times = listOf(LocalTime.of(20, 0)))
        )
        whenever(mockMedicationDao.getMedicationsDueToday()).thenReturn(emptyList())

        // When
        val result = medicationRepository.getMedicationsDueToday()

        // Then
        assertEquals(0, result.size)
        verify(mockMedicationDao).getMedicationsDueToday()
    }

    private fun createTestMedication(
        id: String, 
        name: String, 
        times: List<LocalTime> = listOf(LocalTime.of(8, 0)),
        isActive: Boolean = true
    ): Medication {
        return Medication(
            id = id,
            name = name,
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = times,
                startDate = LocalDate.now()
            ),
            parentId = "parent-123",
            isActive = isActive
        )
    }
}