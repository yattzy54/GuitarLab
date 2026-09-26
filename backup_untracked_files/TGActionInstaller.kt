package app.tuxguitar.android.action.installer;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionAdapterManager;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import app.tuxguitar.android.action.impl.caret.TGGoDownAction;
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.android.action.impl.caret.TGGoRightAction;
import app.tuxguitar.android.action.impl.caret.TGGoUpAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction;
import app.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.android.action.impl.gui.TGBackAction;
import app.tuxguitar.android.action.impl.gui.TGExitAction;
import app.tuxguitar.android.action.impl.gui.TGFinishAction;
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import app.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import app.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.android.action.impl.transport.TGTransportSetLoopSHeaderAction;
import app.tuxguitar.android.action.impl.transport.TGTransportSetLoopEHeaderAction;
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction;
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import app.tuxguitar.editor.action.channel.TGSetChannelsAction;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.action.composition.TGChangeClefAction;
import app.tuxguitar.editor.action.composition.TGChangeInfoAction;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDurationAction;
import app.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import app.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import app.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.file.TGNewSongAction;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction;
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangeStrokeAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGInsertTextAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveTextAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.song.TGClearSongAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackNameAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.util.TGContext;

class TGActionInstaller(private val manager: TGActionAdapterManager) {
	private val configMap = TGActionConfigMap()


	

	fun installDefaultActions() {
		val context = manager.getContext()

		//file actions
		installAction(TGLoadSongAction(context));
		installAction(TGNewSongAction(context));
		installAction(TGLoadTemplateAction(context));
		installAction(TGReadSongAction(context));
		installAction(TGWriteSongAction(context));

		//edit actions
		installAction(TGUndoAction(context));
		installAction(TGRedoAction(context));
		installAction(TGSetVoice1Action(context));
		installAction(TGSetVoice2Action(context));

		//caret actions
		installAction(TGMoveToAction(context));
		installAction(TGGoRightAction(context));
		installAction(TGGoLeftAction(context));
		installAction(TGGoUpAction(context));
		installAction(TGGoDownAction(context));
		installAction(TGMoveToAxisPositionAction(context));

		//song actions
		installAction(TGCopySongFromAction(context));
		installAction(TGClearSongAction(context));

		//track actions
		installAction(TGAddTrackAction(context));
		installAction(TGAddNewTrackAction(context));
		installAction(TGSetTrackMuteAction(context));
		installAction(TGSetTrackSoloAction(context));
		installAction(TGChangeTrackMuteAction(context));
		installAction(TGChangeTrackSoloAction(context));
		installAction(TGCloneTrackAction(context));
		installAction(TGGoFirstTrackAction(context));
		installAction(TGGoLastTrackAction(context));
		installAction(TGGoNextTrackAction(context));
		installAction(TGGoPreviousTrackAction(context));
		installAction(TGGoToTrackAction(context));
		installAction(TGMoveTrackDownAction(context));
		installAction(TGMoveTrackUpAction(context));
		installAction(TGRemoveTrackAction(context));
		installAction(TGSetTrackInfoAction(context));
		installAction(TGSetTrackNameAction(context));
		installAction(TGSetTrackChannelAction(context));
		installAction(TGSetTrackStringCountAction(context));
		installAction(TGChangeTrackTuningAction(context));
		installAction(TGCopyTrackFromAction(context));
		installAction(TGChangeTrackPropertiesAction(context));

		//measure actions
		installAction(TGAddMeasureAction(context));
		installAction(TGAddMeasureListAction(context));
		installAction(TGCleanMeasureAction(context));
		installAction(TGCleanMeasureListAction(context));
		installAction(TGGoFirstMeasureAction(context));
		installAction(TGGoLastMeasureAction(context));
		installAction(TGGoNextMeasureAction(context));
		installAction(TGGoPreviousMeasureAction(context));
		installAction(TGRemoveMeasureAction(context));
		installAction(TGRemoveMeasureRangeAction(context));
		installAction(TGInsertMeasuresAction(context));
		installAction(TGCopyMeasureFromAction(context));
		installAction(TGCopyMeasureAction(context));
		installAction(TGPasteMeasureAction(context));
		installAction(TGFixMeasureVoiceAction(context));
		//beat actions
		installAction(TGChangeNoteAction(context));
		installAction(TGChangeTiedNoteAction(context));
		installAction(TGChangeVelocityAction(context));
		installAction(TGCleanBeatAction(context));
		installAction(TGDecrementNoteSemitoneAction(context));
		installAction(TGDeleteNoteOrRestAction(context));
		installAction(TGIncrementNoteSemitoneAction(context));
		installAction(TGInsertRestBeatAction(context));
		installAction(TGMoveBeatsAction(context));
		installAction(TGMoveBeatsLeftAction(context));
		installAction(TGMoveBeatsRightAction(context));
		installAction(TGRemoveUnusedVoiceAction(context));
		installAction(TGSetVoiceAutoAction(context));
		installAction(TGSetVoiceDownAction(context));
		installAction(TGSetVoiceUpAction(context));
		installAction(TGShiftNoteDownAction(context));
		installAction(TGShiftNoteUpAction(context));
		installAction(TGChangePickStrokeDownAction(context));
		installAction(TGChangePickStrokeUpAction(context));
		installAction(TGChangeStrokeAction(context));
		installAction(TGInsertTextAction(context));
		installAction(TGRemoveTextAction(context));
		for (i in 0 until 10) {
			installAction(TGSetNoteFretNumberAction(context, i));
		}

		//effect actions
		installAction(TGChangeAccentuatedNoteAction(context));
		installAction(TGChangeBendNoteAction(context));
		installAction(TGChangeDeadNoteAction(context));
		installAction(TGChangeFadeInAction(context));
		installAction(TGChangeGhostNoteAction(context));
		installAction(TGChangeGraceNoteAction(context));
		installAction(TGChangeHammerNoteAction(context));
		installAction(TGChangeHarmonicNoteAction(context));
		installAction(TGChangeHeavyAccentuatedNoteAction(context));
		installAction(TGChangeLetRingAction(context));
		installAction(TGChangePalmMuteAction(context));
		installAction(TGChangePoppingAction(context));
		installAction(TGChangeSlappingAction(context));
		installAction(TGChangeSlideNoteAction(context));
		installAction(TGChangeStaccatoAction(context));
		installAction(TGChangeTappingAction(context));
		installAction(TGChangeTremoloBarAction(context));
		installAction(TGChangeTremoloPickingAction(context));
		installAction(TGChangeTrillNoteAction(context));
		installAction(TGChangeVibratoNoteAction(context));

		//duration actions
		installAction(TGSetDurationAction(context));
		installAction(TGSetWholeDurationAction(context));
		installAction(TGSetHalfDurationAction(context));
		installAction(TGSetQuarterDurationAction(context));
		installAction(TGSetEighthDurationAction(context));
		installAction(TGSetSixteenthDurationAction(context));
		installAction(TGSetThirtySecondDurationAction(context));
		installAction(TGSetSixtyFourthDurationAction(context));
		installAction(TGSetDivisionTypeDurationAction(context));
		installAction(TGChangeDottedDurationAction(context));
		installAction(TGChangeDoubleDottedDurationAction(context));
		installAction(TGIncrementDurationAction(context));
		installAction(TGDecrementDurationAction(context));

		//composition actions
		installAction(TGChangeTempoAction(context));
		installAction(TGChangeTempoRangeAction(context));
		installAction(TGChangeClefAction(context));
		installAction(TGChangeTimeSignatureAction(context));
		installAction(TGChangeKeySignatureAction(context));
		installAction(TGChangeTripletFeelAction(context));
		installAction(TGChangeInfoAction(context));
		installAction(TGRepeatOpenAction(context));
		installAction(TGRepeatCloseAction(context));
		installAction(TGRepeatAlternativeAction(context));

		//channel actions
		installAction(TGSetChannelsAction(context));
		installAction(TGAddChannelAction(context));
		installAction(TGAddNewChannelAction(context));
		installAction(TGRemoveChannelAction(context));
		installAction(TGUpdateChannelAction(context));

		//transport actions
		installAction(TGTransportPlayAction(context));
		installAction(TGTransportStopAction(context));
		installAction(TGTransportMetronomeAction(context));
		installAction(TGTransportCountDownAction(context));
		installAction(TGTransportSetLoopSHeaderAction(context));
		installAction(TGTransportSetLoopEHeaderAction(context));
		installAction(TGTransportLoadSettingsAction(context));

		//layout actions
		installAction(TGSetLayoutScaleAction(context));
		installAction(TGSetScoreEnabledAction(context));
		installAction(TGSetChordNameEnabledAction(context));
		installAction(TGSetChordDiagramEnabledAction(context));
		installAction(TGToggleHighlightPlayedBeatAction(context));

		//view actions
		installAction(TGToggleTabKeyboardAction(context));
		installAction(TGShowSmartMenuAction(context));

		//storage
		installAction(TGUriReadAction(context));
		installAction(TGUriWriteAction(context));
		installAction(TGOpenDocumentAction(context));
		installAction(TGSaveDocumentAction(context));
		installAction(TGSaveDocumentAsAction(context));
		installAction(TGStorageLoadSettingsAction(context));

		//browser actions
		installAction(TGBrowserCloseAction(context));
		installAction(TGBrowserCdRootAction(context));
		installAction(TGBrowserCdUpAction(context));
		installAction(TGBrowserCdElementAction(context));
		installAction(TGBrowserRefreshAction(context));
		installAction(TGBrowserOpenElementAction(context));
		installAction(TGBrowserSaveElementAction(context));
		installAction(TGBrowserSaveNewElementAction(context));
		installAction(TGBrowserSaveCurrentElementAction(context));
		installAction(TGBrowserPrepareForReadAction(context));
		installAction(TGBrowserPrepareForWriteAction(context));
		installAction(TGBrowserLoadSessionAction(context));
		installAction(TGBrowserOpenSessionAction(context));
		installAction(TGBrowserCloseSessionAction(context));
		installAction(TGBrowserAddCollectionAction(context));
		installAction(TGBrowserRemoveCollectionAction(context));
		installAction(TGBrowserRunnableAction(context));

		//intent actions
		installAction(TGProcessIntentAction(context));

		//gui actions
		installAction(TGBackAction(context));
		installAction(TGExitAction(context));
		installAction(TGFinishAction(context));
		installAction(TGOpenDialogAction(context));
		installAction(TGOpenMenuAction(context));
		installAction(TGOpenCabMenuAction(context));
		installAction(TGOpenFragmentAction(context));
		installAction(TGStartActivityForResultAction(context));
		installAction(TGRequestPermissionsAction(context));
	}

	fun installAction(action: TGActionBase) {
		val actionId = action.name

		TGActionManager.getInstance(manager.getContext()).mapAction(actionId, action);
		val config = configMap.get(actionId)
		if (config != null) {
			if( config.disableOnPlaying ) {
				manager.getDisableOnPlayInterceptor().addActionId(actionId);
			}
			if( config.stopTransport ) {
				manager.getStopTransportInterceptor().addActionId(actionId);
			}
			if( config.syncThread ) {
				manager.getSyncThreadInterceptor().addActionId(actionId);
			}
			if( config.lockableAction ) {
				manager.getLockableActionListener().addActionId(actionId);
			}

			manager.getUpdatableActionListener().getControllers().set(actionId, config.updateController);
			manager.getUndoableActionListener().getControllers().set(actionId, config.undoableController);
		}
	}
}
