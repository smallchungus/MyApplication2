package com.example.myapplication.presentation.medication

import com.example.myapplication.data.repository.MedicationRepositoryImpl
import com.example.myapplication.domain.model.Medication
import com.example.myapplication.domain.model.MedicationSchedule
import com.example.myapplication.domain.model.ScheduleFrequency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Test-first development for MedicationViewModel
 * Testing UI state management and user interactions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MedicationViewModelTest {

    @Mock
    private lateinit var mockRepository: MedicationRepositoryImpl

    private lateinit var viewModel: MedicationViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MedicationViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load medications on init`() = runTest {
        // Given
        val testMedications = listOf(
            createTestMedication("med-1", "Aspirin"),
            createTestMedication("med-2", "Vitamin D")
        )
        whenever(mockRepository.getMedications("parent-123")).thenReturn(testMedications)

        // When
        viewModel.loadMedications("parent-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertEquals(2, uiState.medications.size)
        assertEquals("Aspirin", uiState.medications[0].name)
        assertNull(uiState.error)
    }

    @Test
    fun `should handle loading state correctly`() = runTest {
        // Given
        whenever(mockRepository.getMedications("parent-123")).thenReturn(emptyList())

        // When
        val initialState = viewModel.uiState.first()
        viewModel.loadMedications("parent-123")
        
        // Then - should start with loading
        assertTrue(initialState.isLoading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.first()
        assertFalse(finalState.isLoading)
    }

    @Test
    fun `should handle repository error gracefully`() = runTest {
        // Given
        val error = RuntimeException("Database error")
        whenever(mockRepository.getMedications("parent-123")).thenThrow(error)

        // When
        viewModel.loadMedications("parent-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertTrue(uiState.medications.isEmpty())
        assertEquals("Failed to load medications", uiState.error)
    }

    @Test
    fun `should add new medication successfully`() = runTest {
        // Given
        val newMedication = createTestMedication("med-new", "New Medicine")
        whenever(mockRepository.saveMedication(any())).thenReturn(Result.success(Unit))
        whenever(mockRepository.getMedications("parent-123")).thenReturn(listOf(newMedication))

        // When
        viewModel.addMedication(
            name = "New Medicine",
            dosage = "50mg",
            times = listOf(LocalTime.of(9, 0)),
            parentId = "parent-123"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).saveMedication(any())
        verify(mockRepository).getMedications("parent-123") // Should reload list
        val uiState = viewModel.uiState.first()
        assertNull(uiState.error)
    }

    @Test
    fun `should validate medication input`() = runTest {
        // When - empty name
        viewModel.addMedication(
            name = "",
            dosage = "50mg", 
            times = listOf(LocalTime.of(9, 0)),
            parentId = "parent-123"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("Medication name cannot be empty", uiState.error)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `should validate dosage input`() = runTest {
        // When - empty dosage
        viewModel.addMedication(
            name = "Medicine",
            dosage = "",
            times = listOf(LocalTime.of(9, 0)),
            parentId = "parent-123"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("Dosage cannot be empty", uiState.error)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `should validate schedule times`() = runTest {
        // When - no times specified
        viewModel.addMedication(
            name = "Medicine",
            dosage = "50mg",
            times = emptyList(),
            parentId = "parent-123"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("At least one dose time is required", uiState.error)
        verifyNoInteractions(mockRepository)
    }

    @Test
    fun `should clear error when loading new data`() = runTest {
        // Given - start with error state
        whenever(mockRepository.getMedications("parent-123"))
            .thenThrow(RuntimeException("Error"))
            .thenReturn(emptyList())
        
        viewModel.loadMedications("parent-123")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val errorState = viewModel.uiState.first()
        assertNotNull(errorState.error)

        // When - load again (should succeed on second call)
        viewModel.loadMedications("parent-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - error should be cleared
        val cleanState = viewModel.uiState.first()
        assertNull(cleanState.error)
    }

    private fun createTestMedication(id: String, name: String): Medication {
        return Medication(
            id = id,
            name = name,
            dosage = "100mg",
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.DAILY,
                times = listOf(LocalTime.of(8, 0)),
                startDate = LocalDate.now()
            ),
            parentId = "parent-123"
        )
    }
}