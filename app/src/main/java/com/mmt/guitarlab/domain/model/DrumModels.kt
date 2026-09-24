package com.mmt.guitarlab.domain.model

enum class DrumKit(
    val id: String,
    val displayName: String,
    val description: String,
    val iconEmoji: String,
    val styleCategory: String,
) {
    ROCK("rock", "Acoustic Rock", "Acoustic punchy kick & crisp maple snare", "🥁", "Rock / Blues"),
    METAL("metal", "Heavy Metal", "Clicky trigger kick & aggressive steel crack", "⚡", "Metal / Punk"),
    POP("pop", "Modern Pop", "Modern radio punch & layered clap-snare", "✨", "Pop / Funk"),
    ELECTRONIC("electronic", "Electronic 808/909", "Deep sub 808 boom & analog 909 percussion", "🎹", "EDM / Synth"),
    JAZZ("jazz", "Vintage Jazz", "Soft acoustic kick, warm brush snare & ride cymbal", "🎷", "Jazz / Swing"),
    PERCUSSION("percussion", "Acoustic Cajon", "Wood cajon, congas, shaker & organic rimshot", "🪘", "Latin / Acoustic"),
}

enum class DrumSound(val displayName: String, val shortName: String) {
    KICK("Bass Drum", "KICK"),
    SNARE("Snare Drum", "SNARE"),
    HIHAT_CLOSED("Closed Hat", "CH"),
    HIHAT_OPEN("Open Hat", "OH"),
    TOM_LOW("Low Tom", "LT"),
    TOM_HIGH("High Tom", "HT"),
    CRASH("Crash", "CR"),
    RIDE("Ride", "RD"),
}

data class DrumPattern(
    val id: String,
    val name: String,
    val style: String,
    val defaultBpm: Int,
    val grid: Map<DrumSound, BooleanArray>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DrumPattern
        if (id != other.id) return false
        if (name != other.name) return false
        if (style != other.style) return false
        if (defaultBpm != other.defaultBpm) return false
        if (grid.keys != other.grid.keys) return false
        for ((k, v) in grid) {
            val otherV = other.grid[k] ?: return false
            if (!v.contentEquals(otherV)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + style.hashCode()
        result = 31 * result + defaultBpm
        return result
    }

    companion object {
        fun createGrid(vararg pairs: Pair<DrumSound, List<Int>>): Map<DrumSound, BooleanArray> {
            val map = mutableMapOf<DrumSound, BooleanArray>()
            DrumSound.entries.forEach { sound ->
                val arr = BooleanArray(16) { false }
                pairs.find { it.first == sound }?.second?.forEach { step ->
                    if (step in 0..15) arr[step] = true
                }
                map[sound] = arr
            }
            return map
        }

        val DEFAULT_PATTERNS = listOf(
            DrumPattern(
                id = "rock_classic",
                name = "Rock Classic 4/4",
                style = "Rock",
                defaultBpm = 110,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 8, 10),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.CRASH to listOf(0),
                )
            ),
            DrumPattern(
                id = "hard_rock",
                name = "Hard Rock Drive",
                style = "Rock",
                defaultBpm = 124,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 3, 8, 10, 14),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.HIHAT_OPEN to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.CRASH to listOf(0, 8),
                )
            ),
            DrumPattern(
                id = "metal_blast",
                name = "Metal Double Bass",
                style = "Metal",
                defaultBpm = 160,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.RIDE to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.CRASH to listOf(0, 8),
                )
            ),
            DrumPattern(
                id = "blues_shuffle",
                name = "Blues Shuffle",
                style = "Blues",
                defaultBpm = 95,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 6, 8),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.RIDE to listOf(0, 2, 3, 6, 8, 10, 11, 14),
                    DrumSound.HIHAT_CLOSED to listOf(4, 12),
                )
            ),
            DrumPattern(
                id = "funk_groove",
                name = "Funk Syncopation",
                style = "Funk",
                defaultBpm = 100,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 7, 10, 13),
                    DrumSound.SNARE to listOf(4, 9, 12),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.HIHAT_OPEN to listOf(15),
                )
            ),
            DrumPattern(
                id = "jazz_swing",
                name = "Jazz Ride Swing",
                style = "Jazz",
                defaultBpm = 130,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 8),
                    DrumSound.SNARE to listOf(12),
                    DrumSound.RIDE to listOf(0, 4, 6, 8, 12, 14),
                    DrumSound.HIHAT_CLOSED to listOf(4, 12),
                )
            ),
            DrumPattern(
                id = "reggae_drop",
                name = "Reggae One Drop",
                style = "Reggae",
                defaultBpm = 75,
                grid = createGrid(
                    DrumSound.KICK to listOf(8),
                    DrumSound.SNARE to listOf(8),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.HIHAT_OPEN to listOf(6, 14),
                )
            ),
            DrumPattern(
                id = "country_train",
                name = "Country Train Beat",
                style = "Country",
                defaultBpm = 115,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 8),
                    DrumSound.SNARE to listOf(2, 4, 6, 10, 12, 14),
                    DrumSound.HIHAT_CLOSED to listOf(0, 4, 8, 12),
                )
            ),
            DrumPattern(
                id = "bossa_latin",
                name = "Bossa Nova Latin",
                style = "Latin",
                defaultBpm = 105,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 6, 8, 14),
                    DrumSound.SNARE to listOf(3, 6, 10, 12),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                )
            ),
            DrumPattern(
                id = "punk_rush",
                name = "Punk Rock 8ths",
                style = "Punk",
                defaultBpm = 175,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 3, 8, 11),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.HIHAT_OPEN to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.CRASH to listOf(0),
                )
            ),
            DrumPattern(
                id = "pop_dance",
                name = "Four-on-the-Floor",
                style = "Pop",
                defaultBpm = 124,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 4, 8, 12),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.HIHAT_OPEN to listOf(2, 6, 10, 14),
                )
            ),
            DrumPattern(
                id = "hiphop_boombap",
                name = "Boom Bap Hip Hop",
                style = "Hip Hop",
                defaultBpm = 90,
                grid = createGrid(
                    DrumSound.KICK to listOf(0, 10, 11),
                    DrumSound.SNARE to listOf(4, 12),
                    DrumSound.HIHAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                    DrumSound.HIHAT_OPEN to listOf(7, 15),
                )
            )
        )
    }
}
