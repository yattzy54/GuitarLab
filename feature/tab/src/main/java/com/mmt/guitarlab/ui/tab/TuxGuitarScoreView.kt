package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import kotlin.collections.chunked

/**
 * TuxGuitar Paginated Score View (TGSongView Canvas with authentic Caret Cursor):
 */
@Composable
 fun TuxGuitarScoreView(
    score: TabScore,
    trackIndex: Int,
    zoomScale: Float,
    isPlaying: Boolean,
    playbackMeasure: Int,
    playbackBeat: Int,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onCellSelected: (measureIdx: Int, beatIdx: Int, stringIdx: Int) -> Unit,
) {
    val activeTrack = score.tracks.getOrNull(trackIndex) ?: score.tracks.firstOrNull()
    val measures = activeTrack?.measures ?: emptyList()
    val stringLabels = activeTrack?.stringLabels ?: InstrumentType.GUITAR.defaultStringLabels
    val stringCount = activeTrack?.stringCount ?: 6

    val measuresPerLine = (2 / zoomScale).toInt().coerceIn(1, 4)
    val measureChunkedLines = remember(measures, measuresPerLine) {
        measures.chunked(measuresPerLine)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(measureChunkedLines) { lineIndex, measureChunk ->
            val startMeasureIndex = lineIndex * measuresPerLine

            TuxGuitarSystemRow(
                measures = measureChunk,
                startMeasureIndex = startMeasureIndex,
                stringCount = stringCount,
                stringLabels = stringLabels,
                zoomScale = zoomScale,
                isPlaying = isPlaying,
                playbackMeasure = playbackMeasure,
                playbackBeat = playbackBeat,
                selectedMeasure = selectedMeasure,
                selectedBeat = selectedBeat,
                selectedString = selectedString,
                onCellSelected = onCellSelected,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuxGuitarScoreViewPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            TuxGuitarScoreView(
                score = TabScore(
                    title = "BUZZ OF US",
                    artist = "BuzzÖuter",
                    tempo = 140,
                    tracks = listOf(
                        TabTrack(
                            name = "Lead Guitar",
                            instrumentType = InstrumentType.GUITAR,
                            tuningName = "Standard E",
                            measures = listOf(
                                TabMeasure(1),
                                TabMeasure(2),
                                TabMeasure(3),
                                TabMeasure(4)
                            )
                        )
                    )
                ),
                trackIndex = 0,
                zoomScale = 1.0f,
                isPlaying = false,
                playbackMeasure = 0,
                playbackBeat = 0,
                selectedMeasure = 0,
                selectedBeat = 0,
                selectedString = 0,
                onCellSelected = { _, _, _ -> }
            )
        }
    }
}