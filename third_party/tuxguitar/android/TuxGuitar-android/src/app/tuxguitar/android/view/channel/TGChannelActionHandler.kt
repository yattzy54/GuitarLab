package app.tuxguitar.android.view.channel

import android.view.View
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction
import app.tuxguitar.song.models.TGChannel

class TGChannelActionHandler(private val channelList: TGChannelListView) {

    fun createAction(actionId: String): TGActionProcessorListener {
        return TGActionProcessorListener(this.channelList.findContext(), actionId)
    }

    fun createAddChannelAction(): TGActionProcessorListener {
        return this.createAction(TGAddNewChannelAction.NAME)
    }

    fun createRemoveChannelAction(channel: TGChannel): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGRemoveChannelAction.NAME)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel)
        return tgActionProcessor
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

    fun createOpenCabMenuAction(controller: TGMenuController, selectableView: View): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGOpenCabMenuAction.NAME)
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.channelList.findActivity())
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, selectableView)
        return tgActionProcessor
    }

    fun createChannelItemMenuAction(channel: TGChannel, selectableView: View): TGActionProcessorListener {
        return this.createOpenCabMenuAction(TGChannelListItemMenu(this.channelList.findActivity(), channel), selectableView)
    }

    fun processConfirmableAction(actionProcessor: TGActionProcessor, confirmMessage: String) {
        val tgActionProcessor = this.createOpenDialogAction(TGConfirmDialogController())
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage)
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, Runnable { actionProcessor.process() })
        tgActionProcessor.process()
    }
}
