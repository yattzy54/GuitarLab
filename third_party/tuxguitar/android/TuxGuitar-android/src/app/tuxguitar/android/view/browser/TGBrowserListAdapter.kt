package app.tuxguitar.android.view.browser

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserListAdapter(private val context: Context) : BaseAdapter() {

    private val elements = mutableListOf<TGBrowserElement>()

    fun clearElements() {
        this.elements.clear()
    }

    fun fillElements(elements: List<TGBrowserElement>) {
        this.clearElements()
        this.elements.addAll(elements)
    }

    override fun getCount(): Int = this.elements.size

    override fun getItem(position: Int): Any = this.elements[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element = this.elements[position]
        val view = convertView ?: this.getLayoutInflater().inflate(R.layout.view_browser_element, parent, false)
        view.tag = element

        try {
            val textView = view.findViewById<TextView>(R.id.tg_browser_element_name)
            textView.text = element.getName()

            val elementIcon = this.findElementIcon(element)
            if (elementIcon != null) {
                val imageView = view.findViewById<ImageView>(R.id.tg_browser_element_icon)
                imageView.setImageDrawable(elementIcon)
            }
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(TGApplicationUtil.findContext(this.context)).handleError(e)
        }
        return view
    }

    @Throws(TGBrowserException::class)
    fun findElementIcon(element: TGBrowserElement): Drawable? {
        val style = if (element.isFolder()) R.style.browserElementIconFolderStyle else R.style.browserElementIconFileStyle
        val typedArray: TypedArray? = this.context.obtainStyledAttributes(style, intArrayOf(android.R.attr.src))
        return typedArray?.getDrawable(0)
    }
}
