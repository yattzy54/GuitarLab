package com.mmt.guitarlab.audio.slowdown

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioSlowDownerEngine @Inject constructor() {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _speed = MutableStateFlow(1.0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _loopA = MutableStateFlow(0L)
    val loopA: StateFlow<Long> = _loopA.asStateFlow()

    private val _loopB = MutableStateFlow(0L)
    val loopB: StateFlow<Long> = _loopB.asStateFlow()

    private val _loopEnabled = MutableStateFlow(false)
    val loopEnabled: StateFlow<Boolean> = _loopEnabled.asStateFlow()

    private val _trackTitle = MutableStateFlow<String?>(null)
    val trackTitle: StateFlow<String?> = _trackTitle.asStateFlow()

    fun loadAudio(context: Context, uri: Uri, title: String) {
        releasePlayer()
        _trackTitle.value = title

        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
                _durationMs.value = duration.toLong()
                _positionMs.value = 0L
                _loopA.value = 0L
                _loopB.value = duration.toLong()
            }
            startProgressLoop()
        }
    }

    fun play() {
        val player = mediaPlayer ?: return
        runCatching {
            setSpeed(_speed.value)
            player.start()
            _isPlaying.value = true
        }
    }

    fun pause() {
        val player = mediaPlayer ?: return
        runCatching {
            player.pause()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        val player = mediaPlayer ?: return
        runCatching {
            player.seekTo(positionMs.toInt())
            _positionMs.value = positionMs
        }
    }

    fun setSpeed(speedFactor: Float) {
        val clamped = speedFactor.coerceIn(0.5f, 1.5f)
        _speed.value = clamped
        val player = mediaPlayer ?: return
        runCatching {
            val params = PlaybackParams()
            params.speed = clamped
            params.pitch = 1.0f // Preserve pitch
            player.playbackParams = params
        }
    }

    fun setLoopA() {
        _loopA.value = _positionMs.value
        if (_loopB.value <= _loopA.value) {
            _loopB.value = _durationMs.value
        }
    }

    fun setLoopB() {
        if (_positionMs.value > _loopA.value) {
            _loopB.value = _positionMs.value
        }
    }

    fun toggleLoopEnabled() {
        _loopEnabled.value = !_loopEnabled.value
    }

    fun clearLoop() {
        _loopA.value = 0L
        _loopB.value = _durationMs.value
        _loopEnabled.value = false
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val player = mediaPlayer
                if (player != null && runCatching { player.isPlaying }.getOrDefault(false)) {
                    val pos = player.currentPosition.toLong()
                    _positionMs.value = pos

                    // Check A-B Looper
                    if (_loopEnabled.value && _loopB.value > _loopA.value) {
                        if (pos >= _loopB.value) {
                            player.seekTo(_loopA.value.toInt())
                            _positionMs.value = _loopA.value
                        }
                    }
                }
                delay(100)
            }
        }
    }

    fun releasePlayer() {
        progressJob?.cancel()
        runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }
}
