package com.mmt.guitarlab.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TapTempoUseCaseTest {

    @Test
    fun `first tap returns null`() {
        val useCase = TapTempoUseCase()
        val result = useCase.recordTap(1000L)
        assertNull(result)
    }

    @Test
    fun `two taps calculate correct bpm`() {
        val useCase = TapTempoUseCase()
        // Interval of 500ms -> 120 BPM
        useCase.recordTap(1000L)
        val bpm = useCase.recordTap(1500L)
        assertEquals(120, bpm)
    }

    @Test
    fun `reset clears tap timestamps`() {
        val useCase = TapTempoUseCase()
        useCase.recordTap(1000L)
        useCase.reset()
        val result = useCase.recordTap(1500L)
        assertNull(result)
    }
}
