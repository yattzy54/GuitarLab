package com.mmt.guitarlab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

enum class Studio3DAccent {
    AMBER, TEAL, GREEN, RUBY, SLATE
}

/**
 * 3D-styled raised icon badge with specular top highlight, bevel gradient and soft glow
 */
@Composable
fun Studio3DIconBadge(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    accent: Studio3DAccent = Studio3DAccent.AMBER,
    onClick: (() -> Unit)? = null,
) {
    val (gradientColors, iconColor, glowColor) = when (accent) {
        Studio3DAccent.AMBER -> Triple(
            listOf(Color(0xFFFFD166), Color(0xFFFFB703), Color(0xFFC47D00)),
            Color(0xFF261800),
            Color(0xFFFFB703).copy(alpha = 0.35f),
        )
        Studio3DAccent.TEAL -> Triple(
            listOf(Color(0xFF80F5FF), Color(0xFF00E5FF), Color(0xFF008394)),
            Color(0xFF00272D),
            Color(0xFF00E5FF).copy(alpha = 0.35f),
        )
        Studio3DAccent.GREEN -> Triple(
            listOf(Color(0xFF69F0AE), Color(0xFF00E676), Color(0xFF008544)),
            Color(0xFF002911),
            Color(0xFF00E676).copy(alpha = 0.35f),
        )
        Studio3DAccent.RUBY -> Triple(
            listOf(Color(0xFFFF708F), Color(0xFFFF3366), Color(0xFFAD0030)),
            Color(0xFFFFFFFF),
            Color(0xFFFF3366).copy(alpha = 0.35f),
        )
        Studio3DAccent.SLATE -> Triple(
            listOf(Color(0xFF384358), Color(0xFF232A38), Color(0xFF161A24)),
            Color(0xFFCAD5E8),
            Color(0xFF000000).copy(alpha = 0.4f),
        )
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(size * 0.32f),
                ambientColor = glowColor,
                spotColor = glowColor,
            )
            .clip(RoundedCornerShape(size * 0.32f))
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.05f),
                    ),
                ),
                shape = RoundedCornerShape(size * 0.32f),
            )
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        // Specular highlight on top half
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.Transparent,
                            ),
                            center = Offset(size.toPx() * 0.35f, size.toPx() * 0.25f),
                            radius = size.toPx() * 0.55f,
                        ),
                    )
                },
        )
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(size * 0.52f),
        )
    }
}
