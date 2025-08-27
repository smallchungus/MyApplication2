package com.example.myapplication.data.local

import androidx.room.*
import com.example.myapplication.data.local.entity.MedicationEntity

@Dao
interface MedicationDao {
    
    @Query("SELECT * FROM medications WHERE id = :medicationId")
    suspend fun getMedicationById(medicationId: String): MedicationEntity?
    
    @Query("SELECT * FROM medications WHERE parentId = :parentId")
    suspend fun getMedicationsByParentId(parentId: String): List<MedicationEntity>
    
    @Query("SELECT * FROM medications WHERE parentId = :parentId AND isActive = 1")
    suspend fun getActiveMedicationsByParentId(parentId: String): List<MedicationEntity>
    
    @Query("SELECT * FROM medications WHERE name LIKE :searchQuery")
    suspend fun searchMedications(searchQuery: String): List<MedicationEntity>
    
    @Query("SELECT * FROM medications WHERE isActive = 1")
    suspend fun getMedicationsDueToday(): List<MedicationEntity>
    
    @Query("SELECT * FROM medications WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSyncMedications(): List<MedicationEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: MedicationEntity)
    
    @Update
    suspend fun update(medication: MedicationEntity)
    
    @Query("DELETE FROM medications WHERE id = :medicationId")
    suspend fun deleteById(medicationId: String)
    
    @Query("UPDATE medications SET syncStatus = 'PENDING' WHERE id = :medicationId")
    suspend fun markAsPendingSync(medicationId: String)
}