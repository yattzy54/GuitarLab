package com.mmt.guitarlab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

/**
 * Modern Studio Pill for mode toggles, tuning selectors, subdivisions
 */
@Composable
fun StudioPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    accentColor: Color = ElectricAmber,
) {
    val bgBrush = if (selected) {
        Brush.verticalGradient(
            colors = listOf(accentColor, accentColor.copy(alpha = 0.82f)),
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF222838), Color(0xFF161B26)),
        )
    }

    val contentColor = if (selected) Color(0xFF101216) else StudioTextSecondary
    val borderColor = if (selected) accentColor.copy(alpha = 0.9f) else StudioCardBorder

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush = bgBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        if (selected) Color.White.copy(alpha = 0.4f) else borderColor,
                        borderColor.copy(alpha = 0.4f),
                    ),
                ),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
        )
    }
}