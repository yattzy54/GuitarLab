package app.tuxguitar.android.view.dialog.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.EditText
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeInfoAction
import app.tuxguitar.song.models.TGSong

class TGSongInfoDialog : TGModalFragment(R.layout.view_song_info) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.song_properties_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            updateSongInfo()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        fillSongInfo()
    }

    fun setTextFieldValue(textFieldId: Int, value: String?) {
        requireView().findViewById<EditText>(textFieldId).text.append(value)
    }

    fun getTextFieldValue(textFieldId: Int): String =
        requireView().findViewById<EditText>(textFieldId).text.toString()

    fun fillSongInfo() {
        val song = requireNotNull(getSong())
        setTextFieldValue(R.id.song_properties_dlg_name_value, song.name)
        setTextFieldValue(R.id.song_properties_dlg_artist_value, song.artist)
        setTextFieldValue(R.id.song_properties_dlg_album_value, song.album)
        setTextFieldValue(R.id.song_properties_dlg_author_value, song.author)
        setTextFieldValue(R.id.song_properties_dlg_date_value, song.date)
        setTextFieldValue(R.id.song_properties_dlg_copyright_value, song.copyright)
        setTextFieldValue(R.id.song_properties_dlg_writer_value, song.writer)
        setTextFieldValue(R.id.song_properties_dlg_transcriber_value, song.transcriber)
        setTextFieldValue(R.id.song_properties_dlg_comments_value, song.comments)
    }

    fun updateSongInfo() {
        val processor = TGActionProcessor(findContext(), TGChangeInfoAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, getTextFieldValue(R.id.song_properties_dlg_name_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, getTextFieldValue(R.id.song_properties_dlg_artist_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, getTextFieldValue(R.id.song_properties_dlg_album_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, getTextFieldValue(R.id.song_properties_dlg_author_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, getTextFieldValue(R.id.song_properties_dlg_date_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, getTextFieldValue(R.id.song_properties_dlg_copyright_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, getTextFieldValue(R.id.song_properties_dlg_writer_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, getTextFieldValue(R.id.song_properties_dlg_transcriber_value))
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, getTextFieldValue(R.id.song_properties_dlg_comments_value))
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
}
