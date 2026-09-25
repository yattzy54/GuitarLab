package app.tuxguitar.android.view.dialog.browser.filesystem

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.tuxguitar.android.R
import java.io.File

class TGBrowserSettingsFolderAdapter(
    private val context: Context,
    private val mountPoint: TGBrowserSettingsMountPoint
) : BaseAdapter() {
    private var path: File? = null
    private val items = mutableListOf<TGBrowserSettingsFolderAdapterItem>()
    private var listener: TGBrowserSettingsFolderAdapterListener? = null

    init {
        updatePath(mountPoint.path)
    }

    fun getPath(): File? = path

    fun setListener(listener: TGBrowserSettingsFolderAdapterListener?) {
        this.listener = listener
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]
        val view = convertView
            ?: getLayoutInflater().inflate(R.layout.view_browser_element, parent, false)
        view.tag = item
        view.findViewById<TextView>(R.id.tg_browser_element_name).text = item.label

        findStyledFolderIcon()?.let { styledIcon ->
            view.findViewById<ImageView>(R.id.tg_browser_element_icon).setImageDrawable(styledIcon)
        }

        view.setOnClickListener { clickedView ->
            updatePath((clickedView.tag as TGBrowserSettingsFolderAdapterItem).file)
        }
        return view
    }

    fun findStyledFolderIcon(): Drawable? {
        val typedArray = context.obtainStyledAttributes(
            R.style.browserElementIconFolderStyle,
            intArrayOf(android.R.attr.src)
        )
        return typedArray.getDrawable(0).also { typedArray.recycle() }
    }

    fun updatePath(path: File?) {
        this.path = path
        items.clear()
        if (path != null && path.exists() && path.isDirectory) {
            path.parentFile
                ?.takeIf { path != mountPoint.path }
                ?.let { items.add(TGBrowserSettingsFolderAdapterItem("../", it)) }

            val directoryFiles = getDirectoryFiles(path)
            sortFiles(directoryFiles)
            directoryFiles.forEach { file ->
                items.add(TGBrowserSettingsFolderAdapterItem(file.name, file))
            }
        }
        notifyDataSetChanged()
        listener?.onPathChanged(path)
    }

    fun getDirectoryFiles(parent: File): MutableList<File> =
        parent.listFiles()?.filter { it.isDirectory }?.toMutableList() ?: mutableListOf()

    fun sortFiles(files: MutableList<File>) {
        files.sortBy { it.name }
    }
}
