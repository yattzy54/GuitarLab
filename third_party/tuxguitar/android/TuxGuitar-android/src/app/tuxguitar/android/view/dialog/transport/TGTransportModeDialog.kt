package app.tuxguitar.android.view.dialog.transport

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.caret.TGMoveToAction
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerMode
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGTrack

class TGTransportModeDialog : TGModalFragment(R.layout.view_transport_mode) {
    protected lateinit var simple: RadioButton
    protected lateinit var custom: RadioButton
    protected lateinit var simpleLabel: TextView
    protected lateinit var simplePercentLabel: TextView
    protected lateinit var simplePercent: Spinner
    protected lateinit var simpleLoop: CheckBox
    protected lateinit var customLabel: TextView
    protected lateinit var customFromLabel: TextView
    protected lateinit var customFrom: Spinner
    protected lateinit var customToLabel: TextView
    protected lateinit var customTo: Spinner
    protected lateinit var customIncrementLabel: TextView
    protected lateinit var customIncrement: Spinner
    protected lateinit var loopLabel: TextView
    protected lateinit var loopFromLabel: TextView
    protected lateinit var loopFrom: Spinner
    protected lateinit var loopToLabel: TextView
    protected lateinit var loopTo: Spinner
    protected var ok = false

    private val bgcolorErr = Color.rgb(230, 110, 110)
    private val bgcolorOK = Color.TRANSPARENT
    private val colorErr = Color.GRAY
    private val colorOK = Color.TRANSPARENT

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.transport_mode_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            val type = if (custom.isChecked) MidiPlayerMode.TYPE_CUSTOM else MidiPlayerMode.TYPE_SIMPLE
            val loop = type == MidiPlayerMode.TYPE_CUSTOM ||
                (type == MidiPlayerMode.TYPE_SIMPLE && simpleLoop.isChecked)
            val simplePcInt = selectedInt(simplePercent)
            val loopStart = if (loopFrom.selectedItemId == 0L) -1 else getMeasureNb(loopFrom)
            val loopEnd = if (loopTo.selectedItemId == 0L) -1 else getMeasureNb(loopTo)

            if (loop) {
                val track = MidiPlayer.getInstance(findContext()).song.getTrack(0)
                val beat: TGBeat = track.getMeasure(if (loopStart > 0) loopStart - 1 else 0).getBeat(0)
                TGActionProcessor(findContext(), TGMoveToAction.NAME).apply {
                    setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat)
                    process()
                }
            }

            MidiPlayer.getInstance(findContext()).mode.apply {
                setType(type)
                setLoop(loop)
                setSimplePercent(simplePcInt)
                setCustomPercentFrom(selectedInt(customFrom))
                setCustomPercentTo(selectedInt(customTo))
                setCustomPercentIncrement(selectedInt(customIncrement))
                setLoopSHeader(loopStart)
                setLoopEHeader(loopEnd)
            }
            close()
            true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_ok).apply {
            isEnabled = ok
            icon?.colorFilter = BlendModeColorFilter(
                if (ok) colorOK else colorErr,
                BlendMode.SRC_ATOP
            )
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val percentAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createPercentValues()
        )
        val loopFromAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createLoopFromValues()
        )
        val loopToAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createLoopToValuesFrom(1)
        )

        simple = requireView().findViewById(R.id.transport_mode_dlg_simple)
        custom = requireView().findViewById(R.id.transport_mode_dlg_trainer)
        simpleLabel = requireView().findViewById(R.id.transport_mode_dlg_simple_label)
        simplePercentLabel = requireView().findViewById(R.id.transport_mode_dlg_simple_tempo_percent_label)
        simplePercent = requireView().findViewById(R.id.transport_mode_dlg_simple_tempo_percent_value)
        simpleLoop = requireView().findViewById(R.id.transport_mode_dlg_simple_loop)
        customLabel = requireView().findViewById(R.id.transport_mode_dlg_trainer_label)
        customFromLabel = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_from_label)
        customFrom = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_from_value)
        customToLabel = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_to_label)
        customTo = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_to_value)
        customIncrementLabel = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_increment_label)
        customIncrement = requireView().findViewById(R.id.transport_mode_dlg_trainer_tempo_increment_value)
        loopLabel = requireView().findViewById(R.id.transport_mode_dlg_loop_range_label)
        loopFromLabel = requireView().findViewById(R.id.transport_mode_dlg_loop_range_from_label)
        loopFrom = requireView().findViewById(R.id.transport_mode_dlg_loop_range_from_value)
        loopToLabel = requireView().findViewById(R.id.transport_mode_dlg_loop_range_to_label)
        loopTo = requireView().findViewById(R.id.transport_mode_dlg_loop_range_to_value)

        val mode = MidiPlayer.getInstance(findContext()).mode
        val isTrainer = mode.type == MidiPlayerMode.TYPE_CUSTOM
        simple.isChecked = !isTrainer
        custom.isChecked = isTrainer
        enableTrainerMode(isTrainer)
        enableLoop(isTrainer || mode.isLoop())

        custom.setOnCheckedChangeListener { _, checked ->
            enableTrainerMode(checked)
            enableLoop(checked || simpleLoop.isChecked)
        }
        simpleLoop.setOnCheckedChangeListener { _, checked -> enableLoop(checked) }
        customFrom.setOnItemSelectedListener(trainerTempoListener())
        customTo.setOnItemSelectedListener(trainerTempoListener())
        customIncrement.setOnItemSelectedListener(trainerTempoListener())
        loopFrom.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val oldToItem = loopTo.selectedItem.toString()
                val oldToItemId = loopTo.selectedItemId
                loopToAdapter.clear()
                loopToAdapter.addAll(
                    createLoopToValuesFrom(
                        if (loopFrom.selectedItemId < 1L) 1 else getMeasureNb(loopFrom)
                    )
                )
                when {
                    oldToItemId == 0L -> loopTo.setSelection(0)
                    loopToAdapter.getPosition(oldToItem) > -1 ->
                        loopTo.setSelection(loopToAdapter.getPosition(oldToItem))
                    else -> loopTo.setSelection(loopToAdapter.getPosition(loopFrom.selectedItem.toString()))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        })

        percentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loopFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loopToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        simplePercent.adapter = percentAdapter
        simplePercent.setSelection(percentAdapter.getPosition(mode.simplePercent))
        simpleLoop.isChecked = mode.isLoop()
        customFrom.adapter = percentAdapter
        customFrom.setSelection(percentAdapter.getPosition(mode.customPercentFrom))
        customTo.adapter = percentAdapter
        customTo.setSelection(percentAdapter.getPosition(mode.customPercentTo))
        customIncrement.adapter = percentAdapter
        customIncrement.setSelection(percentAdapter.getPosition(mode.customPercentIncrement))
        loopFrom.adapter = loopFromAdapter
        loopFrom.setSelection(if (mode.loopSHeader > 0) mode.loopSHeader else 0)
        loopTo.adapter = loopToAdapter
        loopTo.setSelection(if (mode.loopEHeader > 0) mode.loopEHeader else 0)
    }

    private fun trainerTempoListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (parent != null) trainerTempoOnSelect(parent)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }

    fun trainerTempoOnSelect(parent: AdapterView<*>) {
        var valid = true
        if (parent == customFrom) {
            if (selectedInt(customFrom) >= selectedInt(customTo)) {
                valid = false
                customFrom.setBackgroundColor(bgcolorErr)
                customTo.setBackgroundColor(bgcolorOK)
            }
        } else if (parent == customTo) {
            if (selectedInt(customTo) <= selectedInt(customFrom)) {
                valid = false
                customTo.setBackgroundColor(bgcolorErr)
                customFrom.setBackgroundColor(bgcolorOK)
            }
        }
        if (valid) {
            customTo.setBackgroundColor(bgcolorOK)
            customFrom.setBackgroundColor(bgcolorOK)
            if (selectedInt(customIncrement) > selectedInt(customTo) - selectedInt(customFrom)) {
                valid = false
                customIncrement.setBackgroundColor(bgcolorErr)
            } else {
                customIncrement.setBackgroundColor(bgcolorOK)
            }
        }
        ok = valid
        requireActivity().invalidateOptionsMenu()
    }

    fun createPercentValues(): Array<Int> = (MIN_SELECTION..MAX_SELECTION).toList().toTypedArray()

    fun createLoopFromValues(): List<String> = buildList {
        add(getString(R.string.transport_mode_dlg_loop_range_from_default))
        for (measure in 1..MidiPlayer.getInstance(findContext()).song.countMeasureHeaders()) {
            add(getItemText(measure))
        }
    }

    fun createLoopToValuesFrom(firstMeasure: Int): List<String> = buildList {
        add(getString(R.string.transport_mode_dlg_loop_range_to_default))
        for (measure in firstMeasure..MidiPlayer.getInstance(findContext()).song.countMeasureHeaders()) {
            add(getItemText(measure))
        }
    }

    private fun getItemText(measure: Int): String {
        val header: TGMeasureHeader =
            MidiPlayer.getInstance(findContext()).song.getMeasureHeader(measure - 1)
        return "#$measure" + if (header.hasMarker()) " (${header.getMarker().getTitle()})" else ""
    }

    private fun getMeasureNb(spinner: Spinner): Int =
        Regex("#(\\d+) ?.*").replace(spinner.selectedItem.toString(), "$1").toInt()

    private fun enableTrainerMode(isTrainer: Boolean) {
        simpleLabel.isEnabled = !isTrainer
        simplePercentLabel.isEnabled = !isTrainer
        simplePercent.isEnabled = !isTrainer
        simpleLoop.isEnabled = !isTrainer
        customLabel.isEnabled = isTrainer
        customFromLabel.isEnabled = isTrainer
        customFrom.isEnabled = isTrainer
        customToLabel.isEnabled = isTrainer
        customTo.isEnabled = isTrainer
        customIncrementLabel.isEnabled = isTrainer
        customIncrement.isEnabled = isTrainer
    }

    private fun enableLoop(enable: Boolean) {
        loopLabel.isEnabled = enable
        loopFromLabel.isEnabled = enable
        loopFrom.isEnabled = enable
        loopToLabel.isEnabled = enable
        loopTo.isEnabled = enable
    }

    private fun selectedInt(spinner: Spinner): Int = spinner.selectedItem as Int

    protected companion object {
        const val MIN_SELECTION = 1
        const val MAX_SELECTION = 500
    }
}
