package com.mmt.guitarlab.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Premium Studio Dark Palette
private val ElectricAmber = Color(0xFFFFB703)
private val ElectricAmberContainer = Color(0xFF382600)
private val ElectricTeal = Color(0xFF00E5FF)
private val ElectricTealContainer = Color(0xFF00363D)
private val DarkStudioBackground = Color(0xFF101216)
private val DarkStudioSurface = Color(0xFF171A21)
private val DarkStudioSurfaceVariant = Color(0xFF222631)
private val DarkStudioSurfaceContainerHigh = Color(0xFF2A2F3D)

private val DarkColors = darkColorScheme(
    primary = ElectricAmber,
    onPrimary = Color(0xFF261800),
    primaryContainer = ElectricAmberContainer,
    onPrimaryContainer = Color(0xFFFFE082),
    secondary = ElectricTeal,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = ElectricTealContainer,
    onSecondaryContainer = Color(0xFFB2F5FF),
    background = DarkStudioBackground,
    onBackground = Color(0xFFE6E8EE),
    surface = DarkStudioSurface,
    onSurface = Color(0xFFE6E8EE),
    surfaceVariant = DarkStudioSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0C5D4),
    surfaceContainerHigh = DarkStudioSurfaceContainerHigh,
    outline = Color(0xFF5A6072),
    error = Color(0xFFFF5252),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF7C5300),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF9E),
    onPrimaryContainer = Color(0xFF261800),
    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF97F0FF),
    onSecondaryContainer = Color(0xFF001F24),
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF1A1C22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C22),
    surfaceVariant = Color(0xFFE1E4ED),
    onSurfaceVariant = Color(0xFF444752),
    outline = Color(0xFF757783),
)

@Composable
fun GuitarLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
