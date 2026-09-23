package com.mmt.guitarlab.audio.tuner

/**
 * YIN pitch detector (de Cheveigné & Kawahara).
 * Operates on a float buffer in [-1, 1] and returns frequency in Hz, or 0 if unvoiced.
 */
class YinPitchDetector(
    private val sampleRate: Int,
    bufferSize: Int,
    private val threshold: Float = 0.12f,
) {
    private val half = bufferSize / 2
    private val yin = FloatArray(half)

    fun detect(samples: FloatArray): Pair<Float, Float> {
        val tauMax = half
        difference(samples, tauMax)
        cumulativeMeanNormalizedDifference(tauMax)
        val tau = absoluteThreshold(tauMax)
        if (tau < 0) return 0f to 0f
        val betterTau = parabolicInterpolation(tau)
        if (betterTau <= 0f) return 0f to 0f
        val clarity = (1f - yin[tau]).coerceIn(0f, 1f)
        return (sampleRate / betterTau) to clarity
    }

    private fun difference(samples: FloatArray, tauMax: Int) {
        yin[0] = 1f
        for (tau in 1 until tauMax) {
            var sum = 0f
            var i = 0
            val limit = samples.size - tau
            while (i < limit) {
                val delta = samples[i] - samples[i + tau]
                sum += delta * delta
                i++
            }
            yin[tau] = sum
        }
    }

    private fun cumulativeMeanNormalizedDifference(tauMax: Int) {
        yin[0] = 1f
        var running = 0f
        for (tau in 1 until tauMax) {
            running += yin[tau]
            yin[tau] = if (running == 0f) 1f else yin[tau] * tau / running
        }
    }

    private fun absoluteThreshold(tauMax: Int): Int {
        var tau = 2
        while (tau < tauMax) {
            if (yin[tau] < threshold) {
                while (tau + 1 < tauMax && yin[tau + 1] < yin[tau]) {
                    tau++
                }
                return tau
            }
            tau++
        }
        var minTau = -1
        var minVal = Float.MAX_VALUE
        for (t in 2 until tauMax) {
            if (yin[t] < minVal) {
                minVal = yin[t]
                minTau = t
            }
        }
        return if (minVal < 0.35f) minTau else -1
    }

    private fun parabolicInterpolation(tau: Int): Float {
        if (tau <= 0 || tau >= yin.size - 1) return tau.toFloat()
        val s0 = yin[tau - 1]
        val s1 = yin[tau]
        val s2 = yin[tau + 1]
        val denom = 2f * s1 - s2 - s0
        if (denom == 0f) return tau.toFloat()
        return tau + (s2 - s0) / (2f * denom)
    }
}
