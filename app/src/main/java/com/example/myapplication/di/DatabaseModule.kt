package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.local.MedSyncDatabase
import com.example.myapplication.data.local.MedicationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MedSyncDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MedSyncDatabase::class.java,
            "medsync_database"
        ).build()
    }

    @Provides
    fun provideMedicationDao(database: MedSyncDatabase): MedicationDao {
        return database.medicationDao()
    }
}