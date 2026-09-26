package app.tuxguitar.android.menu.controller.impl.fragment

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGChannelListMenu private constructor(private val context: TGContext) : TGMenuController {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_channel_list, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        menu.findItem(R.id.action_channel_list_add)!!
            .setOnMenuItemClickListener(createActionProcessor(TGAddNewChannelAction.NAME))
    }

    fun createActionProcessor(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(context, actionId)

    fun getContext(): TGContext = context

    fun getActivity(): TGActivity = TGActivityController.getInstance(context).activity!!

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGChannelListMenu =
            TGSingletonUtil.getInstance(
                context,
                TGChannelListMenu::class.java.name,
                object : TGSingletonFactory<TGChannelListMenu> {
                    override fun createInstance(context: TGContext) = TGChannelListMenu(context)
                }
            )
    }
}
