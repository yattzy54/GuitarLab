package com.mmt.guitarlab.audio

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mmt.guitarlab.domain.audio.MetronomeEngine
import com.mmt.guitarlab.domain.audio.DrumEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioLifecycleObserver @Inject constructor(
    private val metronomeEngine: MetronomeEngine,
    private val drumEngine: DrumEngine,
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        metronomeEngine.stop()
        drumEngine.stop()
    }
}
