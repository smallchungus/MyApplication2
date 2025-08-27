package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.myapplication.data.local.entity.MedicationEntity

@Database(
    entities = [MedicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MedSyncDatabase : RoomDatabase() {
    
    abstract fun medicationDao(): MedicationDao
    
    companion object {
        @Volatile
        private var INSTANCE: MedSyncDatabase? = null
        
        fun getDatabase(context: Context): MedSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedSyncDatabase::class.java,
                    "medsync_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}