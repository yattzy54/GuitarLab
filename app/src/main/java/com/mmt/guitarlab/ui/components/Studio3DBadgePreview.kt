package com.mmt.guitarlab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.theme.GuitarLabTheme

@Preview(showBackground = true)
@Composable
private fun Studio3DBadgePreview() {
    GuitarLabTheme {
        Studio3DIconBadge(
            icon = Icons.Default.MusicNote,
            contentDescription = "Music",
            size = 56.dp,
            accent = Studio3DAccent.TEAL,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Studio3DBadge2Preview() {
    GuitarLabTheme {
        Studio3DIconBadge(
            icon = Icons.Default.AccessTime,
            contentDescription = "Music",
            size = 64.dp,
            accent = Studio3DAccent.RUBY,
        )
    }
}

