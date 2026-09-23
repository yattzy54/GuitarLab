package com.mmt.guitarlab.audio.metronome

object ClickSynthesizer {
    fun woodblock(sampleRate: Int, accent: Boolean): ShortArray {
        val durationMs = if (accent) 18 else 12
        val n = (sampleRate * durationMs / 1000).coerceAtLeast(32)
        val freq = if (accent) 1760.0 else 1320.0
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / sampleRate
            val env = kotlin.math.exp(-t * if (accent) 55.0 else 70.0)
            val click = kotlin.math.sin(2.0 * Math.PI * freq * t) * env
            val noise = (Math.random() - 0.5) * 0.08 * env
            val sample = ((click + noise) * 0.9 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            out[i] = sample.toShort()
        }
        return out
    }
}
