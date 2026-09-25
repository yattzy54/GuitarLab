package com.mmt.guitarlab.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

class ThemeTest {
    @Test
    fun xmlPaletteMatchesComposePalette() {
        val colors = mapOf(
            "guitarlab_background" to DarkColors.background,
            "guitarlab_surface" to DarkColors.surface,
            "guitarlab_surface_elevated" to DarkColors.surfaceContainerHigh,
            "guitarlab_outline" to DarkColors.outline,
            "guitarlab_primary" to DarkColors.primary,
            "guitarlab_on_primary" to DarkColors.onPrimary,
            "guitarlab_primary_variant" to DarkColors.primaryContainer,
            "guitarlab_secondary" to DarkColors.secondary,
            "guitarlab_on_secondary" to DarkColors.onSecondary,
            "guitarlab_secondary_variant" to DarkColors.secondaryContainer,
            "guitarlab_text_primary" to DarkColors.onSurface,
            "guitarlab_text_secondary" to DarkColors.onSurfaceVariant,
            "guitarlab_text_muted" to StudioTextMuted,
            "guitarlab_error" to DarkColors.error,
            "guitarlab_on_error" to DarkColors.onError,
        )
        val document = requireNotNull(javaClass.getResourceAsStream("/colors.xml")).use {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it)
        }
        val nodes = document.getElementsByTagName("color")
        assertEquals(colors.size, nodes.length)
        for (index in 0 until nodes.length) {
            val node = nodes.item(index) as Element
            val name = node.getAttribute("name")
            val rgb = node.textContent.trim().removePrefix("#").toLong(16)
            assertEquals(name, (0xff000000L or rgb).toInt(), colors.getValue(name).toArgb())
        }
    }

    @Test
    fun textAndAccentRolesHaveReadableContrast() {
        val pairs = with(DarkColors) {
            listOf(
                onBackground to background,
                onSurface to surface,
                onSurface to surfaceContainerHigh,
                onSurfaceVariant to surfaceContainerHigh,
                onPrimary to primary,
                onPrimaryContainer to primaryContainer,
                onSecondary to secondary,
                onSecondaryContainer to secondaryContainer,
                onTertiary to tertiary,
                onTertiaryContainer to tertiaryContainer,
                onError to error,
                onErrorContainer to errorContainer,
                inverseOnSurface to inverseSurface,
            )
        }
        for ((foreground, background) in pairs) {
            assertTrue("$foreground on $background", contrast(foreground, background) >= 4.5f)
        }
    }

    private fun contrast(first: Color, second: Color): Float {
        val firstLuminance = first.luminance()
        val secondLuminance = second.luminance()
        return (maxOf(firstLuminance, secondLuminance) + 0.05f) /
            (minOf(firstLuminance, secondLuminance) + 0.05f)
    }
}
