package app.tuxguitar.android.view.dialog.tempo

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTempo
import app.tuxguitar.song.models.TGTempoBase

class TGTempoDialog : TGModalFragment(R.layout.view_tempo_dialog) {
    private val tempoBase = TGTempoBase.getTempoBases()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.tempo_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeTempo()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val tempo = requireNotNull(getHeader()).tempo
        val group = requireView().findViewById<RadioGroup>(R.id.tempo_dlg_tempo_base)
        tempoBase.forEachIndexed { index, base ->
            val button = RadioButton(requireContext())
            group.addView(button)
            button.id = index
            var iconName = "duration_${base.base}"
            if (base.isDotted) iconName += "dotted"
            val context = requireContext()
            val iconId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
            button.setCompoundDrawablesWithIntrinsicBounds(
                context.getDrawable(iconId),
                null,
                null,
                null
            )
            button.text = " 1/${base.base}" + if (base.isDotted) "•" else ""
            button.isChecked = tempo.base == base.base && tempo.isDotted == base.isDotted
        }

        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createTempoValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.tempo_dlg_tempo_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(tempo.rawValue))

        val applyToDefault = TGChangeTempoRangeAction.APPLY_TO_NEXT
        updateRadio(
            requireView().findViewById(R.id.tempo_dlg_options_apply_to_song),
            TGChangeTempoRangeAction.APPLY_TO_ALL,
            applyToDefault
        )
        updateRadio(
            requireView().findViewById(R.id.tempo_dlg_options_apply_to_end),
            TGChangeTempoRangeAction.APPLY_TO_END,
            applyToDefault
        )
        updateRadio(
            requireView().findViewById(R.id.tempo_dlg_options_apply_to_next_marker),
            TGChangeTempoRangeAction.APPLY_TO_NEXT,
            applyToDefault
        )
    }

    fun createTempoValues(): Array<Int> =
        Array(TGChangeTempoRangeAction.MAX_TEMPO - TGChangeTempoRangeAction.MIN_TEMPO + 1) {
            it + TGChangeTempoRangeAction.MIN_TEMPO
        }

    fun updateRadio(button: RadioButton, value: Int, selection: Int?) {
        button.tag = value
        button.isChecked = selection == value
    }

    fun parseTempoValue(): Int =
        requireView().findViewById<Spinner>(R.id.tempo_dlg_tempo_value).selectedItem as Int

    fun parseTempoBase(): Int {
        val index = requireView().findViewById<RadioGroup>(R.id.tempo_dlg_tempo_base).checkedRadioButtonId
        return tempoBase[index].base
    }

    fun parseTempoBaseDotted(): Boolean {
        val index = requireView().findViewById<RadioGroup>(R.id.tempo_dlg_tempo_base).checkedRadioButtonId
        return tempoBase[index].isDotted
    }

    fun parseApplyTo(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.tempo_dlg_options_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) {
            group.findViewById<RadioButton>(id)?.tag as? Int ?: TGChangeTempoRangeAction.APPLY_TO_ALL
        } else {
            TGChangeTempoRangeAction.APPLY_TO_ALL
        }
    }

    fun changeTempo() {
        val processor = TGActionProcessor(findContext(), TGChangeTempoRangeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, parseTempoValue())
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, parseTempoBase())
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, parseTempoBaseDotted())
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, parseApplyTo())
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}
