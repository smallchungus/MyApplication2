package com.example.myapplication.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Firebase dependencies.
 * 
 * This module provides singleton instances of Firebase services
 * that can be injected throughout the application.
 * 
 * @since 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    /**
     * Provides Firebase Authentication instance.
     * 
     * @return Singleton FirebaseAuth instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Provides Firebase Firestore instance.
     * 
     * @return Singleton FirebaseFirestore instance
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
