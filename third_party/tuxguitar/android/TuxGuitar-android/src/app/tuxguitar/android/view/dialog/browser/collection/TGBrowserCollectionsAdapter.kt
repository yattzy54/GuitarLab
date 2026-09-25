package app.tuxguitar.android.view.dialog.browser.collection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserCollectionsAdapter(
    private val dialog: TGBrowserCollectionsDialog,
    private val context: Context
) : BaseAdapter() {
    private val collections = mutableListOf<TGBrowserCollection>()

    fun clearCollections() {
        collections.clear()
    }

    fun addCollection(collection: TGBrowserCollection) {
        collections.add(collection)
    }

    override fun getCount(): Int = collections.size
    override fun getItem(position: Int): Any = collections[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val collection = collections[position]
        val view = convertView
            ?: getLayoutInflater().inflate(R.layout.view_browser_collections_item, parent, false)
        view.tag = collection
        view.findViewById<TextView>(R.id.browser_collections_item_name).text =
            collection.settings.title
        view.findViewById<ImageView>(R.id.browser_collections_item_icon)
            .setOnClickListener { dialog.removeCollection(collection) }
        return view
    }
}
