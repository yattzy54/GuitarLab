package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.action.duration.*
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.models.TGDivisionType
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.managers.TGSongManager

class TGDurationMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_duration, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val duration = TGSongViewController.getInstance(context).caret.duration
        val running = MidiPlayer.getInstance(context).isRunning
        initializeItem(menu, R.id.action_set_duration_whole, createActionProcessor(TGSetWholeDurationAction.NAME), !running, duration.value == TGDuration.WHOLE)
        initializeItem(menu, R.id.action_set_duration_half, createActionProcessor(TGSetHalfDurationAction.NAME), !running, duration.value == TGDuration.HALF)
        initializeItem(menu, R.id.action_set_duration_quarter, createActionProcessor(TGSetQuarterDurationAction.NAME), !running, duration.value == TGDuration.QUARTER)
        initializeItem(menu, R.id.action_set_duration_eighth, createActionProcessor(TGSetEighthDurationAction.NAME), !running, duration.value == TGDuration.EIGHTH)
        initializeItem(menu, R.id.action_set_duration_sixteenth, createActionProcessor(TGSetSixteenthDurationAction.NAME), !running, duration.value == TGDuration.SIXTEENTH)
        initializeItem(menu, R.id.action_set_duration_thirtysecond, createActionProcessor(TGSetThirtySecondDurationAction.NAME), !running, duration.value == TGDuration.THIRTY_SECOND)
        initializeItem(menu, R.id.action_set_duration_sixtyfourth, createActionProcessor(TGSetSixtyFourthDurationAction.NAME), !running, duration.value == TGDuration.SIXTY_FOURTH)
        initializeItem(menu, R.id.action_set_duration_dotted, createActionProcessor(TGChangeDottedDurationAction.NAME), !running, duration.isDotted)
        initializeItem(menu, R.id.action_set_duration_doubledotted, createActionProcessor(TGChangeDoubleDottedDurationAction.NAME), !running, duration.isDoubleDotted)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_1, TGDivisionType.NORMAL, duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_3, TGDivisionType.DIVISION_TYPES[1], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_5, TGDivisionType.DIVISION_TYPES[2], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_6, TGDivisionType.DIVISION_TYPES[3], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_7, TGDivisionType.DIVISION_TYPES[4], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_9, TGDivisionType.DIVISION_TYPES[5], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_10, TGDivisionType.DIVISION_TYPES[6], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_11, TGDivisionType.DIVISION_TYPES[7], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_12, TGDivisionType.DIVISION_TYPES[8], duration, running)
        initializeDivisionItem(menu, R.id.action_set_duration_division_type_13, TGDivisionType.DIVISION_TYPES[9], duration, running)
    }

    fun initializeDivisionItem(
        menu: Menu,
        id: Int,
        divisionType: TGDivisionType,
        duration: TGDuration,
        running: Boolean
    ) {
        initializeItem(
            menu,
            id,
            createDivisionTypeActionProcessor(divisionType),
            !running,
            divisionType.isEqual(duration.division)
        )
    }

    fun createDivisionTypeActionProcessor(divisionType: TGDivisionType): TGActionProcessorListener {
        val songManager: TGSongManager =
            TGDocumentManager.getInstance(findContext()).songManager
        return createActionProcessor(TGSetDivisionTypeDurationAction.NAME).apply {
            setAttribute(
                TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE,
                divisionType.clone(songManager.factory)
            )
        }
    }
}
