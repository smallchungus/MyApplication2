package com.example.myapplication.navigation

/**
 * Navigation destinations for ParentCare family coordination app.
 * 
 * Defines type-safe navigation routes using sealed classes to prevent
 * navigation errors and provide compile-time safety. Each destination
 * represents a distinct screen or user flow within the application.
 * 
 * Design Decisions:
 * - Uses sealed classes for type safety and exhaustive when statements
 * - Each destination includes route name for navigation graph definition
 * - Supports parameterized routes for data passing between screens
 * - Hierarchical structure matches information architecture
 * 
 * Navigation Flow:
 * ```
 * Onboarding/Auth → Dashboard → Feature Screens
 *      ↓               ↓            ↓
 *   Login/Setup    Family Care   Emergency/Reports
 * ```
 * 
 * @since 1.0.0
 * @see ParentCareNavigationCoordinator for implementation details
 */
sealed class ParentCareDestination(val route: String) {
    
    /**
     * Onboarding and authentication flow destinations.
     * 
     * These screens guide new users through account setup and
     * family coordination configuration.
     */
    sealed class Auth(route: String) : ParentCareDestination(route) {
        /** Initial welcome screen with value proposition */
        object Welcome : Auth("welcome")
        
        /** Login and account creation */
        object Login : Auth("login") 
        
        /** Family setup for new accounts */
        object FamilySetup : Auth("family_setup")
    }
    
    /**
     * Main application flow destinations.
     * 
     * Core family coordination screens used daily by family members
     * to manage medication oversight and care coordination.
     */
    sealed class Main(route: String) : ParentCareDestination(route) {
        /** Primary dashboard showing today's assignments and alerts */
        object Dashboard : Main("dashboard")
        
        /** Detailed view of a specific assignment */
        object AssignmentDetail : Main("assignment_detail/{assignmentId}") {
            /**
             * Creates route with assignment ID parameter.
             * @param assignmentId Unique identifier for the assignment
             */
            fun createRoute(assignmentId: String) = "assignment_detail/$assignmentId"
        }
        
        /** Emergency contact information and protocols */
        object Emergency : Main("emergency")
        
        /** Weekly and monthly care reports */
        object Reports : Main("reports")
        
        /** Family member management and settings */
        object FamilySettings : Main("family_settings")
    }
    
    /**
     * System and utility destinations.
     * 
     * Supporting screens for error handling, settings, and system functions.
     */
    sealed class System(route: String) : ParentCareDestination(route) {
        /** Error recovery screen with retry functionality */
        object Error : System("error")
        
        /** App settings and preferences */
        object Settings : System("settings")
        
        /** Development mode dashboard for testing */
        object DevMode : System("dev_mode")
    }
}

/**
 * Navigation events for coordinating between UI and navigation logic.
 * 
 * Sealed class representing all possible navigation actions that can
 * be triggered from UI components. Provides type-safe event handling
 * and supports complex navigation scenarios.
 * 
 * Usage Example:
 * ```kotlin
 * // In UI component
 * onNavigationEvent(NavigationEvent.NavigateTo(ParentCareDestination.Main.Dashboard))
 * 
 * // In navigation coordinator
 * when (event) {
 *     is NavigationEvent.NavigateTo -> navController.navigate(event.destination.route)
 *     is NavigationEvent.NavigateBack -> navController.popBackStack()
 * }
 * ```
 * 
 * @since 1.0.0
 */
sealed class NavigationEvent {
    /**
     * Navigate to a specific destination.
     * @param destination Target screen to navigate to
     * @param clearBackStack Whether to clear the back stack (default: false)
     */
    data class NavigateTo(
        val destination: ParentCareDestination, 
        val clearBackStack: Boolean = false
    ) : NavigationEvent()
    
    /**
     * Navigate back to previous screen.
     * @param fallbackDestination Optional fallback if back stack is empty
     */
    data class NavigateBack(
        val fallbackDestination: ParentCareDestination? = null
    ) : NavigationEvent()
    
    /**
     * Navigate to destination and clear back stack up to specified destination.
     * @param destination Target destination
     * @param popUpTo Destination to pop up to (exclusive)
     */
    data class NavigateAndClearTo(
        val destination: ParentCareDestination,
        val popUpTo: ParentCareDestination
    ) : NavigationEvent()
}
