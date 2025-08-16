package com.example.myapplication

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Firebase initialization with graceful fallback for development.
 * 
 * This approach allows the app to run without Firebase for UI development
 * while providing proper Firebase integration for production builds.
 * 
 * Security Note: Never disable Firebase checks in production builds.
 * 
 * @author ParentCare Development Team
 * @since 1.0.0
 */
class FirebaseManager {
    companion object {
        private var isFirebaseAvailable: Boolean = false
        
        fun initializeFirebase(context: Context): Boolean {
            return try {
                FirebaseApp.initializeApp(context)
                isFirebaseAvailable = true
                Log.d("Firebase", "Firebase initialized successfully")
                true
            } catch (e: Exception) {
                Log.w("Firebase", "Firebase initialization failed: ${e.message}")
                isFirebaseAvailable = false
                false
            }
        }
        
        fun isAvailable(): Boolean = isFirebaseAvailable
    }
}
