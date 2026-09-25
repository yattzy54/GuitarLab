package app.tuxguitar.android.drawer.main

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.tuxguitar.android.R

class TGMainDrawerFileListAdapter(mainDrawer: TGMainDrawer) : TGMainDrawerListAdapter(mainDrawer) {
    private val actions = mutableListOf<TGMainDrawerFileAction>()

    init {
        createActions()
    }

    fun createActions() {
        actions.clear()
        actions.add(
            TGMainDrawerFileAction(
                R.string.action_file_new,
                getMainDrawer().getActionHandler().createNewFileAction()
            )
        )
        actions.add(
            TGMainDrawerFileAction(
                R.string.action_file_open,
                getMainDrawer().getActionHandler().createOpenFileAction()
            )
        )
        actions.add(
            TGMainDrawerFileAction(
                R.string.action_file_save,
                getMainDrawer().getActionHandler().createSaveFileAction()
            )
        )
        actions.add(
            TGMainDrawerFileAction(
                R.string.action_file_save_as,
                getMainDrawer().getActionHandler().createSaveFileAsAction()
            )
        )
    }

    override fun getCount(): Int = actions.size

    override fun getItem(position: Int): Any = actions[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val action = actions[position]
        val view = convertView
            ?: getLayoutInflater().inflate(R.layout.view_main_drawer_text_item, parent, false)
        view.setOnClickListener(action.getProcessor())
        view.findViewById<TextView>(R.id.main_drawer_text_item).setText(action.getLabel())
        return view
    }
}
