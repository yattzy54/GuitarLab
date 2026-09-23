package com.mmt.guitarlab.data.repository

import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import kotlin.math.pow

object DefaultTunings {

    fun midiToHz(midi: Int, a4: Float = 440f): Float {
        return (a4 * 2.0.pow((midi - 69) / 12.0)).toFloat()
    }

    private fun createTuning(
        id: String,
        name: String,
        category: String,
        midiNotes: List<Triple<String, Int, Int>>, // NoteName, Octave, Midi
        a4: Float = 440f,
    ): Tuning {
        val notes = midiNotes.mapIndexed { index, triple ->
            TuningNote(
                stringNumber = index + 1,
                noteName = triple.first,
                octave = triple.second,
                targetFrequencyHz = midiToHz(triple.third, a4),
                midiNote = triple.third,
            )
        }
        return Tuning(
            id = id,
            name = name,
            category = category,
            stringCount = notes.size,
            notes = notes,
        )
    }

    fun getAll(a4: Float = 440f): List<Tuning> {
        return listOf(
            createTuning(
                "standard_e",
                "Standard E",
                "Standard",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                ),
                a4,
            ),
            createTuning(
                "half_step_down",
                "Half Step Down",
                "Standard",
                listOf(
                    Triple("D♯", 4, 63),
                    Triple("A♯", 3, 58),
                    Triple("F♯", 3, 54),
                    Triple("C♯", 3, 49),
                    Triple("G♯", 2, 44),
                    Triple("D♯", 2, 39),
                ),
                a4,
            ),
            createTuning(
                "drop_d",
                "Drop D",
                "Drop",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("D", 2, 38),
                ),
                a4,
            ),
            createTuning(
                "drop_c",
                "Drop C",
                "Drop",
                listOf(
                    Triple("D", 4, 62),
                    Triple("A", 3, 57),
                    Triple("F", 3, 53),
                    Triple("C", 3, 48),
                    Triple("G", 2, 43),
                    Triple("C", 2, 36),
                ),
                a4,
            ),
            createTuning(
                "drop_b",
                "Drop B",
                "Drop",
                listOf(
                    Triple("C♯", 4, 61),
                    Triple("G♯", 3, 56),
                    Triple("E", 3, 52),
                    Triple("B", 2, 47),
                    Triple("F♯", 2, 42),
                    Triple("B", 1, 35),
                ),
                a4,
            ),
            createTuning(
                "drop_a",
                "Drop A",
                "Drop",
                listOf(
                    Triple("B", 3, 59),
                    Triple("F♯", 3, 54),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                    Triple("A", 1, 33),
                ),
                a4,
            ),
            createTuning(
                "open_d",
                "Open D",
                "Open",
                listOf(
                    Triple("D", 4, 62),
                    Triple("A", 3, 57),
                    Triple("F♯", 3, 54),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("D", 2, 38),
                ),
                a4,
            ),
            createTuning(
                "open_g",
                "Open G",
                "Open",
                listOf(
                    Triple("D", 4, 62),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("G", 2, 43),
                    Triple("D", 2, 38),
                ),
                a4,
            ),
            createTuning(
                "dadgad",
                "DADGAD",
                "Alternate",
                listOf(
                    Triple("D", 4, 62),
                    Triple("A", 3, 57),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("D", 2, 38),
                ),
                a4,
            ),
            createTuning(
                "std_7string",
                "7-String Standard",
                "7-String",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                    Triple("B", 1, 35),
                ),
                a4,
            ),
            createTuning(
                "drop_a_7string",
                "7-String Drop A",
                "7-String",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                    Triple("A", 1, 33),
                ),
                a4,
            ),
            createTuning(
                "std_8string",
                "8-String Standard",
                "8-String",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                    Triple("B", 1, 35),
                    Triple("F♯", 1, 30),
                ),
                a4,
            ),
            createTuning(
                "drop_e_8string",
                "8-String Drop E",
                "8-String",
                listOf(
                    Triple("E", 4, 64),
                    Triple("B", 3, 59),
                    Triple("G", 3, 55),
                    Triple("D", 3, 50),
                    Triple("A", 2, 45),
                    Triple("E", 2, 40),
                    Triple("B", 1, 35),
                    Triple("E", 1, 28),
                ),
                a4,
            ),
        )
    }
}
