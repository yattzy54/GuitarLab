package com.mmt.guitarlab.domain.usecase

import javax.inject.Inject

class TapTempoUseCase @Inject constructor() {
    private val minBpm: Int = 30
    private val maxBpm: Int = 300
    private val tapTimestamps = mutableListOf<Long>()

    fun recordTap(currentTime: Long = System.currentTimeMillis()): Int? {
        if (tapTimestamps.isNotEmpty() && currentTime - tapTimestamps.last() > 2500) {
            tapTimestamps.clear()
        }
        tapTimestamps.add(currentTime)
        if (tapTimestamps.size > 5) {
            tapTimestamps.removeAt(0)
        }
        if (tapTimestamps.size >= 2) {
            val intervals = tapTimestamps.zipWithNext { a, b -> b - a }
            val avgInterval = intervals.average()
            if (avgInterval > 0) {
                return (60_000.0 / avgInterval).toInt().coerceIn(minBpm, maxBpm)
            }
        }
        return null
    }

    fun reset() {
        tapTimestamps.clear()
    }
}
