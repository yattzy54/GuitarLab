package app.tuxguitar.android.drawer.main

import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.fragment.impl.TGChannelListFragmentController
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.info.TGSongInfoDialogController
import app.tuxguitar.android.view.tablature.TGSongViewSmartMenu
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.file.TGLoadTemplateAction
import app.tuxguitar.editor.action.track.TGAddNewTrackAction
import app.tuxguitar.song.models.TGTrack

class TGMainDrawerActionHandler(private val mainDrawer: TGMainDrawer) {
    fun createAction(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(mainDrawer.findContext(), actionId)

    fun createGoToTrackAction(track: TGTrack): TGActionProcessorListener =
        createAction(TGGoToTrackAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)
        }

    fun createGoToTrackWithSmartMenuAction(track: TGTrack): TGActionProcessorListener =
        createGoToTrackAction(track).apply {
            setAttribute(TGSongViewSmartMenu.REQUEST_SMART_MENU, true)
            setAttribute(TGSongViewSmartMenu.TRACK_AREA_SELECTED, true)
        }

    fun createNewFileAction(): TGActionProcessorListener = createAction(TGLoadTemplateAction.NAME)
    fun createOpenFileAction(): TGActionProcessorListener = createAction(TGOpenDocumentAction.NAME)
    fun createSaveFileAsAction(): TGActionProcessorListener = createAction(TGSaveDocumentAsAction.NAME)
    fun createSaveFileAction(): TGActionProcessorListener = createAction(TGSaveDocumentAction.NAME)
    fun createAddTrackAction(): TGActionProcessorListener = createAction(TGAddNewTrackAction.NAME)

    fun createOpenInstrumentsAction(): TGActionProcessorListener =
        createFragmentAction(TGChannelListFragmentController.getInstance(mainDrawer.findContext()))

    fun createOpenInfoAction(): TGActionProcessorListener =
        createDialogAction(TGSongInfoDialogController())

    fun createFragmentAction(controller: TGFragmentController<*>): TGActionProcessorListener =
        createAction(TGOpenFragmentAction.NAME).apply {
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller)
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, mainDrawer.findActivity())
        }

    fun createDialogAction(controller: TGDialogController): TGActionProcessorListener =
        createAction(TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, mainDrawer.findActivity())
        }
}
