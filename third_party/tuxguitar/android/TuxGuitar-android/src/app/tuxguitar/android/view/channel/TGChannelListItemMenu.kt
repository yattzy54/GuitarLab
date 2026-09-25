package app.tuxguitar.android.view.channel

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong

class TGChannelListItemMenu(activity: TGActivity, private val channel: TGChannel) : TGMenuBase(activity) {

    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_channel_list_item, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        this.initializeItem(menu, R.id.action_channel_list_item_edit, this.createEditChannelAction(), true)
        if (this.isRemovableChannel()) {
            this.initializeItem(menu, R.id.action_channel_list_item_remove, this.createRemoveChannelAction(), true)
        }
    }

    fun isRemovableChannel(): Boolean {
        val documentManager = TGDocumentManager.getInstance(this.findContext())
        val song: TGSong = documentManager.getSong()
        val songManager: TGSongManager = documentManager.getSongManager()

        return !songManager.isAnyTrackConnectedToChannel(song, this.channel.channelId)
    }

    fun createEditChannelAction(): TGActionProcessorListener {
        val tgActionProcessor = this.createDialogActionProcessor(TGChannelEditDialogController())
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel)
        return tgActionProcessor
    }

    fun createRemoveChannelAction(): TGActionProcessorListener {
        val tgActionProcessor: TGActionProcessor = this.createActionProcessor(TGRemoveChannelAction.NAME)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel)
        return this.createConfirmableActionProcessor(tgActionProcessor, this.activity.getString(R.string.action_channel_list_item_remove_confirm_question))
    }
}
