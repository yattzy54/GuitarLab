package app.tuxguitar.android.view.dialog.info

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeInfoAction
import app.tuxguitar.song.models.TGSong

data class TGSongInfoFields(
    val name: String = "",
    val artist: String = "",
    val album: String = "",
    val author: String = "",
    val date: String = "",
    val copyright: String = "",
    val writer: String = "",
    val transcriber: String = "",
    val comments: String = "",
) {
    companion object {
        fun from(song: TGSong): TGSongInfoFields = TGSongInfoFields(
            name = song.name.orEmpty(),
            artist = song.artist.orEmpty(),
            album = song.album.orEmpty(),
            author = song.author.orEmpty(),
            date = song.date.orEmpty(),
            copyright = song.copyright.orEmpty(),
            writer = song.writer.orEmpty(),
            transcriber = song.transcriber.orEmpty(),
            comments = song.comments.orEmpty(),
        )
    }
}

class TGSongInfoDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val song = requireNotNull(getSong())
        TGSongInfoDialogContent(
            initial = TGSongInfoFields.from(song),
            onSave = { fields ->
                updateSongInfo(fields)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun updateSongInfo(fields: TGSongInfoFields) {
        val processor = TGActionProcessor(findContext(), TGChangeInfoAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, fields.name)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, fields.artist)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, fields.album)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, fields.author)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, fields.date)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, fields.copyright)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, fields.writer)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, fields.transcriber)
        processor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, fields.comments)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
}

@Composable
fun TGSongInfoDialogContent(
    initial: TGSongInfoFields,
    onSave: (TGSongInfoFields) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(initial.name) }
    var artist by remember { mutableStateOf(initial.artist) }
    var album by remember { mutableStateOf(initial.album) }
    var author by remember { mutableStateOf(initial.author) }
    var date by remember { mutableStateOf(initial.date) }
    var copyright by remember { mutableStateOf(initial.copyright) }
    var writer by remember { mutableStateOf(initial.writer) }
    var transcriber by remember { mutableStateOf(initial.transcriber) }
    var comments by remember { mutableStateOf(initial.comments) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.song_properties_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)

        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_name_label)) })
        OutlinedTextField(value = artist, onValueChange = { artist = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_artist_label)) })
        OutlinedTextField(value = album, onValueChange = { album = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_album_label)) })
        OutlinedTextField(value = author, onValueChange = { author = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_author_label)) })
        OutlinedTextField(value = date, onValueChange = { date = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_date_label)) })
        OutlinedTextField(value = copyright, onValueChange = { copyright = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_copyright_label)) })
        OutlinedTextField(value = writer, onValueChange = { writer = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_writer_label)) })
        OutlinedTextField(value = transcriber, onValueChange = { transcriber = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_transcriber_label)) })
        OutlinedTextField(value = comments, onValueChange = { comments = it }, modifier = fieldModifier, label = { Text(stringResource(R.string.song_properties_dlg_comments_label)) })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(
                onClick = {
                    onSave(
                        TGSongInfoFields(
                            name = name,
                            artist = artist,
                            album = album,
                            author = author,
                            date = date,
                            copyright = copyright,
                            writer = writer,
                            transcriber = transcriber,
                            comments = comments,
                        )
                    )
                },
            ) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGSongInfoDialogContentPreview() {
    GuitarLabTheme {
        TGSongInfoDialogContent(
            initial = TGSongInfoFields(
                name = "Sample Song",
                artist = "Sample Artist",
                album = "Sample Album",
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
