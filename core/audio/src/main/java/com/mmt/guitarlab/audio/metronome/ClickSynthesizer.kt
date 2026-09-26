package com.mmt.guitarlab.audio.metronome

import com.mmt.guitarlab.domain.model.MetronomeSound
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object ClickSynthesizer {

    fun generateClick(sound: MetronomeSound, sampleRate: Int, accent: Boolean): ShortArray {
        return when (sound) {
            MetronomeSound.WOODBLOCK -> woodblock(sampleRate, accent)
            MetronomeSound.DIGITAL_BEEP -> digitalBeep(sampleRate, accent)
            MetronomeSound.MECHANICAL_CLICK -> mechanicalClick(sampleRate, accent)
            MetronomeSound.COWBELL -> cowbell(sampleRate, accent)
        }
    }

    fun woodblock(sampleRate: Int, accent: Boolean): ShortArray {
        val durationMs = if (accent) 18 else 12
        val n = (sampleRate * durationMs / 1000).coerceAtLeast(32)
        val freq = if (accent) 1760.0 else 1320.0
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * if (accent) 55.0 else 70.0)
            val click = sin(2.0 * PI * freq * t) * env
            val noise = (Math.random() - 0.5) * 0.08 * env
            val sample = ((click + noise) * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    fun digitalBeep(sampleRate: Int, accent: Boolean): ShortArray {
        val durationMs = if (accent) 24 else 16
        val n = (sampleRate * durationMs / 1000).coerceAtLeast(32)
        val freq = if (accent) 2200.0 else 1100.0
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * if (accent) 40.0 else 60.0)
            val wave = sin(2.0 * PI * freq * t) * env
            val sample = (wave * 0.85 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    fun mechanicalClick(sampleRate: Int, accent: Boolean): ShortArray {
        val durationMs = if (accent) 14 else 10
        val n = (sampleRate * durationMs / 1000).coerceAtLeast(32)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 90.0)
            val lowFreq = if (accent) 800.0 else 550.0
            val body = sin(2.0 * PI * lowFreq * t) * 0.4
            val transientNoise = (Math.random() - 0.5) * 0.8
            val sample = ((body + transientNoise) * env * 0.95 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }

    fun cowbell(sampleRate: Int, accent: Boolean): ShortArray {
        val durationMs = if (accent) 38 else 26
        val n = (sampleRate * durationMs / 1000).coerceAtLeast(32)
        val f1 = if (accent) 840.0 else 620.0
        val f2 = if (accent) 1260.0 else 930.0
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * if (accent) 30.0 else 45.0)
            val tone = (sin(2.0 * PI * f1 * t) * 0.6 + sin(2.0 * PI * f2 * t) * 0.4) * env
            val sample = (tone * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }
}
