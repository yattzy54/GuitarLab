package com.mmt.guitarlab.audio.drums

import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.domain.model.DrumSound
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.tanh

object DrumSoundSynthesizer {
    const val SAMPLE_RATE = 44100

    private val cachedSamples = mutableMapOf<Pair<DrumSound, DrumKit>, ShortArray>()

    init {
        DrumKit.entries.forEach { kit ->
            DrumSound.entries.forEach { sound ->
                cachedSamples[sound to kit] = generateSound(sound, kit)
            }
        }
    }

    fun getSample(sound: DrumSound, kit: DrumKit = DrumKit.ROCK): ShortArray {
        return cachedSamples[sound to kit] ?: generateSound(sound, kit)
    }

    private fun generateSound(sound: DrumSound, kit: DrumKit): ShortArray {
        return when (sound) {
            DrumSound.KICK -> generateKick(kit)
            DrumSound.SNARE -> generateSnare(kit)
            DrumSound.HIHAT_CLOSED -> generateHiHatClosed(kit)
            DrumSound.HIHAT_OPEN -> generateHiHatOpen(kit)
            DrumSound.TOM_LOW -> generateTomLow(kit)
            DrumSound.TOM_HIGH -> generateTomHigh(kit)
            DrumSound.CRASH -> generateCrash(kit)
            DrumSound.RIDE -> generateRide(kit)
        }
    }

    // --- KICK SYNTHESIS ---
    private fun generateKick(kit: DrumKit): ShortArray {
        val durationMs = when (kit) {
            DrumKit.METAL -> 180
            DrumKit.ROCK -> 280
            DrumKit.POP -> 300
            DrumKit.ELECTRONIC -> 480 // 808 long sub decay
        }
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)

        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleVal: Double = when (kit) {
                DrumKit.ROCK -> {
                    // Acoustic maple punch: beater click + organic pitch drop 155 -> 48Hz
                    val env = exp(-t * 15.0)
                    val pitch = 48.0 + 120.0 * exp(-t * 40.0)
                    val body = sin(2.0 * PI * pitch * t)
                    val click = if (t < 0.008) (Math.random() - 0.5) * exp(-t * 300.0) * 0.9 else 0.0
                    tanh((body * 0.9 + click) * env * 1.2)
                }
                DrumKit.METAL -> {
                    // Trigger double-bass: ultra-fast transient click at 3.8kHz + tight sub
                    val env = exp(-t * 22.0)
                    val pitch = 56.0 + 160.0 * exp(-t * 60.0)
                    val body = sin(2.0 * PI * pitch * t)
                    val click = if (t < 0.015) sin(2.0 * PI * 3800.0 * t) * exp(-t * 220.0) * 1.1 else 0.0
                    tanh((body * 0.85 + click) * env * 1.3)
                }
                DrumKit.POP -> {
                    // Radio punch: deep round 46Hz sub with smooth rounded attack
                    val env = exp(-t * 13.0)
                    val pitch = 46.0 + 95.0 * exp(-t * 32.0)
                    val body = sin(2.0 * PI * pitch * t) + sin(2.0 * PI * pitch * 2.0 * t) * 0.12
                    val click = if (t < 0.006) (Math.random() - 0.5) * 0.4 else 0.0
                    (body + click) * env * 0.95
                }
                DrumKit.ELECTRONIC -> {
                    // Iconic TR-808 sub boom: pure sine drop 110 -> 38Hz with extended sustain
                    val env = exp(-t * 6.5)
                    val pitch = 38.0 + 72.0 * exp(-t * 18.0)
                    val sine = sin(2.0 * PI * pitch * t)
                    val drive = tanh(sine * 1.4) * 0.95
                    val click = if (t < 0.004) 0.5 else 0.0
                    (drive + click) * env
                }
            }

            out[i] = (sampleVal.coerceIn(-1.0, 1.0) * 0.92 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- SNARE SYNTHESIS ---
    private fun generateSnare(kit: DrumKit): ShortArray {
        val durationMs = when (kit) {
            DrumKit.METAL -> 190
            DrumKit.ROCK -> 240
            DrumKit.POP -> 280
            DrumKit.ELECTRONIC -> 220
        }
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)

        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleVal: Double = when (kit) {
                DrumKit.ROCK -> {
                    // Organic 14" maple snare: dual body tone + sizzle
                    val toneEnv = exp(-t * 26.0)
                    val noiseEnv = exp(-t * 16.0)
                    val tone = (sin(2.0 * PI * 185.0 * t) * 0.45 + sin(2.0 * PI * 290.0 * t) * 0.25) * toneEnv
                    val noise = (Math.random() - 0.5) * 0.75 * noiseEnv
                    val snap = if (t < 0.005) 0.6 * exp(-t * 400.0) else 0.0
                    (tone + noise + snap)
                }
                DrumKit.METAL -> {
                    // Aggressive steel crack: 310Hz ring + piercing transient
                    val toneEnv = exp(-t * 32.0)
                    val noiseEnv = exp(-t * 20.0)
                    val tone = sin(2.0 * PI * 310.0 * t) * 0.5 * toneEnv
                    val noise = (Math.random() - 0.5) * 0.85 * noiseEnv
                    val click = if (t < 0.008) sin(2.0 * PI * 2600.0 * t) * 0.8 else 0.0
                    tanh((tone + noise + click) * 1.2)
                }
                DrumKit.POP -> {
                    // Modern layered snare + handclap snap
                    val toneEnv = exp(-t * 22.0)
                    val clapEnv = exp(-t * 14.0)
                    val tone = (sin(2.0 * PI * 200.0 * t) * 0.4 + sin(2.0 * PI * 130.0 * t) * 0.3) * toneEnv
                    val clap = (Math.random() - 0.5) * 0.75 * clapEnv
                    val handclap = if (t in 0.010..0.025 || t in 0.028..0.045) (Math.random() - 0.5) * 0.5 else 0.0
                    (tone + clap + handclap)
                }
                DrumKit.ELECTRONIC -> {
                    // TR-909 style: dual triangle/sine oscillators (175Hz + 335Hz) + snappy gated noise
                    val toneEnv = exp(-t * 35.0)
                    val noiseEnv = exp(-t * 20.0)
                    val tone = (sin(2.0 * PI * 175.0 * t) * 0.5 + sin(2.0 * PI * 335.0 * t) * 0.35) * toneEnv
                    val noise = (Math.random() - 0.5) * 0.7 * noiseEnv
                    val click = if (t < 0.004) 0.6 else 0.0
                    (tone + noise + click)
                }
            }

            out[i] = (sampleVal.coerceIn(-1.0, 1.0) * 0.90 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- HI-HAT CLOSED ---
    private fun generateHiHatClosed(kit: DrumKit): ShortArray {
        val durationMs = when (kit) {
            DrumKit.METAL -> 30
            DrumKit.ELECTRONIC -> 45
            DrumKit.ROCK -> 40
            DrumKit.POP -> 50
        }
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)

        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleVal: Double = when (kit) {
                DrumKit.ROCK -> {
                    val env = exp(-t * 85.0)
                    val noise = (Math.random() - 0.5) * 0.75
                    val metallic = (sin(2.0 * PI * 6800.0 * t) * 0.2 + sin(2.0 * PI * 9200.0 * t) * 0.15)
                    (noise + metallic) * env
                }
                DrumKit.METAL -> {
                    val env = exp(-t * 120.0)
                    val noise = (Math.random() - 0.5) * 0.8
                    val metallic = sin(2.0 * PI * 8200.0 * t) * 0.3
                    (noise + metallic) * env
                }
                DrumKit.POP -> {
                    val env = exp(-t * 70.0)
                    val noise = (Math.random() - 0.5) * 0.65
                    val metallic = (sin(2.0 * PI * 7500.0 * t) * 0.2 + sin(2.0 * PI * 10500.0 * t) * 0.15)
                    (noise + metallic) * env
                }
                DrumKit.ELECTRONIC -> {
                    // TR-808 6-oscillator metallic cluster
                    val env = exp(-t * 75.0)
                    val o1 = sin(2.0 * PI * 263.0 * t)
                    val o2 = sin(2.0 * PI * 400.0 * t)
                    val o3 = sin(2.0 * PI * 421.0 * t)
                    val o4 = sin(2.0 * PI * 474.0 * t)
                    val o5 = sin(2.0 * PI * 587.0 * t)
                    val o6 = sin(2.0 * PI * 845.0 * t)
                    val cluster = if ((o1 + o2 + o3 + o4 + o5 + o6) > 0) 0.6 else -0.6
                    val noise = (Math.random() - 0.5) * 0.3
                    (cluster + noise) * env
                }
            }

            out[i] = (sampleVal.coerceIn(-1.0, 1.0) * 0.85 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- HI-HAT OPEN ---
    private fun generateHiHatOpen(kit: DrumKit): ShortArray {
        val durationMs = when (kit) {
            DrumKit.METAL -> 180
            DrumKit.ROCK -> 260
            DrumKit.POP -> 280
            DrumKit.ELECTRONIC -> 320
        }
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)

        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * (1000.0 / durationMs * 2.8))
            val noise = (Math.random() - 0.5) * 0.7
            val metal = (sin(2.0 * PI * 6200.0 * t) * 0.2 + sin(2.0 * PI * 8400.0 * t) * 0.18 + sin(2.0 * PI * 10200.0 * t) * 0.12)
            val sampleVal = (noise + metal) * env
            out[i] = (sampleVal.coerceIn(-1.0, 1.0) * 0.82 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- TOMS ---
    private fun generateTomLow(kit: DrumKit): ShortArray = generateTom(85.0, 48.0, 240, kit)
    private fun generateTomHigh(kit: DrumKit): ShortArray = generateTom(155.0, 95.0, 190, kit)

    private fun generateTom(startFreq: Double, endFreq: Double, durationMs: Int, kit: DrumKit): ShortArray {
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 11.0)
            val freq = endFreq + (startFreq - endFreq) * exp(-t * 22.0)
            val tone = when (kit) {
                DrumKit.ELECTRONIC -> sin(2.0 * PI * freq * t) // Synth tom
                else -> sin(2.0 * PI * freq * t) * 0.8 + sin(2.0 * PI * freq * 1.6 * t) * 0.2
            }
            val stick = if (t < 0.008) (Math.random() - 0.5) * 0.3 else 0.0
            val sample = ((tone + stick) * env * 0.88).coerceIn(-1.0, 1.0)
            out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- CRASH ---
    private fun generateCrash(kit: DrumKit): ShortArray {
        val durationMs = when (kit) {
            DrumKit.METAL -> 700
            DrumKit.ELECTRONIC -> 500
            else -> 650
        }
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 6.5)
            val noise = (Math.random() - 0.5) * 0.65
            val metal = (sin(2.0 * PI * 4200.0 * t) * 0.15 + sin(2.0 * PI * 5800.0 * t) * 0.15 + sin(2.0 * PI * 8100.0 * t) * 0.12)
            val sample = ((noise + metal) * env * 0.85).coerceIn(-1.0, 1.0)
            out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    // --- RIDE ---
    private fun generateRide(kit: DrumKit): ShortArray {
        val durationMs = 380
        val n = (SAMPLE_RATE * durationMs / 1000)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 9.5)
            val pingFreq = if (kit == DrumKit.METAL) 2800.0 else 2400.0
            val ping = (sin(2.0 * PI * pingFreq * t) * 0.45 + sin(2.0 * PI * (pingFreq * 1.6) * t) * 0.25)
            val shimmer = (Math.random() - 0.5) * 0.3
            val sample = ((ping + shimmer) * env * 0.82).coerceIn(-1.0, 1.0)
            out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }
}
