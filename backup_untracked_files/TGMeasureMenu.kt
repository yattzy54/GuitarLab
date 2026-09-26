package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.measure.*
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController
import app.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController
import app.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.helpers.TGMeasureError

class TGMeasureMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_measure, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val running = MidiPlayer.getInstance(context).isRunning
        val caret = TGSongViewController.getInstance(context).caret
        val measure = requireNotNull(caret.measure)
        val isFirst = measure.number == 1
        val isLast = measure.number == measure.track.countMeasures()
        val errors: List<TGMeasureError> =
            caret.getSongManager().measureManager.getMeasureErrors(measure)
        val voiceIndex = caret.getVoice()
        var voiceCanBeFixed = errors.any { it.voiceIndex == voiceIndex }
        if (voiceCanBeFixed) {
            voiceCanBeFixed = errors.filter { it.voiceIndex == voiceIndex }.all { it.canBeFixed() }
        }
        initializeItem(menu, R.id.action_measure_first, createActionProcessor(TGGoFirstMeasureAction.NAME), !isFirst)
        initializeItem(menu, R.id.action_measure_previous, createActionProcessor(TGGoPreviousMeasureAction.NAME), !isFirst)
        initializeItem(menu, R.id.action_measure_next, createActionProcessor(TGGoNextMeasureAction.NAME), !isLast)
        initializeItem(menu, R.id.action_measure_last, createActionProcessor(TGGoLastMeasureAction.NAME), !isLast)
        initializeItem(menu, R.id.action_measure_add, TGMeasureAddDialogController(), !running)
        initializeItem(menu, R.id.action_measure_clean, TGMeasureCleanDialogController(), !running)
        initializeItem(menu, R.id.action_measure_remove, TGMeasureRemoveDialogController(), !running)
        initializeItem(menu, R.id.action_remove_unused_voice, createActionProcessor(TGRemoveUnusedVoiceAction.NAME), !running)
        val fixProcessor = createActionProcessor(TGFixMeasureVoiceAction.NAME).apply {
            setAttribute(TGFixMeasureVoiceAction.ATTRIBUTE_VOICE_INDEX, voiceIndex)
        }
        initializeItem(menu, R.id.action_fix_voice, fixProcessor, !running && voiceCanBeFixed)
    }
}
