package app.tuxguitar.android.view.dialog.repeat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.CheckBox
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGRepeatAlternativeDialog : TGModalFragment(R.layout.view_repeat_alternative) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.repeat_alternative_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu)
        menu.findItem(R.id.action_clean).setOnMenuItemClickListener {
            cleanRepeatAlternative()
            close()
            true
        }
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeRepeatAlternative()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val song = getSong()
        val header = getHeader()
        val existent = getExistentEndings(song, header)
        val selected = if (header.repeatAlternative > 0) {
            header.repeatAlternative
        } else {
            getDefaultEndings(existent)
        }
        updateSelections(getCheckBoxes(), existent, selected)
    }

    fun getCheckBoxes(): Array<CheckBox> = arrayOf(
        R.id.repeat_alternative_dlg_alt_1,
        R.id.repeat_alternative_dlg_alt_2,
        R.id.repeat_alternative_dlg_alt_3,
        R.id.repeat_alternative_dlg_alt_4,
        R.id.repeat_alternative_dlg_alt_5,
        R.id.repeat_alternative_dlg_alt_6,
        R.id.repeat_alternative_dlg_alt_7,
        R.id.repeat_alternative_dlg_alt_8
    ).map { requireView().findViewById<CheckBox>(it) }.toTypedArray()

    protected fun getExistentEndings(song: TGSong, measureHeader: TGMeasureHeader): Int {
        var existentEndings = 0
        val iterator = song.getMeasureHeaders()
        while (iterator.hasNext()) {
            val header = iterator.next()
            if (header.number == measureHeader.number) break
            if (header.isRepeatOpen) existentEndings = 0
            existentEndings = existentEndings or header.repeatAlternative
        }
        return existentEndings
    }

    protected fun getDefaultEndings(existentEndings: Int): Int {
        for (i in 0 until 8) {
            if (existentEndings and (1 shl i) == 0) return 1 shl i
        }
        return -1
    }

    fun updateSelections(selections: Array<CheckBox>, existentEndings: Int, selectedEndings: Int) {
        selections.forEachIndexed { index, checkBox ->
            val enabled = existentEndings and (1 shl index) == 0
            checkBox.isEnabled = enabled
            checkBox.isChecked = enabled && selectedEndings and (1 shl index) != 0
        }
    }

    fun parseRepeatAlternative(): Int {
        var repeatAlternative = 0
        getCheckBoxes().forEachIndexed { index, checkBox ->
            if (checkBox.isChecked) repeatAlternative = repeatAlternative or (1 shl index)
        }
        return repeatAlternative
    }

    fun cleanRepeatAlternative() {
        changeRepeatAlternative(0)
    }

    fun changeRepeatAlternative() {
        changeRepeatAlternative(parseRepeatAlternative())
    }

    fun changeRepeatAlternative(repeatAlternative: Int) {
        val processor = TGActionProcessor(findContext(), TGRepeatAlternativeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}
