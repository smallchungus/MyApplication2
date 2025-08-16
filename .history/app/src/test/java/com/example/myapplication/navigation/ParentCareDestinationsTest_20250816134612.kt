package com.example.myapplication.navigation

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ParentCare navigation destinations and events.
 * 
 * Validates route definitions, parameter handling, and type safety
 * of navigation destinations. Ensures that navigation routes are
 * correctly defined and parameterized routes work as expected.
 * 
 * Test Coverage:
 * - Route string validation for all destinations
 * - Parameterized route creation and parsing
 * - Navigation event type safety and equality
 * - Hierarchical destination structure validation
 * 
 * Design Validation:
 * Tests ensure that the sealed class hierarchy provides proper
 * type safety and that route names follow consistent patterns
 * for maintainable navigation architecture.
 * 
 * @since 1.0.0
 */
class ParentCareDestinationsTest {
    
    /**
     * Tests that all destination routes have correct string values.
     * 
     * Validates that route strings follow naming conventions and
     * don't contain invalid characters or patterns that could
     * cause navigation failures.
     */
    @Test
    fun `destination routes have correct string values`() {
        // Auth destinations
        assertEquals("welcome", ParentCareDestination.Auth.Welcome.route)
        assertEquals("login", ParentCareDestination.Auth.Login.route)
        assertEquals("family_setup", ParentCareDestination.Auth.FamilySetup.route)
        
        // Main destinations
        assertEquals("dashboard", ParentCareDestination.Main.Dashboard.route)
        assertEquals("assignment_detail/{assignmentId}", 
            ParentCareDestination.Main.AssignmentDetail.route)
        assertEquals("emergency", ParentCareDestination.Main.Emergency.route)
        assertEquals("reports", ParentCareDestination.Main.Reports.route)
        assertEquals("family_settings", ParentCareDestination.Main.FamilySettings.route)
        
        // System destinations
        assertEquals("error", ParentCareDestination.System.Error.route)
        assertEquals("settings", ParentCareDestination.System.Settings.route)
        assertEquals("dev_mode", ParentCareDestination.System.DevMode.route)
    }
    
    /**
     * Tests parameterized route creation functionality.
     * 
     * Verifies that parameterized routes correctly substitute
     * parameters and create valid navigation routes.
     */
    @Test
    fun `parameterized routes create correct paths`() {
        val assignmentId = "assignment_123"
        val expectedRoute = "assignment_detail/assignment_123"
        
        val actualRoute = ParentCareDestination.Main.AssignmentDetail.createRoute(assignmentId)
        
        assertEquals("Parameterized route should substitute ID correctly", 
            expectedRoute, actualRoute)
    }
    
    /**
     * Tests parameterized route edge cases.
     * 
     * Ensures parameterized routes handle edge cases like empty
     * strings, special characters, and very long IDs gracefully.
     */
    @Test
    fun `parameterized routes handle edge cases`() {
        // Empty string
        val emptyRoute = ParentCareDestination.Main.AssignmentDetail.createRoute("")
        assertEquals("assignment_detail/", emptyRoute)
        
        // Special characters (URL encoded in real navigation)
        val specialCharsId = "assignment@123#test"
        val specialRoute = ParentCareDestination.Main.AssignmentDetail.createRoute(specialCharsId)
        assertEquals("assignment_detail/assignment@123#test", specialRoute)
        
        // Very long ID
        val longId = "a".repeat(100)
        val longRoute = ParentCareDestination.Main.AssignmentDetail.createRoute(longId)
        assertTrue("Long route should contain the ID", longRoute.contains(longId))
    }
    
    /**
     * Tests navigation event equality and type safety.
     * 
     * Verifies that navigation events can be compared correctly
     * and that sealed class type checking works as expected.
     */
    @Test
    fun `navigation events support equality and type checking`() {
        val destination1 = ParentCareDestination.Main.Dashboard
        val destination2 = ParentCareDestination.Main.Dashboard
        val destination3 = ParentCareDestination.Main.Emergency
        
        // Test NavigateTo event equality
        val event1 = NavigationEvent.NavigateTo(destination1)
        val event2 = NavigationEvent.NavigateTo(destination2)
        val event3 = NavigationEvent.NavigateTo(destination3)
        
        assertEquals("Same destination events should be equal", event1, event2)
        assertNotEquals("Different destination events should not be equal", event1, event3)
        
        // Test NavigateBack event equality
        val backEvent1 = NavigationEvent.NavigateBack(destination1)
        val backEvent2 = NavigationEvent.NavigateBack(destination1)
        val backEvent3 = NavigationEvent.NavigateBack(destination3)
        
        assertEquals("Same fallback events should be equal", backEvent1, backEvent2)
        assertNotEquals("Different fallback events should not be equal", backEvent1, backEvent3)
    }
    
    /**
     * Tests navigation event type hierarchy.
     * 
     * Validates that all navigation events are properly sealed
     * and support exhaustive when statements for type safety.
     */
    @Test
    fun `navigation events form proper sealed hierarchy`() {
        val navigateEvent = NavigationEvent.NavigateTo(ParentCareDestination.Main.Dashboard)
        val backEvent = NavigationEvent.NavigateBack()
        val clearEvent = NavigationEvent.NavigateAndClearTo(
            ParentCareDestination.Main.Dashboard,
            ParentCareDestination.System.DevMode
        )
        
        // Test exhaustive when coverage (compilation test)
        fun handleEvent(event: NavigationEvent): String = when (event) {
            is NavigationEvent.NavigateTo -> "navigate"
            is NavigationEvent.NavigateBack -> "back"
            is NavigationEvent.NavigateAndClearTo -> "clear"
        }
        
        assertEquals("navigate", handleEvent(navigateEvent))
        assertEquals("back", handleEvent(backEvent))
        assertEquals("clear", handleEvent(clearEvent))
    }
    
    /**
     * Tests destination hierarchy and inheritance.
     * 
     * Verifies that destination classes properly inherit from
     * ParentCareDestination and maintain type safety.
     */
    @Test
    fun `destinations maintain proper type hierarchy`() {
        val authDest: ParentCareDestination = ParentCareDestination.Auth.Welcome
        val mainDest: ParentCareDestination = ParentCareDestination.Main.Dashboard
        val systemDest: ParentCareDestination = ParentCareDestination.System.DevMode
        
        // Test that all destinations are ParentCareDestination instances
        assertTrue("Auth destination should be ParentCareDestination", 
            authDest is ParentCareDestination)
        assertTrue("Main destination should be ParentCareDestination", 
            mainDest is ParentCareDestination)
        assertTrue("System destination should be ParentCareDestination", 
            systemDest is ParentCareDestination)
        
        // Test specific type checking
        assertTrue("Should be Auth.Welcome", authDest is ParentCareDestination.Auth.Welcome)
        assertTrue("Should be Main.Dashboard", mainDest is ParentCareDestination.Main.Dashboard)
        assertTrue("Should be System.DevMode", systemDest is ParentCareDestination.System.DevMode)
    }
    
    /**
     * Tests route naming consistency and conventions.
     * 
     * Validates that route names follow consistent patterns
     * and naming conventions for maintainability.
     */
    @Test
    fun `route names follow consistent conventions`() {
        val allRoutes = listOf(
            ParentCareDestination.Auth.Welcome.route,
            ParentCareDestination.Auth.Login.route,
            ParentCareDestination.Auth.FamilySetup.route,
            ParentCareDestination.Main.Dashboard.route,
            ParentCareDestination.Main.Emergency.route,
            ParentCareDestination.Main.Reports.route,
            ParentCareDestination.Main.FamilySettings.route,
            ParentCareDestination.System.Error.route,
            ParentCareDestination.System.Settings.route,
            ParentCareDestination.System.DevMode.route
        )
        
        // Test that all routes are lowercase (except parameters)
        allRoutes.forEach { route ->
            val routeWithoutParams = route.replace(Regex("\\{[^}]+}"), "")
            assertEquals("Route should be lowercase: $route", 
                routeWithoutParams.lowercase(), routeWithoutParams)
        }
        
        // Test that multi-word routes use underscores
        assertTrue("FamilySetup should use underscore", 
            ParentCareDestination.Auth.FamilySetup.route.contains("_"))
        assertTrue("FamilySettings should use underscore", 
            ParentCareDestination.Main.FamilySettings.route.contains("_"))
        assertTrue("DevMode should use underscore", 
            ParentCareDestination.System.DevMode.route.contains("_"))
    }
    
    /**
     * Tests NavigateAndClearTo event functionality.
     * 
     * Verifies that the NavigateAndClearTo event properly handles
     * destination and popUpTo parameters for back stack management.
     */
    @Test
    fun `NavigateAndClearTo handles destination and popUpTo correctly`() {
        val destination = ParentCareDestination.Main.Dashboard
        val popUpTo = ParentCareDestination.System.DevMode
        
        val event = NavigationEvent.NavigateAndClearTo(destination, popUpTo)
        
        assertEquals("Destination should match", destination, event.destination)
        assertEquals("PopUpTo should match", popUpTo, event.popUpTo)
        
        // Test equality
        val event2 = NavigationEvent.NavigateAndClearTo(destination, popUpTo)
        assertEquals("Events with same parameters should be equal", event, event2)
        
        val event3 = NavigationEvent.NavigateAndClearTo(
            ParentCareDestination.Main.Emergency, 
            popUpTo
        )
        assertNotEquals("Events with different destinations should not be equal", event, event3)
    }
}
