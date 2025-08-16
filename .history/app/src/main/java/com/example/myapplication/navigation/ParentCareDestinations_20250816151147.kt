package com.example.myapplication.navigation

/**
 * Type-safe navigation destinations for ParentCare family coordination app.
 * 
 * Implements sealed class hierarchy for compile-time safe navigation routing.
 * Each destination encapsulates its route string and any required arguments.
 * 
 * Design Decisions:
 * - Uses sealed classes for exhaustive when statement handling
 * - Separates destinations by feature area (Auth, Main, System)
 * - Provides clear route strings for debugging
 * - Supports deep linking through consistent URL patterns
 * 
 * @author ParentCare Navigation Team
 * @since 1.0.0
 */
sealed class ParentCareDestination(val route: String) {
    
    /**
     * Authentication flow destinations
     */
    sealed class Auth(route: String) : ParentCareDestination(route) {
        data object Welcome : Auth("auth/welcome")
        data object Login : Auth("auth/login") 
        data object FamilySetup : Auth("auth/family_setup")
    }
    
    /**
     * Main application destinations
     */
    sealed class Main(route: String) : ParentCareDestination(route) {
        data object Dashboard : Main("main/dashboard")
        data object Emergency : Main("main/emergency")
        data object Reports : Main("main/reports")
    }
    
    /**
     * System/utility destinations
     */
    sealed class System(route: String) : ParentCareDestination(route) {
        data object DevMode : System("system/dev_mode")
    }
}

/**
 * Navigation events for type-safe navigation actions.
 */
sealed class NavigationEvent {
    data object NavigateToLogin : NavigationEvent()
    data object NavigateToFamilySetup : NavigationEvent()
    data object NavigateToFamilyDashboard : NavigationEvent()
    data object NavigateToEmergencyInfo : NavigationEvent()
    data object NavigateToReports : NavigationEvent()
    data object NavigateToDevMode : NavigationEvent()
    data object NavigateBack : NavigationEvent()
    data class NavigateTo(val destination: ParentCareDestination) : NavigationEvent()
}
