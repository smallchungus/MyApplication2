package com.example.myapplication.data.repository

import com.example.myapplication.data.local.MedicationDao
import com.example.myapplication.data.local.entity.MedicationEntity
import com.example.myapplication.data.remote.FirebaseMedicationDataSource
import com.example.myapplication.domain.model.Medication
import com.example.myapplication.domain.model.MedicationSchedule
import com.example.myapplication.domain.model.ScheduleFrequency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

class MedicationRepositoryImpl(
    private val localDataSource: MedicationDao,
    private val remoteDataSource: FirebaseMedicationDataSource
) {

    suspend fun getMedications(parentId: String): List<Medication> = withContext(Dispatchers.IO) {
        try {
            val entities = localDataSource.getMedicationsByParentId(parentId)
            entities.map { it.toDomainModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveMedication(medication: Medication): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = medication.toEntity()
            localDataSource.insert(entity)
            remoteDataSource.scheduleMedicationSync(medication.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMedication(medicationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            localDataSource.deleteById(medicationId)
            remoteDataSource.deleteMedication(medicationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveMedications(parentId: String): List<Medication> = withContext(Dispatchers.IO) {
        val entities = localDataSource.getActiveMedicationsByParentId(parentId)
        entities.map { it.toDomainModel() }
    }

    suspend fun searchMedications(query: String): List<Medication> = withContext(Dispatchers.IO) {
        val entities = localDataSource.searchMedications("%$query%")
        entities.map { it.toDomainModel() }
    }

    suspend fun getMedicationsDueToday(): List<Medication> = withContext(Dispatchers.IO) {
        val entities = localDataSource.getMedicationsDueToday()
        entities.map { it.toDomainModel() }
    }

    suspend fun syncMedicationToFirebase(medication: Medication): Result<Unit> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.saveMedication(medication)
        if (result.isFailure) {
            localDataSource.markAsPendingSync(medication.id)
        }
        result
    }

    @Serializable
    private data class ScheduleData(
        val frequency: String,
        val times: List<String>,
        val startDate: String,
        val endDate: String? = null
    )

    private fun MedicationEntity.toDomainModel(): Medication {
        val scheduleData = Json.decodeFromString<ScheduleData>(scheduleJson)
        
        return Medication(
            id = id,
            name = name,
            dosage = dosage,
            schedule = MedicationSchedule(
                frequency = ScheduleFrequency.valueOf(scheduleData.frequency),
                times = scheduleData.times.map { LocalTime.parse(it) },
                startDate = LocalDate.parse(scheduleData.startDate),
                endDate = scheduleData.endDate?.let { LocalDate.parse(it) }
            ),
            parentId = parentId,
            instructions = instructions,
            isActive = isActive
        )
    }

    private fun Medication.toEntity(): MedicationEntity {
        val scheduleData = ScheduleData(
            frequency = schedule.frequency.name,
            times = schedule.times.map { it.toString() },
            startDate = schedule.startDate.toString(),
            endDate = schedule.endDate?.toString()
        )
        
        return MedicationEntity(
            id = id,
            name = name,
            dosage = dosage,
            parentId = parentId,
            scheduleJson = Json.encodeToString(scheduleData),
            instructions = instructions,
            isActive = isActive
        )
    }
}