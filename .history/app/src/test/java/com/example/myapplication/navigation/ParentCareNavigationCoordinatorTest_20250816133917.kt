package com.example.myapplication.navigation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlinx.coroutines.Dispatchers

/**
 * Comprehensive unit tests for ParentCareNavigationCoordinator.
 * 
 * Tests navigation event handling, state management, and error scenarios
 * to ensure robust navigation behavior. Follows FAANG testing standards
 * with thorough coverage of business logic and edge cases.
 * 
 * Test Categories:
 * - Navigation event processing and validation
 * - State management during navigation
 * - Error handling and recovery scenarios
 * - Concurrent navigation request handling
 * - Destination state tracking
 * 
 * Testing Strategy:
 * - Uses Turbine for Flow testing with proper coroutine handling
 * - Tests both success and failure scenarios
 * - Validates state consistency across navigation operations
 * - Ensures thread safety with concurrent operations
 * 
 * @since 1.0.0
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ParentCareNavigationCoordinatorTest {
    
    private lateinit var coordinator: ParentCareNavigationCoordinator
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coordinator = ParentCareNavigationCoordinator()
    }
    
    /**
     * Tests basic navigation event emission and state updates.
     * 
     * Verifies that navigation events are properly emitted through the
     * SharedFlow and that current destination state is updated correctly.
     */
    @Test
    fun `onNavigationEvent emits navigation event and updates state`() = runTest {
        val destination = ParentCareDestination.Main.Dashboard
        val event = NavigationEvent.NavigateTo(destination)
        
        coordinator.navigationEvents.test {
            coordinator.onNavigationEvent(event)
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Verify event emission
            val emittedEvent = awaitItem()
            assertEquals(event, emittedEvent)
            
            // Verify state update
            assertEquals(destination, coordinator.currentDestination.value)
        }
    }
    
    /**
     * Tests navigation blocking during ongoing navigation operations.
     * 
     * Ensures that concurrent navigation requests are properly handled
     * and that navigation state prevents inconsistent operations.
     */
    @Test
    fun `concurrent navigation events are blocked during navigation`() = runTest {
        val firstEvent = NavigationEvent.NavigateTo(ParentCareDestination.Main.Dashboard)
        val secondEvent = NavigationEvent.NavigateTo(ParentCareDestination.Main.Emergency)
        
        coordinator.navigationEvents.test {
            // Start first navigation
            coordinator.onNavigationEvent(firstEvent)
            
            // Attempt second navigation before first completes
            coordinator.onNavigationEvent(secondEvent)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Should only receive first event
            val emittedEvent = awaitItem()
            assertEquals(firstEvent, emittedEvent)
            
            // Should not receive second event
            expectNoEvents()
        }
    }
    
    /**
     * Tests navigation back functionality with state management.
     * 
     * Verifies that back navigation events are properly handled and
     * that fallback destinations work when back stack is empty.
     */
    @Test
    fun `navigateBack emits correct navigation event`() = runTest {
        coordinator.navigationEvents.test {
            coordinator.navigateBack()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val emittedEvent = awaitItem()
            assertTrue("Expected NavigateBack event", emittedEvent is NavigationEvent.NavigateBack)
            
            val backEvent = emittedEvent as NavigationEvent.NavigateBack
            assertEquals(
                ParentCareDestination.Main.Dashboard,
                backEvent.fallbackDestination
            )
        }
    }
    
    /**
     * Tests family dashboard navigation with proper back stack clearing.
     * 
     * Verifies that navigating to the main dashboard clears the back stack
     * to prevent returning to development/auth screens.
     */
    @Test
    fun `navigateToFamilyDashboard clears back stack correctly`() = runTest {
        coordinator.navigationEvents.test {
            coordinator.navigateToFamilyDashboard()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val emittedEvent = awaitItem()
            assertTrue("Expected NavigateAndClearTo event", 
                emittedEvent is NavigationEvent.NavigateAndClearTo)
            
            val clearEvent = emittedEvent as NavigationEvent.NavigateAndClearTo
            assertEquals(ParentCareDestination.Main.Dashboard, clearEvent.destination)
            assertEquals(ParentCareDestination.System.DevMode, clearEvent.popUpTo)
        }
    }
    
    /**
     * Tests emergency navigation functionality.
     * 
     * Verifies that emergency navigation works correctly for urgent scenarios
     * requiring quick access to critical information.
     */
    @Test
    fun `navigateToEmergencyInfo navigates to emergency destination`() = runTest {
        coordinator.navigationEvents.test {
            coordinator.navigateToEmergencyInfo()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val emittedEvent = awaitItem()
            assertTrue("Expected NavigateTo event", emittedEvent is NavigationEvent.NavigateTo)
            
            val navigateEvent = emittedEvent as NavigationEvent.NavigateTo
            assertEquals(ParentCareDestination.Main.Emergency, navigateEvent.destination)
            assertFalse("Should not clear back stack", navigateEvent.clearBackStack)
        }
    }
    
    /**
     * Tests reports navigation functionality.
     * 
     * Verifies navigation to medication adherence reports and care analytics
     * for family coordination insights.
     */
    @Test
    fun `navigateToReports navigates to reports destination`() = runTest {
        coordinator.navigationEvents.test {
            coordinator.navigateToReports()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val emittedEvent = awaitItem()
            assertTrue("Expected NavigateTo event", emittedEvent is NavigationEvent.NavigateTo)
            
            val navigateEvent = emittedEvent as NavigationEvent.NavigateTo
            assertEquals(ParentCareDestination.Main.Reports, navigateEvent.destination)
        }
    }
    
    /**
     * Tests navigation state consistency during complex operations.
     * 
     * Verifies that isNavigating state is properly managed during navigation
     * operations to prevent UI inconsistencies and race conditions.
     */
    @Test
    fun `isNavigating state is managed correctly during navigation`() = runTest {
        // Initial state should be false
        assertFalse("Initial navigation state should be false", 
            coordinator.isNavigating.value)
        
        coordinator.navigationEvents.test {
            coordinator.onNavigationEvent(
                NavigationEvent.NavigateTo(ParentCareDestination.Main.Dashboard)
            )
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // After navigation completes, state should be false again
            assertFalse("Navigation state should be false after completion", 
                coordinator.isNavigating.value)
            
            awaitItem() // Consume the navigation event
        }
    }
    
    /**
     * Tests destination state tracking accuracy.
     * 
     * Verifies that currentDestination state accurately reflects the current
     * screen location for proper UI state management.
     */
    @Test
    fun `currentDestination tracks navigation accurately`() = runTest {
        val emergencyDestination = ParentCareDestination.Main.Emergency
        val reportsDestination = ParentCareDestination.Main.Reports
        
        coordinator.navigationEvents.test {
            // Navigate to emergency
            coordinator.onNavigationEvent(NavigationEvent.NavigateTo(emergencyDestination))
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // Consume event
            
            assertEquals("Should track emergency destination", 
                emergencyDestination, coordinator.currentDestination.value)
            
            // Navigate to reports
            coordinator.onNavigationEvent(NavigationEvent.NavigateTo(reportsDestination))
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // Consume event
            
            assertEquals("Should track reports destination", 
                reportsDestination, coordinator.currentDestination.value)
        }
    }
    
    /**
     * Tests edge case handling and error scenarios.
     * 
     * Verifies graceful handling of edge cases like null destinations
     * and malformed navigation events.
     */
    @Test
    fun `handles edge cases gracefully`() = runTest {
        coordinator.navigationEvents.test {
            // Test with NavigateBack that has null fallback
            val backEvent = NavigationEvent.NavigateBack(fallbackDestination = null)
            coordinator.onNavigationEvent(backEvent)
            testDispatcher.scheduler.advanceUntilIdle()
            
            val emittedEvent = awaitItem()
            assertEquals(backEvent, emittedEvent)
            
            // Coordinator should handle this gracefully without crashing
            expectNoEvents()
        }
    }
}
