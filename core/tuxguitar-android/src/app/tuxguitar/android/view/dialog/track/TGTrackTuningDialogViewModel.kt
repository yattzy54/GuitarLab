package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.EditorStateViewModel
import app.tuxguitar.song.models.TGTrack

data class TGTrackTuningState(
    val tuning: List<TGTrackTuningModel>,
    val presets: List<TGTrackTuningPresetModel>,
    val selectedOffset: Int,
    val offsetEnabled: Boolean,
    val selectedPreset: TGTrackTuningPresetModel? = null,
)

class TGTrackTuningDialogViewModel(initial: TGTrackTuningState) :
    EditorStateViewModel<TGTrackTuningState>(initial) {
    init {
        setTuning(initial.tuning)
    }

    val isValidTuning: Boolean
        get() = state.value.tuning.size in TGTrack.MIN_STRINGS..TGTrack.MAX_STRINGS

    fun selectOffset(offset: Int) {
        require(offset in TGTrack.MIN_OFFSET..TGTrack.MAX_OFFSET) { "Track offset is out of range" }
        update { it.copy(selectedOffset = offset) }
    }

    fun setOffsetEnabled(enabled: Boolean) {
        update { it.copy(offsetEnabled = enabled) }
    }

    fun selectPreset(preset: TGTrackTuningPresetModel?) {
        if (preset == null) {
            update { it.copy(selectedPreset = null) }
        } else {
            setTuning(preset.values.orEmpty().map { model -> TGTrackTuningModel().apply { value = model.value } })
        }
    }

    fun modify(model: TGTrackTuningModel, value: Int?) {
        update {
            withTuning(it, it.tuning.map { current ->
                if (current === model) TGTrackTuningModel().apply { this.value = value } else current
            })
        }
    }

    fun add(model: TGTrackTuningModel) {
        update { withTuning(it, it.tuning + model) }
    }

    fun remove(model: TGTrackTuningModel) {
        update { withTuning(it, it.tuning - model) }
    }

    fun setTuning(tuning: List<TGTrackTuningModel>) {
        update { withTuning(it, tuning.toList()) }
    }

    private fun withTuning(current: TGTrackTuningState, tuning: List<TGTrackTuningModel>): TGTrackTuningState =
        current.copy(
            tuning = tuning,
            selectedPreset = current.presets.lastOrNull { preset ->
                val values = preset.values
                values != null && tuning.size == values.size &&
                    tuning.indices.all { tuning[it].value == values[it].value }
            },
        )
}
