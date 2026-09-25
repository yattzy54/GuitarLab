package app.tuxguitar.android.view.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TGSelectableAdapter(
    context: Context,
    resource: Int,
    items: List<TGSelectableItem>,
) : ArrayAdapter<TGSelectableItem>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        if (item != null) {
            updateTextView(view, item.getLabel())
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val item = getItem(position)
        if (item != null) {
            updateTextView(view, item.getDropDownLabel())
        }
        return view
    }

    fun updateTextView(view: View, text: String?) {
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView?.text = text
    }
}
