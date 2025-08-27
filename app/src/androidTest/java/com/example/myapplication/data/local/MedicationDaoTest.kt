package com.example.myapplication.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.local.entity.MedicationEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Integration tests for Room database operations
 * Testing actual database CRUD operations
 */
@RunWith(AndroidJUnit4::class)
class MedicationDaoTest {

    private lateinit var database: MedSyncDatabase
    private lateinit var medicationDao: MedicationDao

    @Before
    fun setup() {
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MedSyncDatabase::class.java
        )
        .allowMainThreadQueries() // Only for testing
        .build()

        medicationDao = database.medicationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetMedication() = runTest {
        // Given
        val medication = createTestMedicationEntity("med-123", "Aspirin")

        // When
        medicationDao.insert(medication)
        val retrieved = medicationDao.getMedicationById("med-123")

        // Then
        assertNotNull(retrieved)
        assertEquals("Aspirin", retrieved?.name)
        assertEquals("100mg", retrieved?.dosage)
        assertTrue(retrieved?.isActive == true)
    }

    @Test
    fun getAllMedicationsForParent() = runTest {
        // Given
        val parentId = "parent-123"
        val medications = listOf(
            createTestMedicationEntity("med-1", "Aspirin", parentId),
            createTestMedicationEntity("med-2", "Vitamin D", parentId),
            createTestMedicationEntity("med-3", "Other Med", "different-parent")
        )

        // When
        medications.forEach { medicationDao.insert(it) }
        val parentMedications = medicationDao.getMedicationsByParentId(parentId)

        // Then
        assertEquals(2, parentMedications.size)
        assertTrue(parentMedications.all { it.parentId == parentId })
    }

    @Test
    fun getActiveMedicationsOnly() = runTest {
        // Given
        val parentId = "parent-123"
        val medications = listOf(
            createTestMedicationEntity("med-1", "Active Med", parentId, isActive = true),
            createTestMedicationEntity("med-2", "Inactive Med", parentId, isActive = false),
            createTestMedicationEntity("med-3", "Another Active", parentId, isActive = true)
        )

        // When
        medications.forEach { medicationDao.insert(it) }
        val activeMedications = medicationDao.getActiveMedicationsByParentId(parentId)

        // Then
        assertEquals(2, activeMedications.size)
        assertTrue(activeMedications.all { it.isActive })
    }

    @Test
    fun searchMedicationsByName() = runTest {
        // Given
        val medications = listOf(
            createTestMedicationEntity("med-1", "Aspirin"),
            createTestMedicationEntity("med-2", "Baby Aspirin"),
            createTestMedicationEntity("med-3", "Vitamin D"),
            createTestMedicationEntity("med-4", "Calcium")
        )

        // When
        medications.forEach { medicationDao.insert(it) }
        val searchResults = medicationDao.searchMedications("%aspirin%")

        // Then
        assertEquals(2, searchResults.size)
        assertTrue(searchResults.all { it.name.contains("Aspirin", ignoreCase = true) })
    }

    @Test
    fun updateMedication() = runTest {
        // Given
        val originalMedication = createTestMedicationEntity("med-123", "Aspirin")
        medicationDao.insert(originalMedication)

        // When
        val updatedMedication = originalMedication.copy(
            dosage = "200mg", // Changed dosage
            instructions = "Take with food" // Added instructions
        )
        medicationDao.update(updatedMedication)

        // Then
        val retrieved = medicationDao.getMedicationById("med-123")
        assertNotNull(retrieved)
        assertEquals("200mg", retrieved?.dosage)
        assertEquals("Take with food", retrieved?.instructions)
    }

    @Test
    fun deleteMedication() = runTest {
        // Given
        val medication = createTestMedicationEntity("med-123", "Aspirin")
        medicationDao.insert(medication)

        // Verify it exists
        assertNotNull(medicationDao.getMedicationById("med-123"))

        // When
        medicationDao.deleteById("med-123")

        // Then
        assertNull(medicationDao.getMedicationById("med-123"))
    }

    @Test
    fun getMedicationsDueToday() = runTest {
        // Given
        val today = LocalDate.now()
        val medications = listOf(
            createTestMedicationEntity("med-1", "Morning Med", times = "08:00"),
            createTestMedicationEntity("med-2", "Evening Med", times = "20:00"),
            createTestMedicationEntity("med-3", "Inactive Med", isActive = false) // Should not appear
        )

        // When
        medications.forEach { medicationDao.insert(it) }
        val dueMedications = medicationDao.getMedicationsDueToday()

        // Then
        assertEquals(2, dueMedications.size) // Only active medications
        assertTrue(dueMedications.all { it.isActive })
    }

    @Test
    fun markAsPendingSync() = runTest {
        // Given
        val medication = createTestMedicationEntity("med-123", "Aspirin")
        medicationDao.insert(medication)

        // When
        medicationDao.markAsPendingSync("med-123")

        // Then
        val retrieved = medicationDao.getMedicationById("med-123")
        assertEquals("PENDING", retrieved?.syncStatus)
    }

    @Test
    fun getPendingSyncMedications() = runTest {
        // Given
        val medications = listOf(
            createTestMedicationEntity("med-1", "Synced Med", syncStatus = "SYNCED"),
            createTestMedicationEntity("med-2", "Pending Med", syncStatus = "PENDING"),
            createTestMedicationEntity("med-3", "Another Pending", syncStatus = "PENDING")
        )

        // When
        medications.forEach { medicationDao.insert(it) }
        val pendingMedications = medicationDao.getPendingSyncMedications()

        // Then
        assertEquals(2, pendingMedications.size)
        assertTrue(pendingMedications.all { it.syncStatus == "PENDING" })
    }

    private fun createTestMedicationEntity(
        id: String,
        name: String,
        parentId: String = "parent-123",
        dosage: String = "100mg",
        times: String = "08:00,20:00",
        isActive: Boolean = true,
        syncStatus: String = "SYNCED"
    ): MedicationEntity {
        return MedicationEntity(
            id = id,
            name = name,
            dosage = dosage,
            parentId = parentId,
            scheduleJson = """
                {
                    "frequency": "DAILY",
                    "times": ["$times"],
                    "startDate": "${LocalDate.now()}"
                }
            """.trimIndent(),
            instructions = null,
            isActive = isActive,
            syncStatus = syncStatus,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}