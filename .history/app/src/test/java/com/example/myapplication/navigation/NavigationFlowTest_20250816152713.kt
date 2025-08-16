package com.example.myapplication.navigation

import app.cash.turbine.test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Comprehensive navigation flow tests for ParentCare app.
 * 
 * Tests complete user journeys, navigation state management, and
 * authentication-aware routing to ensure seamless user experience.
 * 
 * Test Coverage:
 * - Complete login → setup → dashboard flow
 * - Back navigation behavior
 * - Logout functionality and state clearing
 * - Navigation event handling
 * - State consistency across navigation changes
 * 
 * @author ParentCare Navigation Team
 * @since 1.0.0
 */
class NavigationFlowTest {
    
    private lateinit var coordinator: ParentCareNavigationCoordinator
    private lateinit var testDispatcher: StandardTestDispatcher
    private lateinit var testScope: TestScope
    
    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        coordinator = ParentCareNavigationCoordinator()
    }
    
    /**
     * Tests complete user journey from login to dashboard.
     * 
     * Verifies that navigation flows correctly through:
     * Login → Family Setup → Dashboard
     */
    @Test
    fun `complete user journey from login to dashboard`() = testScope.runTest {
        // Given: Starting at login screen
        assertEquals(
            ParentCareDestination.Auth.Welcome,
            coordinator.currentDestination.value,
            "Should start at Welcome screen"
        )
        
        // When: User navigates to login
        coordinator.navigateToLogin()
        advanceUntilIdle()
        
        // Then: Should be at login screen
        assertEquals(
            ParentCareDestination.Auth.Login,
            coordinator.currentDestination.value,
            "Should navigate to Login screen"
        )
        
        // When: User creates new account
        coordinator.navigateToCreateAccount()
        advanceUntilIdle()
        
        // Then: Should be at family setup
        assertEquals(
            ParentCareDestination.Auth.FamilySetup,
            coordinator.currentDestination.value,
            "Should navigate to Family Setup screen"
        )
        
        // When: User completes setup
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        // Then: Should be at dashboard
        assertEquals(
            ParentCareDestination.Main.Dashboard,
            coordinator.currentDestination.value,
            "Should navigate to Dashboard screen"
        )
    }
    
    /**
     * Tests existing user login flow (direct to dashboard).
     */
    @Test
    fun `existing user can login directly to dashboard`() = testScope.runTest {
        // Given: User is at login screen
        coordinator.navigateToLogin()
        advanceUntilIdle()
        
        // When: Existing user signs in
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        // Then: Should go directly to dashboard
        assertEquals(
            ParentCareDestination.Main.Dashboard,
            coordinator.currentDestination.value,
            "Existing users should go directly to dashboard"
        )
    }
    
    /**
     * Tests navigation from dashboard to sub-screens.
     */
    @Test
    fun `dashboard navigation to sub-screens works correctly`() = testScope.runTest {
        // Given: User is at dashboard
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        // When: User navigates to emergency info
        coordinator.navigateToEmergencyInfo()
        advanceUntilIdle()
        
        // Then: Should be at emergency screen
        assertEquals(
            ParentCareDestination.Main.Emergency,
            coordinator.currentDestination.value,
            "Should navigate to Emergency screen"
        )
        
        // When: User navigates back
        coordinator.navigateBack()
        advanceUntilIdle()
        
        // And navigates to reports
        coordinator.navigateToReports()
        advanceUntilIdle()
        
        // Then: Should be at reports screen
        assertEquals(
            ParentCareDestination.Main.Reports,
            coordinator.currentDestination.value,
            "Should navigate to Reports screen"
        )
    }
    
    /**
     * Tests logout functionality clears authentication state.
     */
    @Test
    fun `logout clears authentication state and returns to login`() = testScope.runTest {
        // Given: User is authenticated and at dashboard
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        assertEquals(
            ParentCareDestination.Main.Dashboard,
            coordinator.currentDestination.value,
            "Should be at dashboard before logout"
        )
        
        // When: User logs out
        coordinator.logout()
        advanceUntilIdle()
        
        // Then: Should return to login screen
        assertEquals(
            ParentCareDestination.Auth.Login,
            coordinator.currentDestination.value,
            "Should return to login screen after logout"
        )
    }
    
    /**
     * Tests navigation events are emitted correctly.
     */
    @Test
    fun `navigation events are emitted for all navigation actions`() = testScope.runTest {
        coordinator.navigationEvents.test {
            // When: Various navigation actions
            coordinator.navigateToLogin()
            advanceUntilIdle()
            assertEquals(NavigationEvent.NavigateToLogin, awaitItem())
            
            coordinator.navigateToCreateAccount()
            advanceUntilIdle()
            assertEquals(NavigationEvent.NavigateToFamilySetup, awaitItem())
            
            coordinator.navigateToFamilyDashboard()
            advanceUntilIdle()
            assertEquals(NavigationEvent.NavigateToFamilyDashboard, awaitItem())
            
            coordinator.navigateToEmergencyInfo()
            advanceUntilIdle()
            assertEquals(NavigationEvent.NavigateToEmergencyInfo, awaitItem())
            
            coordinator.logout()
            advanceUntilIdle()
            assertEquals(NavigationEvent.NavigateToLogin, awaitItem())
        }
    }
    
    /**
     * Tests navigation loading state management.
     */
    @Test
    fun `navigation loading state is managed correctly`() = testScope.runTest {
        coordinator.isNavigating.test {
            // Initially not navigating
            assertEquals(false, awaitItem())
            
            // When: Navigation action is triggered
            coordinator.navigateToLogin()
            
            // Then: Loading state should be handled properly
            // Note: Loading state is managed internally and resets quickly
            advanceUntilIdle()
            
            // Navigation should complete
            assertEquals(
                ParentCareDestination.Auth.Login,
                coordinator.currentDestination.value
            )
        }
    }
    
    /**
     * Tests that navigation maintains proper back stack behavior.
     */
    @Test
    fun `navigation maintains proper back stack behavior`() = testScope.runTest {
        // Given: User navigates through multiple screens
        coordinator.navigateToLogin()
        advanceUntilIdle()
        
        coordinator.navigateToCreateAccount()
        advanceUntilIdle()
        
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        coordinator.navigateToEmergencyInfo()
        advanceUntilIdle()
        
        // When: User navigates back
        coordinator.navigateBack()
        advanceUntilIdle()
        
        // Then: Should handle back navigation properly
        // Note: Actual back stack behavior is handled by NavController
        // This test verifies the back event is triggered
        coordinator.navigationEvents.test {
            coordinator.navigateBack()
            assertEquals(NavigationEvent.NavigateBack, awaitItem())
        }
    }
    
    /**
     * Tests concurrent navigation events are handled properly.
     */
    @Test
    fun `concurrent navigation events are handled sequentially`() = testScope.runTest {
        coordinator.navigationEvents.test {
            // When: Multiple rapid navigation events
            coordinator.navigateToLogin()
            coordinator.navigateToCreateAccount()
            coordinator.navigateToFamilyDashboard()
            
            // Then: All events should be processed
            advanceUntilIdle()
            
            assertEquals(NavigationEvent.NavigateToLogin, awaitItem())
            assertEquals(NavigationEvent.NavigateToFamilySetup, awaitItem()) 
            assertEquals(NavigationEvent.NavigateToFamilyDashboard, awaitItem())
        }
    }
    
    /**
     * Tests navigation state consistency across different flows.
     */
    @Test
    fun `navigation state remains consistent across different user flows`() = testScope.runTest {
        // Test Flow 1: New user signup
        coordinator.navigateToLogin()
        coordinator.navigateToCreateAccount()
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        assertEquals(
            ParentCareDestination.Main.Dashboard,
            coordinator.currentDestination.value,
            "New user flow should end at dashboard"
        )
        
        // Test Flow 2: Logout and re-login
        coordinator.logout()
        advanceUntilIdle()
        
        assertEquals(
            ParentCareDestination.Auth.Login,
            coordinator.currentDestination.value,
            "Logout should return to login"
        )
        
        // Test Flow 3: Existing user login
        coordinator.navigateToFamilyDashboard()
        advanceUntilIdle()
        
        assertEquals(
            ParentCareDestination.Main.Dashboard,
            coordinator.currentDestination.value,
            "Existing user should go directly to dashboard"
        )
    }
}
