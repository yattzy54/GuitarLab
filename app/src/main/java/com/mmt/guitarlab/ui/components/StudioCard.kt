package com.mmt.guitarlab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated

/**
 * 3D Studio Card with raised border and deep dark surface
 */
@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    accentBorder: Color? = null,
    content: @Composable () -> Unit,
) {
    val borderColor = accentBorder ?: StudioCardBorder
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(StudioCardElevated, StudioCardBg),
                ),
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.8f),
                        StudioCardBorder.copy(alpha = 0.3f),
                    ),
                ),
                shape = shape,
            ),
    ) {
        content()
    }
}