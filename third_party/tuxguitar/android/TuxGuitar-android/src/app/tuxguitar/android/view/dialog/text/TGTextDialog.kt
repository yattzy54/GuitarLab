package app.tuxguitar.android.view.dialog.text

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.EditText
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.note.TGInsertTextAction
import app.tuxguitar.editor.action.note.TGRemoveTextAction
import app.tuxguitar.song.models.TGBeat

class TGTextDialog : TGModalFragment(R.layout.view_text_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.text_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            doInsertText()
            close()
            true
        }
        menu.findItem(R.id.action_clean).setOnMenuItemClickListener {
            doRemoveText()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        fillTextValue()
    }

    fun setTextFieldValue(textFieldId: Int, value: String?) {
        requireView().findViewById<EditText>(textFieldId).text.append(value)
    }

    fun getTextFieldValue(textFieldId: Int): String =
        requireView().findViewById<EditText>(textFieldId).text.toString()

    fun fillTextValue() {
        val text = getBeat()?.text?.value ?: ""
        setTextFieldValue(R.id.text_dlg_value, text)
    }

    fun findTextValue(): String = getTextFieldValue(R.id.text_dlg_value)

    fun doInsertText() {
        val processor = TGActionProcessor(findContext(), TGInsertTextAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, findTextValue())
        processor.process()
    }

    fun doRemoveText() {
        val processor = TGActionProcessor(findContext(), TGRemoveTextAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.process()
    }

    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}
