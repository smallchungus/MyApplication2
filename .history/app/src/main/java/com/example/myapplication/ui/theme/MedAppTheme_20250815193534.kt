package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// Custom medical color palette
val MedicalBlue = Color(0xFF1976D2)
val MedicalBlueVariant = Color(0xFF1565C0)
val MedicalTeal = Color(0xFF00695C)
val MedicalGreen = Color(0xFF2E7D32)
val MedicalRed = Color(0xFFD32F2F)
val MedicalOrange = Color(0xFFF57C00)

// Light color scheme
val LightColorScheme = lightColorScheme(
    primary = MedicalBlue,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueVariant,
    onPrimaryContainer = Color.White,
    secondary = MedicalTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = MedicalGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),
    error = MedicalRed,
    onError = Color.White,
    errorContainer = Color(0xFFFDE7E7),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3F3F3),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

// Dark color scheme
val DarkColorScheme = darkColorScheme(
    primary = MedicalBlue,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueVariant,
    onPrimaryContainer = Color.White,
    secondary = MedicalTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = MedicalGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color(0xFFC8E6C9),
    error = MedicalRed,
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFDE7E7),
    background = Color(0xFF1C1B1F),
    onBackground = Color.White,
    surface = Color(0xFF2F2F2F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3C3C3C),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

// Custom typography for accessibility (senior-friendly)
val MedAppTypography = androidx.compose.material3.Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontSize = sp(57),
        lineHeight = sp(64),
        letterSpacing = sp(-0.25)
    ),
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontSize = sp(45),
        lineHeight = sp(52),
        letterSpacing = sp(0)
    ),
    displaySmall = androidx.compose.ui.text.TextStyle(
        fontSize = sp(36),
        lineHeight = sp(44),
        letterSpacing = sp(0)
    ),
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontSize = sp(32),
        lineHeight = sp(40),
        letterSpacing = sp(0)
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontSize = sp(28),
        lineHeight = sp(36),
        letterSpacing = sp(0)
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontSize = sp(24),
        lineHeight = sp(32),
        letterSpacing = sp(0)
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontSize = sp(22),
        lineHeight = sp(28),
        letterSpacing = sp(0)
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontSize = sp(18),
        lineHeight = sp(24),
        letterSpacing = sp(0.15)
    ),
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontSize = sp(16),
        lineHeight = sp(22),
        letterSpacing = sp(0.1)
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontSize = sp(18),
        lineHeight = sp(24),
        letterSpacing = sp(0.5)
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = sp(16),
        lineHeight = sp(22),
        letterSpacing = sp(0.25)
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontSize = sp(14),
        lineHeight = sp(20),
        letterSpacing = sp(0.4)
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontSize = sp(16),
        lineHeight = sp(22),
        letterSpacing = sp(0.1)
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontSize = sp(14),
        lineHeight = sp(20),
        letterSpacing = sp(0.1)
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontSize = sp(12),
        lineHeight = sp(16),
        letterSpacing = sp(0.5)
    )
)

// CompositionLocal for testing access
val LocalMedAppColors = staticCompositionLocalOf { LightColorScheme }
val LocalMedAppTypography = staticCompositionLocalOf { MedAppTypography }

@Composable
fun MedAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(
        LocalMedAppColors provides colorScheme,
        LocalMedAppTypography provides MedAppTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MedAppTypography,
            content = content
        )
    }
}
