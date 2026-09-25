package com.mmt.guitarlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Studio Dark Palette - Consistent across all screens
val StudioDarkBg = Color(0xFF0C0F15)
val StudioCardBg = Color(0xFF141822)
val StudioCardElevated = Color(0xFF1B212F)
val StudioCardBorder = Color(0xFF252D3E)
val StudioCardBorderLight = Color(0xFF333E56)

val ElectricAmber = Color(0xFFFFB703)
val ElectricAmberGlow = Color(0xFFFFCA3A)
val ElectricTeal = Color(0xFF00E5FF)
val ElectricGreen = Color(0xFF00E676)
val ElectricRuby = Color(0xFFFF3366)
val StudioTextPrimary = Color(0xFFF1F3F9)
val StudioTextSecondary = Color(0xFF94A0B8)
val StudioTextMuted = Color(0xFF64748B)

// 3D Gradients
val Amber3DGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFD166), Color(0xFFFFB703), Color(0xFFD48B00))
)
val Teal3DGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF80F5FF), Color(0xFF00E5FF), Color(0xFF009BB0))
)
val Green3DGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF69F0AE), Color(0xFF00E676), Color(0xFF00A850))
)
val Ruby3DGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFF6B8B), Color(0xFFFF3366), Color(0xFFB80036))
)
val Card3DGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF22293A), Color(0xFF151A24))
)

private val DarkColors = darkColorScheme(
    primary = ElectricAmber,
    onPrimary = Color(0xFF120E00),
    primaryContainer = Color(0xFF382600),
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = ElectricTeal,
    onSecondary = Color(0xFF003138),
    secondaryContainer = Color(0xFF004D58),
    onSecondaryContainer = Color(0xFFB2F5FF),
    tertiary = ElectricGreen,
    onTertiary = Color(0xFF003817),
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioCardBg,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioCardElevated,
    onSurfaceVariant = StudioTextSecondary,
    surfaceContainerHigh = StudioCardElevated,
    outline = StudioCardBorder,
    error = ElectricRuby,
)

@Composable
fun GuitarLabTheme(
    darkTheme: Boolean = true, // Force premium dark studio theme for guitar app
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
