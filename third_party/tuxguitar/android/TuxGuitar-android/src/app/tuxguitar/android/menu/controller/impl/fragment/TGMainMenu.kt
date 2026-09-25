package app.tuxguitar.android.menu.controller.impl.fragment

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.fragment.impl.TGPreferencesFragmentController
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.menu.controller.impl.contextual.*
import app.tuxguitar.android.menu.util.TGToggleStyledIconHandler
import app.tuxguitar.android.menu.util.TGToggleStyledIconHelper
import app.tuxguitar.android.view.dialog.tempo.TGTempoDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.models.TGTempo
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGMainMenu private constructor(private val context: TGContext) : TGMenuController {
    private val styledIconHelper = TGToggleStyledIconHelper(context)
    private var tempoDisplayItem: TextView? = null

    init {
        fillStyledIconHandlers()
    }

    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        initializeItems(menu)
        styledIconHelper.initialize(getActivity(), menu)
    }

    fun initializeItems(menu: Menu) {
        val activity = getActivity()
        val readerMode = activity.javaClass.name == "app.tuxguitar.android.activity.TGReaderActivity"
        if (readerMode) {
            intArrayOf(
                R.id.action_tab_keyboard_toggle,
                R.id.action_menu_edit,
                R.id.action_menu_composition,
                R.id.action_menu_track,
                R.id.action_menu_measure,
                R.id.action_menu_beat,
                R.id.action_menu_duration,
                R.id.action_menu_dynamic,
                R.id.action_menu_effects
            ).forEach { menu.findItem(it)!!.isVisible = false }
        }
        menu.findItem(R.id.action_tab_keyboard_toggle)!!
            .setOnMenuItemClickListener(createActionProcessor(TGToggleTabKeyboardAction.NAME))
        menu.findItem(R.id.action_transport_play)!!
            .setOnMenuItemClickListener(createActionProcessor(TGTransportPlayAction.NAME))
        menu.findItem(R.id.action_menu_edit)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGEditMenu(activity)))
        menu.findItem(R.id.action_menu_view)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGViewMenu(activity)))
        menu.findItem(R.id.action_menu_composition)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGCompositionMenu(activity)))
        menu.findItem(R.id.action_menu_track)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGTrackMenu(activity)))
        menu.findItem(R.id.action_menu_measure)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGMeasureMenu(activity)))
        menu.findItem(R.id.action_menu_beat)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGBeatMenu(activity)))
        menu.findItem(R.id.action_menu_duration)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGDurationMenu(activity)))
        menu.findItem(R.id.action_menu_dynamic)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGVelocityMenu(activity)))
        menu.findItem(R.id.action_menu_effects)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGEffectMenu(activity)))
        menu.findItem(R.id.action_menu_transport)!!
            .setOnMenuItemClickListener(createContextMenuActionProcessor(TGTransportMenu(activity)))
        menu.findItem(R.id.action_menu_settings)!!
            .setOnMenuItemClickListener(createFragmentActionProcessor(TGPreferencesFragmentController()))
        menu.findItem(R.id.action_songsterr_import)!!
            .setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {
                activity.startActivity(
                    Intent().setClassName(activity, "com.mmt.guitarlab.ui.tab.SongsterrImportActivity")
                )
                true
            })

        tempoDisplayItem = menu.findItem(R.id.action_tempo_display)!!
            .actionView!!.findViewById(R.id.tempo_display_item)
        tempoDisplayItem?.setOnClickListener {
            if (MidiPlayer.getInstance(context).isRunning) {
                return@setOnClickListener
            }
            val processor = TGActionProcessorListener(context, TGOpenDialogAction.NAME)
            processor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, activity)
            processor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, TGTempoDialogController())
            processor.process()
        }
        updateTempoDisplay()
    }

    fun updateTempoDisplay() {
        val display = tempoDisplayItem ?: return
        val midiPlayer = MidiPlayer.getInstance(context)
        val currentTempo: TGTempo?
        var tempoPercent = 100
        if (midiPlayer.isRunning && midiPlayer.currentTempo != null) {
            currentTempo = midiPlayer.currentTempo
            tempoPercent = midiPlayer.mode.currentPercent
        } else {
            currentTempo = TGSongViewController.getInstance(context).caret.measure.tempo
        }
        val tempo = currentTempo ?: return
        var iconName = "duration_${tempo.base}"
        if (tempo.isDotted) iconName += "dotted"
        val activity = getActivity()
        val iconId = activity.resources.getIdentifier(iconName, "drawable", activity.packageName)
        display.setCompoundDrawablesWithIntrinsicBounds(
            activity.resources.getDrawable(iconId),
            null,
            null,
            null
        )
        display.text = "= ${tempo.rawValue * tempoPercent / 100} "
    }

    fun fillStyledIconHandlers() {
        styledIconHelper.addHandler(createStyledIconTransportHandler())
    }

    fun createStyledIconTransportHandler(): TGToggleStyledIconHandler =
        object : TGToggleStyledIconHandler {
            override fun getMenuItemId(): Int = R.id.action_transport_play
            override fun resolveStyle(): Int =
                if (MidiPlayer.getInstance(context).isRunning) {
                    R.style.TGImageButton_Stop
                } else {
                    R.style.TGImageButton_Play
                }
        }

    fun createActionProcessor(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(context, actionId)

    fun createFragmentActionProcessor(controller: TGFragmentController<*>): TGActionProcessorListener =
        TGActionProcessorListener(context, TGOpenFragmentAction.NAME).apply {
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller)
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, getActivity())
        }

    fun createContextMenuActionProcessor(controller: TGMenuController): TGActionProcessorListener =
        TGActionProcessorListener(context, TGOpenMenuAction.NAME).apply {
            setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
            setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, getActivity())
        }

    fun getContext(): TGContext = context

    fun getActivity(): TGActivity = TGActivityController.getInstance(context).activity!!

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGMainMenu =
            TGSingletonUtil.getInstance(
                context,
                TGMainMenu::class.java.name,
                object : TGSingletonFactory<TGMainMenu> {
                    override fun createInstance(context: TGContext) = TGMainMenu(context)
                }
            )
    }
}
