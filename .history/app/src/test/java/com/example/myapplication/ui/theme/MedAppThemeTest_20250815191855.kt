package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*

class MedAppThemeTest {

    @Test
    fun `medical blue color has correct hex value`() {
        // Arrange & Act
        val expectedColor = Color(0xFF1976D2)
        
        // Assert
        assertEquals("Medical blue should match expected hex value", expectedColor, MedicalBlue)
    }

    @Test
    fun `medical teal color has correct hex value`() {
        // Arrange & Act
        val expectedColor = Color(0xFF00695C)
        
        // Assert
        assertEquals("Medical teal should match expected hex value", expectedColor, MedicalTeal)
    }

    @Test
    fun `medical green color has correct hex value`() {
        // Arrange & Act
        val expectedColor = Color(0xFF2E7D32)
        
        // Assert
        assertEquals("Medical green should match expected hex value", expectedColor, MedicalGreen)
    }

    @Test
    fun `medical red color has correct hex value`() {
        // Arrange & Act
        val expectedColor = Color(0xFFD32F2F)
        
        // Assert
        assertEquals("Medical red should match expected hex value", expectedColor, MedicalRed)
    }

    @Test
    fun `light color scheme primary is medical blue`() {
        // Arrange & Act
        val primaryColor = LightColorScheme.primary
        
        // Assert
        assertEquals("Light theme primary should be medical blue", MedicalBlue, primaryColor)
    }

    @Test
    fun `light color scheme secondary is medical teal`() {
        // Arrange & Act
        val secondaryColor = LightColorScheme.secondary
        
        // Assert
        assertEquals("Light theme secondary should be medical teal", MedicalTeal, secondaryColor)
    }

    @Test
    fun `light color scheme tertiary is medical green`() {
        // Arrange & Act
        val tertiaryColor = LightColorScheme.tertiary
        
        // Assert
        assertEquals("Light theme tertiary should be medical green", MedicalGreen, tertiaryColor)
    }

    @Test
    fun `light color scheme error is medical red`() {
        // Arrange & Act
        val errorColor = LightColorScheme.error
        
        // Assert
        assertEquals("Light theme error should be medical red", MedicalRed, errorColor)
    }

    @Test
    fun `dark color scheme primary is medical blue`() {
        // Arrange & Act
        val primaryColor = DarkColorScheme.primary
        
        // Assert
        assertEquals("Dark theme primary should be medical blue", MedicalBlue, primaryColor)
    }

    @Test
    fun `dark color scheme secondary is medical teal`() {
        // Arrange & Act
        val secondaryColor = DarkColorScheme.secondary
        
        // Assert
        assertEquals("Dark theme secondary should be medical teal", MedicalTeal, secondaryColor)
    }

    @Test
    fun `dark color scheme tertiary is medical green`() {
        // Arrange & Act
        val tertiaryColor = DarkColorScheme.tertiary
        
        // Assert
        assertEquals("Dark theme tertiary should be medical green", MedicalGreen, tertiaryColor)
    }

    @Test
    fun `dark color scheme error is medical red`() {
        // Arrange & Act
        val errorColor = DarkColorScheme.error
        
        // Assert
        assertEquals("Dark theme error should be medical red", MedicalRed, errorColor)
    }

    @Test
    fun `typography display large has correct font size`() {
        // Arrange & Act
        val fontSize = MedAppTypography.displayLarge.fontSize
        
        // Assert
        assertNotNull("Display large font size should not be null", fontSize)
        assertEquals("Display large should have 57sp font size", 57, fontSize.value.toInt())
    }

    @Test
    fun `typography headline large has correct font size`() {
        // Arrange & Act
        val fontSize = MedAppTypography.headlineLarge.fontSize
        
        // Assert
        assertNotNull("Headline large font size should not be null", fontSize)
        assertEquals("Headline large should have 32sp font size", 32, fontSize.value.toInt())
    }

    @Test
    fun `typography title large has correct font size`() {
        // Arrange & Act
        val fontSize = MedAppTypography.titleLarge.fontSize
        
        // Assert
        assertNotNull("Title large font size should not be null", fontSize)
        assertEquals("Title large should have 22sp font size", 22, fontSize.value.toInt())
    }

    @Test
    fun `typography body large has correct font size`() {
        // Arrange & Act
        val fontSize = MedAppTypography.bodyLarge.fontSize
        
        // Assert
        assertNotNull("Body large font size should not be null", fontSize)
        assertEquals("Body large should have 18sp font size", 18, fontSize.value.toInt())
    }

    @Test
    fun `typography label large has correct font size`() {
        // Arrange & Act
        val fontSize = MedAppTypography.labelLarge.fontSize
        
        // Assert
        assertNotNull("Label large font size should not be null", fontSize)
        assertEquals("Label large should have 16sp font size", 16, fontSize.value.toInt())
    }

    @Test
    fun `light color scheme has high contrast colors`() {
        // Arrange & Act
        val background = LightColorScheme.background
        val onBackground = LightColorScheme.onBackground
        
        // Assert
        assertNotEquals("Background and onBackground should have different colors for contrast", 
                       background, onBackground)
    }

    @Test
    fun `dark color scheme has high contrast colors`() {
        // Arrange & Act
        val background = DarkColorScheme.background
        val onBackground = DarkColorScheme.onBackground
        
        // Assert
        assertNotEquals("Background and onBackground should have different colors for contrast", 
                       background, onBackground)
    }
}
