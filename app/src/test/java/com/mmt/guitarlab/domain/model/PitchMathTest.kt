package com.mmt.guitarlab.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PitchMathTest {
    @Test
    fun a4_maps_to_a4() {
        val pitch = PitchMath.fromFrequency(440f, 440f, 1f)!!
        assertEquals("A", pitch.noteName)
        assertEquals(4, pitch.octave)
        assertTrue(kotlin.math.abs(pitch.cents) < 1f)
    }

    @Test
    fun e2_standard_guitar() {
        val pitch = PitchMath.fromFrequency(82.41f, 440f, 1f)!!
        assertEquals("E", pitch.noteName)
        assertEquals(2, pitch.octave)
    }
}
