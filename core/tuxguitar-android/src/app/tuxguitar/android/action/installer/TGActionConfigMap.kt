package app.tuxguitar.android.action.installer

import app.tuxguitar.android.action.TGActionMap
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction
import app.tuxguitar.android.action.impl.caret.TGGoDownAction
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction
import app.tuxguitar.android.action.impl.caret.TGGoRightAction
import app.tuxguitar.android.action.impl.caret.TGGoUpAction
import app.tuxguitar.android.action.impl.caret.TGMoveToAction
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction
import app.tuxguitar.android.action.impl.edit.TGSetVoice1Action
import app.tuxguitar.android.action.impl.edit.TGSetVoice2Action
import app.tuxguitar.android.action.impl.gui.TGBackAction
import app.tuxguitar.android.action.impl.gui.TGExitAction
import app.tuxguitar.android.action.impl.gui.TGFinishAction
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction
import app.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction
import app.tuxguitar.android.action.impl.intent.TGProcessIntentAction
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction
import app.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction
import app.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction
import app.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction
import app.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction
import app.tuxguitar.android.action.impl.track.TGGoFirstTrackAction
import app.tuxguitar.android.action.impl.track.TGGoLastTrackAction
import app.tuxguitar.android.action.impl.track.TGGoNextTrackAction
import app.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction
import app.tuxguitar.android.action.impl.transport.TGTransportSetLoopEHeaderAction
import app.tuxguitar.android.action.impl.transport.TGTransportSetLoopSHeaderAction
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction
import app.tuxguitar.android.action.listener.cache.TGUpdateController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedMeasureController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedTrackController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateChannelsController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateItemsController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateLoadedSongController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateMeasureController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedChannelController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedDurationController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedNoteController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedVelocityController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdatePlayerTracksController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedMeasureController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedTrackController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateSavedSongController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateShiftedNoteController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateSongController
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateTransportPositionController
import app.tuxguitar.editor.action.channel.TGAddChannelAction
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction
import app.tuxguitar.editor.action.channel.TGSetChannelsAction
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction
import app.tuxguitar.editor.action.composition.TGChangeClefAction
import app.tuxguitar.editor.action.composition.TGChangeInfoAction
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction
import app.tuxguitar.editor.action.composition.TGChangeTempoAction
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction
import app.tuxguitar.editor.action.duration.TGSetDurationAction
import app.tuxguitar.editor.action.duration.TGSetEighthDurationAction
import app.tuxguitar.editor.action.duration.TGSetHalfDurationAction
import app.tuxguitar.editor.action.duration.TGSetQuarterDurationAction
import app.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction
import app.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction
import app.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction
import app.tuxguitar.editor.action.duration.TGSetWholeDurationAction
import app.tuxguitar.editor.action.edit.TGRedoAction
import app.tuxguitar.editor.action.edit.TGUndoAction
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction
import app.tuxguitar.editor.action.effect.TGChangePoppingAction
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction
import app.tuxguitar.editor.action.effect.TGChangeTappingAction
import app.tuxguitar.editor.action.effect.TGChangeTremoloBarAction
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction
import app.tuxguitar.editor.action.file.TGLoadSongAction
import app.tuxguitar.editor.action.file.TGLoadTemplateAction
import app.tuxguitar.editor.action.file.TGNewSongAction
import app.tuxguitar.editor.action.file.TGReadSongAction
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.editor.action.measure.TGAddMeasureAction
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction
import app.tuxguitar.editor.action.measure.TGCleanMeasureAction
import app.tuxguitar.editor.action.measure.TGCleanMeasureListAction
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction
import app.tuxguitar.editor.action.measure.TGCopyMeasureFromAction
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction
import app.tuxguitar.editor.action.note.TGChangeNoteAction
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction
import app.tuxguitar.editor.action.note.TGChangeStrokeAction
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction
import app.tuxguitar.editor.action.note.TGChangeVelocityAction
import app.tuxguitar.editor.action.note.TGCleanBeatAction
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction
import app.tuxguitar.editor.action.note.TGInsertTextAction
import app.tuxguitar.editor.action.note.TGMoveBeatsAction
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction
import app.tuxguitar.editor.action.note.TGRemoveTextAction
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction
import app.tuxguitar.editor.action.song.TGClearSongAction
import app.tuxguitar.editor.action.song.TGCopySongFromAction
import app.tuxguitar.editor.action.track.TGAddNewTrackAction
import app.tuxguitar.editor.action.track.TGAddTrackAction
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction
import app.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction
import app.tuxguitar.editor.action.track.TGCloneTrackAction
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction
import app.tuxguitar.editor.action.track.TGRemoveTrackAction
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction
import app.tuxguitar.editor.action.track.TGSetTrackNameAction
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction
import app.tuxguitar.editor.undo.TGUndoableActionController
import app.tuxguitar.editor.undo.impl.channel.TGUndoableChannelGenericController
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableAltRepeatController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableClefController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableCloseRepeatController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableKeySignatureController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableOpenRepeatController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableSongInfoController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTempoController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTimeSignatureController
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTripletFeelController
import app.tuxguitar.editor.undo.impl.measure.TGUndoableAddMeasureController
import app.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGenericController
import app.tuxguitar.editor.undo.impl.measure.TGUndoableRemoveMeasureController
import app.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController
import app.tuxguitar.editor.undo.impl.track.TGUndoableAddTrackController
import app.tuxguitar.editor.undo.impl.track.TGUndoableCloneTrackController
import app.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackDownController
import app.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackUpController
import app.tuxguitar.editor.undo.impl.track.TGUndoableRemoveTrackController
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackInfoController
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackSoloMuteController

class TGActionConfigMap : TGActionMap<TGActionConfig>() {
    init {
        createConfigMap()
    }

    fun createConfigMap() {
        map(TGLoadSongAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_SONG_LOADED_CTL)
        map(TGNewSongAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGLoadTemplateAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGReadSongAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGWriteSongAction.NAME, LOCKABLE, UPDATE_SONG_SAVED_CTL)

        map(TGUndoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGRedoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetVoice1Action.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGSetVoice2Action.NAME, LOCKABLE, UPDATE_ITEMS_CTL)

        map(TGMoveToAction.NAME, LOCKABLE, TGUpdateTransportPositionController())
        map(TGGoRightAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGGoLeftAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGGoUpAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGGoDownAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGMoveToAxisPositionAction.NAME, LOCKABLE, null)

        map(TGCopySongFromAction.NAME, LOCKABLE, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC)
        map(TGClearSongAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)

        map(TGAddTrackAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateAddedTrackController(), TGUndoableAddTrackController())
        map(TGAddNewTrackAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateAddedTrackController(), TGUndoableAddTrackController())
        map(TGSetTrackMuteAction.NAME, LOCKABLE, TGUpdatePlayerTracksController(), TGUndoableTrackSoloMuteController())
        map(TGSetTrackSoloAction.NAME, LOCKABLE, TGUpdatePlayerTracksController(), TGUndoableTrackSoloMuteController())
        map(TGChangeTrackMuteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGChangeTrackSoloAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGCloneTrackAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableCloneTrackController())
        map(TGGoFirstTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoLastTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoNextTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoPreviousTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoToTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGMoveTrackDownAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableMoveTrackDownController())
        map(TGMoveTrackUpAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableMoveTrackUpController())
        map(TGRemoveTrackAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateRemovedTrackController(), TGUndoableRemoveTrackController())
        map(TGSetTrackInfoAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL, TGUndoableTrackInfoController())
        map(TGSetTrackNameAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGSetTrackChannelAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC)
        map(TGSetTrackStringCountAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC)
        map(TGChangeTrackTuningAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC)
        map(TGChangeTrackPropertiesAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGCopyTrackFromAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC)

        map(TGAddMeasureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateAddedMeasureController(), TGUndoableAddMeasureController())
        map(TGAddMeasureListAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGCleanMeasureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGCleanMeasureListAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGGoFirstMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoLastMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoNextMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGGoPreviousMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGRemoveMeasureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateRemovedMeasureController(), TGUndoableRemoveMeasureController())
        map(TGRemoveMeasureRangeAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGCopyMeasureFromAction.NAME, LOCKABLE, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGInsertMeasuresAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC)
        map(TGCopyMeasureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGPasteMeasureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC)
        map(TGFixMeasureVoiceAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)

        map(TGChangeNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MODIFIED_NOTE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeTiedNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeVelocityAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateModifiedVelocityController(), UNDOABLE_MEASURE_GENERIC)
        map(TGCleanBeatAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGDecrementNoteSemitoneAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGDeleteNoteOrRestAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGIncrementNoteSemitoneAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGInsertRestBeatAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGMoveBeatsAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC)
        map(TGMoveBeatsLeftAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGMoveBeatsRightAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGRemoveUnusedVoiceAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGSetVoiceAutoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGSetVoiceDownAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGSetVoiceUpAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGShiftNoteDownAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC)
        map(TGShiftNoteUpAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC)
        map(TGChangePickStrokeDownAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangePickStrokeUpAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeStrokeAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGInsertTextAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGRemoveTextAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        for (i in 0 until 10) {
            map(TGSetNoteFretNumberAction.getActionName(i), LOCKABLE or DISABLE_ON_PLAY, UPDATE_MODIFIED_NOTE_CTL, UNDOABLE_MEASURE_GENERIC)
        }

        map(TGChangeAccentuatedNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeBendNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeDeadNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeFadeInAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeGhostNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeGraceNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeHammerNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeHarmonicNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeHeavyAccentuatedNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeLetRingAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangePalmMuteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangePoppingAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeSlappingAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeSlideNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeStaccatoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeTappingAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeTremoloBarAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeTremoloPickingAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeTrillNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)
        map(TGChangeVibratoNoteAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC)

        map(TGSetDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, TGUpdateModifiedDurationController(), UNDOABLE_MEASURE_GENERIC)
        map(TGSetWholeDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetHalfDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetQuarterDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetEighthDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetSixteenthDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetThirtySecondDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetSixtyFourthDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGSetDivisionTypeDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGChangeDottedDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGChangeDoubleDottedDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGIncrementDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGDecrementDurationAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)

        map(TGChangeTempoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, TGUndoableTempoController())
        map(TGChangeTempoRangeAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGChangeClefAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableClefController())
        map(TGChangeTimeSignatureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableTimeSignatureController())
        map(TGChangeKeySignatureAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableKeySignatureController())
        map(TGChangeTripletFeelAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL, TGUndoableTripletFeelController())
        map(TGChangeInfoAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, TGUndoableSongInfoController())
        map(TGRepeatOpenAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, TGUndoableOpenRepeatController())
        map(TGRepeatCloseAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, TGUndoableCloseRepeatController())
        map(TGRepeatAlternativeAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, TGUndoableAltRepeatController())

        map(TGSetChannelsAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC)
        map(TGAddChannelAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC)
        map(TGAddNewChannelAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC)
        map(TGRemoveChannelAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC)
        map(TGUpdateChannelAction.NAME, LOCKABLE, TGUpdateModifiedChannelController(), TGUndoableModifyChannelController())

        map(TGTransportPlayAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportStopAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportMetronomeAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportCountDownAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportSetLoopSHeaderAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportSetLoopEHeaderAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGTransportLoadSettingsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)

        map(TGSetLayoutScaleAction.NAME, LOCKABLE or DISABLE_ON_PLAY, UPDATE_SONG_CTL)
        map(TGSetScoreEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL)
        map(TGSetChordNameEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL)
        map(TGSetChordDiagramEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL)
        map(TGToggleHighlightPlayedBeatAction.NAME, LOCKABLE, UPDATE_SONG_CTL)

        map(TGToggleTabKeyboardAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGShowSmartMenuAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)

        map(TGBrowserCloseAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserCdRootAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserCdUpAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserCdElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserRefreshAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserOpenElementAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)
        map(TGBrowserSaveElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserSaveNewElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserSaveCurrentElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserPrepareForReadAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserPrepareForWriteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserLoadSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserOpenSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserCloseSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserAddCollectionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserRemoveCollectionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGBrowserRunnableAction.NAME, LOCKABLE, null)

        map(TGUriReadAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGUriWriteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGOpenDocumentAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGSaveDocumentAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGSaveDocumentAsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)
        map(TGStorageLoadSettingsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL)

        map(TGProcessIntentAction.NAME, LOCKABLE or STOP_TRANSPORT or DISABLE_ON_PLAY, UPDATE_ITEMS_CTL)

        map(TGOpenDialogAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGOpenMenuAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGOpenCabMenuAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGOpenFragmentAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGStartActivityForResultAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGRequestPermissionsAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGBackAction.NAME, LOCKABLE or SYNC_THREAD, UPDATE_ITEMS_CTL)
        map(TGExitAction.NAME, LOCKABLE, null)
        map(TGFinishAction.NAME, LOCKABLE or SYNC_THREAD, null)
    }

    private fun map(actionId: String, flags: Int, updateController: TGUpdateController?, undoableController: TGUndoableActionController? = null) {
        val tgActionConfig = TGActionConfig()
        tgActionConfig.updateController = updateController
        tgActionConfig.undoableController = undoableController
        tgActionConfig.lockableAction = (flags and LOCKABLE) != 0
        tgActionConfig.disableOnPlaying = (flags and DISABLE_ON_PLAY) != 0
        tgActionConfig.stopTransport = (flags and STOP_TRANSPORT) != 0
        tgActionConfig.syncThread = (flags and SYNC_THREAD) != 0
        tgActionConfig.documentModifier = undoableController != null
        set(actionId, tgActionConfig)
    }

    companion object {
        const val LOCKABLE = 0x01
        const val SYNC_THREAD = 0x02
        const val DISABLE_ON_PLAY = 0x04
        const val STOP_TRANSPORT = 0x08
        const val DISABLE_PROCESSING = 0x10

        private val UPDATE_ITEMS_CTL = TGUpdateItemsController()
        private val UPDATE_MEASURE_CTL = TGUpdateMeasureController()
        private val UPDATE_SONG_CTL = TGUpdateSongController()
        private val UPDATE_SONG_LOADED_CTL = TGUpdateLoadedSongController()
        private val UPDATE_SONG_SAVED_CTL = TGUpdateSavedSongController()
        private val UPDATE_CHANNELS_CTL = TGUpdateChannelsController()
        private val UPDATE_MODIFIED_NOTE_CTL = TGUpdateModifiedNoteController()

        private val UNDOABLE_SONG_GENERIC = TGUndoableSongGenericController()
        private val UNDOABLE_MEASURE_GENERIC = TGUndoableMeasureGenericController()
        private val UNDOABLE_TRACK_GENERIC = TGUndoableTrackGenericController()
        private val UNDOABLE_CHANNEL_GENERIC = TGUndoableChannelGenericController()
    }
}
