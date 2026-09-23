package com.mmt.guitarlab.audio.drums

import com.mmt.guitarlab.domain.model.DrumSound
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object DrumSoundSynthesizer {
    const val SAMPLE_RATE = 44100

    private val cachedSamples = mutableMapOf<DrumSound, ShortArray>()

    init {
        DrumSound.entries.forEach { sound ->
            cachedSamples[sound] = generateSound(sound)
        }
    }

    fun getSample(sound: DrumSound): ShortArray {
        return cachedSamples[sound] ?: generateSound(sound)
    }

    private fun generateSound(sound: DrumSound): ShortArray {
        return when (sound) {
            DrumSound.KICK -> generateKick()
            DrumSound.SNARE -> generateSnare()
            DrumSound.HIHAT_CLOSED -> generateHiHatClosed()
            DrumSound.HIHAT_OPEN -> generateHiHatOpen()
            DrumSound.TOM_LOW -> generateTom(90.0, 50.0, 220)
            DrumSound.TOM_HIGH -> generateTom(160.0, 100.0, 180)
            DrumSound.CRASH -> generateCrash()
            DrumSound.RIDE -> generateRide()
        }
    }

    private fun generateKick(): ShortArray {
        val durationMs = 280
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 14.0)
            val pitch = 45.0 + 110.0 * exp(-t * 35.0)
            val sine = sin(2.0 * PI * pitch * t)
            val click = if (t < 0.005) (Math.random() - 0.5) * 0.4 else 0.0
            val sample = ((sine + click) * env * 0.95 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateSnare(): ShortArray {
        val durationMs = 220
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val toneEnv = exp(-t * 28.0)
            val noiseEnv = exp(-t * 18.0)
            val tone = sin(2.0 * PI * 185.0 * t) * 0.35 * toneEnv
            val noise = (Math.random() - 0.5) * 0.65 * noiseEnv
            val snap = if (t < 0.004) 0.5 else 0.0
            val sample = ((tone + noise + snap) * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateHiHatClosed(): ShortArray {
        val durationMs = 35
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 90.0)
            val noise = (Math.random() - 0.5) * 0.8
            val metallic = sin(2.0 * PI * 6800.0 * t) * 0.2
            val sample = ((noise + metallic) * env * 0.8 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateHiHatOpen(): ShortArray {
        val durationMs = 180
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 18.0)
            val noise = (Math.random() - 0.5) * 0.75
            val metallic = sin(2.0 * PI * 6500.0 * t) * 0.25
            val sample = ((noise + metallic) * env * 0.75 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateTom(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 12.0)
            val freq = endFreq + (startFreq - endFreq) * exp(-t * 20.0)
            val tone = sin(2.0 * PI * freq * t)
            val sample = (tone * env * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateCrash(): ShortArray {
        val durationMs = 450
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 7.5)
            val noise = (Math.random() - 0.5) * 0.7
            val metal1 = sin(2.0 * PI * 4400.0 * t) * 0.15
            val metal2 = sin(2.0 * PI * 7200.0 * t) * 0.15
            val sample = ((noise + metal1 + metal2) * env * 0.85 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    private fun generateRide(): ShortArray {
        val durationMs = 280
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 11.0)
            val ping = (sin(2.0 * PI * 2200.0 * t) * 0.4 + sin(2.0 * PI * 3400.0 * t) * 0.3)
            val shimmer = (Math.random() - 0.5) * 0.3
            val sample = ((ping + shimmer) * env * 0.8 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }
}
