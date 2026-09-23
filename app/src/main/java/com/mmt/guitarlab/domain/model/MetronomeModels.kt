package com.mmt.guitarlab.domain.model

enum class TimeSignature(
    val beatsPerBar: Int,
    val beatUnit: Int,
    val accentBeats: Set<Int>,
    val label: String,
) {
    TWO_FOUR(2, 4, setOf(1), "2/4"),
    THREE_FOUR(3, 4, setOf(1), "3/4"),
    FOUR_FOUR(4, 4, setOf(1), "4/4"),
    FIVE_FOUR(5, 4, setOf(1, 4), "5/4"),
    SIX_EIGHT(6, 8, setOf(1, 4), "6/8"),
    SEVEN_EIGHT(7, 8, setOf(1, 4), "7/8");

    companion object {
        val default = FOUR_FOUR
    }
}

enum class TrainerIntervalKind {
    BARS,
    MINUTES,
}

data class TrainerConfig(
    val enabled: Boolean = false,
    val startBpm: Int = 80,
    val targetBpm: Int = 140,
    val incrementBpm: Int = 2,
    val intervalKind: TrainerIntervalKind = TrainerIntervalKind.BARS,
    val intervalValue: Int = 4,
)

data class MetronomeConfig(
    val bpm: Int = 100,
    val timeSignature: TimeSignature = TimeSignature.FOUR_FOUR,
    val volume: Float = 0.85f,
    val trainer: TrainerConfig = TrainerConfig(),
) {
    companion object {
        const val MIN_BPM = 30
        const val MAX_BPM = 300
    }
}

data class MetronomeBeat(
    val beatInBar: Int,
    val barIndex: Long,
    val accent: Boolean,
    val bpm: Int,
    val progressToNextJump: Float,
    val beatsUntilJump: Int?,
    val millisUntilJump: Long?,
)
