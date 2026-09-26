package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private enum class PracticeTab(val label: String, val icon: ImageVector) {
    Fretboard("Fretboard & Chords", Icons.Default.GridOn),
    SlowDowner("Slow-Downer", Icons.Default.SlowMotionVideo),
    Recorder("Riff Recorder", Icons.Default.Mic),
    Tracker("Session Tracker", Icons.Default.Timeline),
}

@Composable
fun PracticeLabScreen() {
    var selectedTabIdx by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIdx,
            edgePadding = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PracticeTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                    text = { Text(tab.label) },
                )
            }
        }

        when (PracticeTab.entries[selectedTabIdx]) {
            PracticeTab.Fretboard -> ChordScaleScreen()
            PracticeTab.SlowDowner -> SlowDownerScreen()
            PracticeTab.Recorder -> RiffRecorderScreen()
            PracticeTab.Tracker -> PracticeTrackerScreen()
        }
    }
}
