package com.example.panalsuite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Honey600,
    onPrimary = HoneyDeep,
    primaryContainer = Honey100,
    onPrimaryContainer = HoneyDeep,
    secondary = Slate700,
    onSecondary = Color.White,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate900,
    background = Paper,
    onBackground = Slate900,
    surface = Paper,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    error = ErrorRed,
    onError = Color.White,
    outlineVariant = Color(0xFFE2DCCD),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Honey50,
    surfaceContainer = Color(0xFFF8F2E4),
    surfaceContainerHigh = Color(0xFFF3ECDC),
    surfaceContainerHighest = Color(0xFFEDE5D3)
)

private val DarkColorScheme = darkColorScheme(
    primary = Honey200,
    onPrimary = HoneyDeep,
    primaryContainer = Color(0xFF5C4300),
    onPrimaryContainer = Honey100,
    secondary = Slate100,
    onSecondary = Slate900,
    secondaryContainer = Slate700,
    onSecondaryContainer = Slate100,
    background = PaperDark,
    onBackground = Slate100,
    surface = SurfaceDark,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate100,
    error = ErrorRedLight,
    onError = Color(0xFF690005),
    outlineVariant = Color(0xFF3A4247),
    surfaceContainerLowest = SurfaceDark,
    surfaceContainerLow = Color(0xFF22292D),
    surfaceContainer = Color(0xFF262D32),
    surfaceContainerHigh = Color(0xFF2C3438),
    surfaceContainerHighest = Color(0xFF333B40)
)

private val PanalShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun PanalSuiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = PanalShapes,
        content = content
    )
}
