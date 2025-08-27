package com.example.myapplication.di

import com.example.myapplication.data.local.MedicationDao
import com.example.myapplication.data.remote.FirebaseMedicationDataSource
import com.example.myapplication.data.repository.MedicationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMedicationRepository(
        medicationDao: MedicationDao,
        firebaseDataSource: FirebaseMedicationDataSource
    ): MedicationRepositoryImpl {
        return MedicationRepositoryImpl(
            localDataSource = medicationDao,
            remoteDataSource = firebaseDataSource
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseMedicationDataSource(): FirebaseMedicationDataSource {
        // For MVP, return a mock implementation
        // TODO: Replace with real Firebase implementation
        return object : FirebaseMedicationDataSource {
            override suspend fun saveMedication(medication: com.example.myapplication.domain.model.Medication): Result<Unit> {
                return Result.success(Unit)
            }
            override suspend fun deleteMedication(medicationId: String): Result<Unit> {
                return Result.success(Unit)
            }
            override suspend fun scheduleMedicationSync(medicationId: String) {
                // No-op for now
            }
        }
    }
}