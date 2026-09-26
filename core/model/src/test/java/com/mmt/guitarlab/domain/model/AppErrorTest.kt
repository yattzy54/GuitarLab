package com.mmt.guitarlab.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppErrorTest {

    @Test
    fun `network error holds message`() {
        val error = AppError.NetworkError("Connection lost")
        assertEquals("Connection lost", error.message)
    }

    @Test
    fun `database error holds message`() {
        val error = AppError.DatabaseError("Query failed")
        assertEquals("Query failed", error.message)
    }

    @Test
    fun `parse error holds message`() {
        val error = AppError.ParseError("Invalid format")
        assertEquals("Invalid format", error.message)
    }

    @Test
    fun `audio error holds message`() {
        val error = AppError.AudioError("Track initialization failed")
        assertEquals("Track initialization failed", error.message)
    }

    @Test
    fun `unknown error holds throwable`() {
        val exception = RuntimeException("Boom")
        val error = AppError.UnknownError(exception)
        assertEquals(exception, error.throwable)
    }
}
