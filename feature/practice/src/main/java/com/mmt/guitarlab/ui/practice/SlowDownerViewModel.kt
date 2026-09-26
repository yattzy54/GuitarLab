package com.mmt.guitarlab.ui.practice

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mmt.guitarlab.audio.slowdown.AudioSlowDownerEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SlowDownerViewModel @Inject constructor(
    private val engine: AudioSlowDownerEngine,
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = engine.isPlaying
    val positionMs: StateFlow<Long> = engine.positionMs
    val durationMs: StateFlow<Long> = engine.durationMs
    val speed: StateFlow<Float> = engine.speed
    val loopA: StateFlow<Long> = engine.loopA
    val loopB: StateFlow<Long> = engine.loopB
    val loopEnabled: StateFlow<Boolean> = engine.loopEnabled
    val trackTitle: StateFlow<String?> = engine.trackTitle

    fun loadAudio(context: Context, uri: Uri, title: String) {
        engine.loadAudio(context, uri, title)
    }

    fun togglePlayPause() = engine.togglePlayPause()

    fun seekTo(positionMs: Long) = engine.seekTo(positionMs)

    fun setSpeed(speed: Float) = engine.setSpeed(speed)

    fun setLoopA() = engine.setLoopA()

    fun setLoopB() = engine.setLoopB()

    fun toggleLoopEnabled() = engine.toggleLoopEnabled()

    fun clearLoop() = engine.clearLoop()

    override fun onCleared() {
        engine.releasePlayer()
        super.onCleared()
    }
}
