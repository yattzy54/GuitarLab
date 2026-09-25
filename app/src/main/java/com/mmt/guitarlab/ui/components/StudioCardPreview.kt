package com.mmt.guitarlab.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mmt.guitarlab.ui.theme.GuitarLabTheme

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun StudioCardPreview() {
    GuitarLabTheme {
        StudioCard {
            StudioPill(
                text = "Preview",
                selected = true,
                onClick = {},
                modifier = androidx.compose.ui.Modifier,
            )
        }
    }
}