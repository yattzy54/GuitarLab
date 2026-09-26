package app.tuxguitar.android.view.channel

import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.fragment.impl.TGChannelListFragment
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong

class TGChannelActionHandler(private val channelList: TGChannelListFragment) {

    fun createAction(actionId: String): TGActionProcessorListener {
        return TGActionProcessorListener(this.channelList.findContext(), actionId)
    }

    fun createAddChannelAction(): TGActionProcessorListener {
        return this.createAction(TGAddNewChannelAction.NAME)
    }

    fun createUpdateVolumeAction(channel: TGChannel, volume: Short): TGActionProcessorListener {
        val tgActionProcessor = this.createUpdateChannelAction(channel)
        tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_VOLUME, volume)
        return tgActionProcessor
    }

    fun createUpdateChannelAction(channel: TGChannel): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGUpdateChannelAction.NAME)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel)
        return tgActionProcessor
    }

    fun createEditChannelAction(channel: TGChannel): TGActionProcessorListener {
        val tgActionProcessor = this.createOpenDialogAction(TGChannelEditDialogController())
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel)
        return tgActionProcessor
    }

    fun createOpenDialogAction(controller: TGDialogController): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGOpenDialogAction.NAME)
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.channelList.findActivity())
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
        return tgActionProcessor
    }

    fun isRemovableChannel(channel: TGChannel): Boolean {
        val documentManager = TGDocumentManager.getInstance(this.channelList.findContext())
        val song: TGSong = documentManager.getSong()
        val songManager: TGSongManager = documentManager.getSongManager()
        return !songManager.isAnyTrackConnectedToChannel(song, channel.channelId)
    }

    fun removeChannel(channel: TGChannel) {
        val tgActionProcessor: TGActionProcessor = this.createAction(TGRemoveChannelAction.NAME)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel)
        processConfirmableAction(
            tgActionProcessor,
            channelList.findActivity().getString(
                app.tuxguitar.android.R.string.action_channel_list_item_remove_confirm_question
            )
        )
    }

    fun processConfirmableAction(actionProcessor: TGActionProcessor, confirmMessage: String) {
        val tgActionProcessor = this.createOpenDialogAction(TGConfirmDialogController())
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage)
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, Runnable { actionProcessor.process() })
        tgActionProcessor.process()
    }
}
