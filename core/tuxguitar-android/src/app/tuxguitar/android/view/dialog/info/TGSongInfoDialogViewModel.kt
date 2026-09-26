package app.tuxguitar.android.view.dialog.info

import app.tuxguitar.android.ui.state.EditorStateViewModel
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

class TGSongInfoDialogViewModel(initial: TGSongInfoFields) : EditorStateViewModel<TGSongInfoFields>(initial) {
    fun onNameChanged(value: String = "") {
        update { it.copy(name = value) }
    }

    fun onArtistChanged(value: String = "") {
        update { it.copy(artist = value) }
    }

    fun onAlbumChanged(value: String = "") {
        update { it.copy(album = value) }
    }

    fun onAuthorChanged(value: String = "") {
        update { it.copy(author = value) }
    }

    fun onDateChanged(value: String = "") {
        update { it.copy(date = value) }
    }

    fun onCopyrightChanged(value: String = "") {
        update { it.copy(copyright = value) }
    }

    fun onWriterChanged(value: String = "") {
        update { it.copy(writer = value) }
    }

    fun onTranscriberChanged(value: String = "") {
        update { it.copy(transcriber = value) }
    }

    fun onCommentsChanged(value: String = "") {
        update { it.copy(comments = value) }
    }
}
