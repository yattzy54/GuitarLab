package app.tuxguitar.android.view.dialog.repeat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGRepeatCloseDialog : TGModalFragment(R.layout.view_repeat_close) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.repeat_close_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeRepeatClose()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val repeatCloseDefault = getHeader().repeatClose.coerceAtLeast(1)
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createRepeatValues()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.repeat_close_dlg_count_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(repeatCloseDefault))
    }

    fun createRepeatValues(): Array<Int> = Array(101) { it }

    fun parseRepeatValue(): Int =
        requireView().findViewById<Spinner>(R.id.repeat_close_dlg_count_value).selectedItem as Int

    fun changeRepeatClose() {
        val processor = TGActionProcessor(findContext(), TGRepeatCloseAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, parseRepeatValue())
        processor.processOnNewThread()
    }

    fun getSong(): TGSong =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}
