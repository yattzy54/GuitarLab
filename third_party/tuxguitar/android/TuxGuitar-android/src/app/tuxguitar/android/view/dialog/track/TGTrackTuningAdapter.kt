package app.tuxguitar.android.view.dialog.track

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TGTrackTuningAdapter(
    private val dialog: TGTrackTuningDialog,
    private val context: Context
) : BaseAdapter() {
    override fun getCount(): Int = dialog.tuning.size
    override fun getItem(position: Int): Any = dialog.tuning[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val tuning = dialog.tuning[position]
        val view = convertView
            ?: getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, parent, false)
        view.tag = tuning
        view.findViewById<TextView>(android.R.id.text1).apply {
            text = tuning.getName()
            setOnLongClickListener(dialog.actionHandler.createTuningModelMenuAction(tuning, view))
        }
        return view
    }
}
