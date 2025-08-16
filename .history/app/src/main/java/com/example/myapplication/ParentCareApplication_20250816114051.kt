package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for ParentCare family coordination app.
 * 
 * This class initializes Hilt dependency injection and any other
 * application-level configurations needed for the app to function.
 * 
 * @since 1.0.0
 */
@HiltAndroidApp
class ParentCareApplication : Application()
