package app.tuxguitar.android.view.browser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Spinner
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionAdapterManager
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.browser.TGBrowserEmptyCallBack
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.assets.TGAssetBrowserFactory
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.view.util.TGSelectableAdapter
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.io.base.TGFileFormatManager
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.TGErrorManager
import java.util.ArrayList
import java.util.Iterator

class TGBrowserView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    val actionHandler: TGBrowserActionHandler = TGBrowserActionHandler(this)
    private val eventListener: TGBrowserEventListener = TGBrowserEventListener(this)
    private val destroyListener: TGBrowserDestroyListener = TGBrowserDestroyListener(this)

    override fun onFinishInflate() {
        try {
            super.onFinishInflate()
            this.fillFormats()
            this.fillListView()
            this.addListeners()
            this.addBrowserDefaults()
            this.refresh(true)
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(findContext()).handleError(e)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.post { this.updateSavePanel() }
    }

    @Throws(TGBrowserException::class)
    fun onDestroy() {
        val browserManager = TGBrowserManager.getInstance(this.findContext())
        val session = browserManager.session
        val browser = session.browser
        if (browser != null) {
            browser.close(TGBrowserEmptyCallBack())
        }
        browserManager.closeSession()
    }

    fun createCollectionValues(): Array<TGSelectableItem> {
        val selectableItems = ArrayList<TGSelectableItem>()

        val collections: Iterator<TGBrowserCollection> = TGBrowserManager.getInstance(this.findContext()).getCollections()
        while (collections.hasNext()) {
            val collection = collections.next()
            selectableItems.add(TGSelectableItem(collection, collection.settings.title))
        }

        if (selectableItems.isEmpty()) {
            selectableItems.add(TGSelectableItem(null, findActivity().getString(R.string.global_spinner_select_option)))
        }

        return selectableItems.toTypedArray()
    }

    fun refreshCollections(forceDefaults: Boolean) {
        val arrayAdapter = ArrayAdapter<TGSelectableItem>(findActivity(), android.R.layout.simple_spinner_item, createCollectionValues())
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val selectedCollection = if (forceDefaults) TGBrowserManager.getInstance(this.findContext()).getDefaultCollection() else this.findCurrentCollection()
        val selectedItem = TGSelectableItem(selectedCollection, null)
        val selectedItemPosition = arrayAdapter.getPosition(selectedItem)

        val spinner = this.findViewById<Spinner>(R.id.browser_collections)
        val listener = spinner.onItemSelectedListener
        spinner.onItemSelectedListener = null
        if (!this.isSameCollection(arrayAdapter, spinner.adapter as ArrayAdapter<TGSelectableItem>)) {
            spinner.adapter = arrayAdapter
        }
        spinner.onItemSelectedListener = listener
        if (spinner.selectedItemPosition != selectedItemPosition) {
            spinner.setSelection(selectedItemPosition, false)
        }
    }

    fun findSelectedCollection(): TGBrowserCollection? {
        val spinner = this.findViewById<Spinner>(R.id.browser_collections)
        val selectableItem = spinner.selectedItem as? TGSelectableItem
        return selectableItem?.getItem() as? TGBrowserCollection
    }

    fun findCurrentCollection(): TGBrowserCollection? {
        val session = TGBrowserManager.getInstance(this.findContext()).session
        return session.collection
    }

    fun createFormatLabel(format: TGFileFormat): String = createExtension(format, format.name)

    fun createFormatDropDownLabel(format: TGFileFormat): String = format.name

    fun createFormatExportDropDownLabel(format: TGFileFormat): String = findActivity().getString(R.string.storage_export_to, format.name)

    fun createFormatValues(): List<TGSelectableItem> {
        val selectableItems = ArrayList<TGSelectableItem>()
        val fileFormatManager = TGFileFormatManager.getInstance(findContext())

        val commonFormats = fileFormatManager.findWriteFileFormats(true)
        for (format in commonFormats) {
            selectableItems.add(TGSelectableItem(format, createFormatLabel(format), createFormatDropDownLabel(format)))
        }

        val nonCommonFormats = fileFormatManager.findWriteFileFormats(false)
        for (format in nonCommonFormats) {
            selectableItems.add(TGSelectableItem(format, createFormatLabel(format), createFormatExportDropDownLabel(format)))
        }

        return selectableItems
    }

    fun fillListView() {
        val listView = this.findViewById<ListView>(R.id.browser_elements)
        listView.adapter = TGBrowserListAdapter(context)
        listView.onItemClickListener = TGBrowserItemListener(this)
    }

    fun fillFormats() {
        val selectableAdapter = TGSelectableAdapter(findActivity(), android.R.layout.simple_spinner_item, createFormatValues())
        selectableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = this.findViewById<Spinner>(R.id.browser_save_format)
        spinner.adapter = selectableAdapter
    }

    fun findSelectedFormat(): TGFileFormat? {
        val spinner = this.findViewById<Spinner>(R.id.browser_save_format)
        val selectableItem = spinner.selectedItem as? TGSelectableItem
        return selectableItem?.getItem() as? TGFileFormat
    }

    @Throws(TGBrowserException::class)
    fun addBrowserDefaults() {
        val context: TGContext = this.findContext()
        val browserManager = TGBrowserManager.getInstance(context)
        val assetBrowserFactory = TGAssetBrowserFactory(context)
        browserManager.addFactory(assetBrowserFactory)
        browserManager.restoreCollections()

        if (!browserManager.hasStoredCollections()) {
            browserManager.addCollection(assetBrowserFactory.createDemoCollection())
        }
    }

    fun requestRefresh() {
        this.actionHandler.createBrowserAction(TGBrowserRefreshAction.NAME).process()
    }

    fun updateSavePanel() {
        val session = TGBrowserManager.getInstance(this.findContext()).session

        val view = findViewById<View>(R.id.browser_save_panel)
        view.visibility = if (session.sessionType == TGBrowserSession.WRITE_MODE) View.VISIBLE else View.GONE

        val editText = this.findViewById<EditText>(R.id.browser_save_element_name)
        editText.setText(findActivity().getString(R.string.storage_default_filename))
    }

    fun addListeners() {
        this.findViewById<View>(R.id.browser_save_button).setOnClickListener(createSaveButtonListener())
        (this.findViewById<View>(R.id.browser_collections) as Spinner).onItemSelectedListener = createCollectionsSpinnerListener()

        TGActionManager.getInstance(this.findContext()).addPostExecutionListener(this.eventListener)
        TGActionManager.getInstance(this.findContext()).addErrorListener(this.eventListener)
        TGActionAdapterManager.getInstance(this.findContext()).addAsyncProcessFinishListener(this.eventListener)
        TGActionAdapterManager.getInstance(this.findContext()).addAsyncProcessErrorListener(this.eventListener)
        TGEditorManager.getInstance(this.findContext()).addDestroyListener(this.destroyListener)
    }

    @Throws(TGBrowserException::class)
    fun updateItems() {
        val session = TGBrowserManager.getInstance(this.findContext()).session
        val browser = session.browser
        val writable = browser != null && browser.isWritable()

        this.findViewById<View>(R.id.browser_save_element_name).isEnabled = writable
        this.findViewById<View>(R.id.browser_save_format).isEnabled = writable
        this.findViewById<View>(R.id.browser_save_button).isEnabled = writable
    }

    fun createCollectionsSpinnerListener(): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                processSelectedCollection()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                processSelectedCollection()
            }
        }
    }

    fun createSaveButtonListener(): View.OnClickListener {
        return View.OnClickListener { processSaveButton() }
    }

    fun processOpenCloseSession(collection: TGBrowserCollection?) {
        if (collection != null) {
            this.actionHandler.createOpenSessionAction(collection).process()
        } else {
            this.actionHandler.createCloseSessionAction().process()
        }
    }

    fun processSelectedCollection() {
        val session = TGBrowserManager.getInstance(this.findContext()).session
        val currentCollection = session.collection
        val selectedCollection = findSelectedCollection()

        if (!this.isSameObject(selectedCollection, currentCollection)) {
            this.processOpenCloseSession(selectedCollection)
        }
    }

    fun processSaveButton() {
        try {
            val session = TGBrowserManager.getInstance(this.findContext()).session
            if (session.browser != null) {
                val format = findSelectedFormat() ?: return

                val editText = this.findViewById<EditText>(R.id.browser_save_element_name)
                val elementName = editText.text.toString() + createExtension(format, TGFileFormatUtils.DEFAULT_EXTENSION)

                val element = findElement(elementName)
                if (element != null) {
                    if (element.isWritable()) {
                        val actionProcessor = this.actionHandler.createBrowserSaveElementAction(element, format)
                        this.actionHandler.processConfirmableAction(actionProcessor, this.findActivity().getString(R.string.browser_file_overwrite_question))
                    } else {
                        throw TGBrowserException(this.findActivity().getString(R.string.browser_file_overwrite_readonly_error))
                    }
                } else {
                    if (session.browser != null && session.browser!!.isWritable()) {
                        this.actionHandler.createBrowserSaveNewElementAction(elementName, format).process()
                    }
                }
            }
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(findContext()).handleError(e)
        }
    }

    fun createExtension(format: TGFileFormat, defaultValue: String): String {
        val supportedFormats = format.supportedFormats
        if (supportedFormats != null && supportedFormats.isNotEmpty()) {
            return this.createExtension(supportedFormats[0])
        }
        return defaultValue
    }

    fun createExtension(supportedFormat: String): String = "." + supportedFormat

    @Throws(TGBrowserException::class)
    fun findElement(name: String): TGBrowserElement? {
        val tgBrowserSession = TGBrowserManager.getInstance(this.findContext()).session
        if (tgBrowserSession.currentElements != null) {
            for (tgBrowserElement in tgBrowserSession.currentElements!!) {
                if (tgBrowserElement.getName() == name) {
                    return tgBrowserElement
                }
            }
        }
        return null
    }

    fun refreshListView() {
        val listView = this.findViewById<ListView>(R.id.browser_elements)

        val tgBrowserElementAdapter = listView.adapter as TGBrowserListAdapter
        val tgBrowserSession = TGBrowserManager.getInstance(this.findContext()).session
        if (tgBrowserSession.currentElements == null) {
            tgBrowserElementAdapter.clearElements()
        } else {
            tgBrowserElementAdapter.fillElements(tgBrowserSession.currentElements!!)
        }

        tgBrowserElementAdapter.notifyDataSetChanged()
    }

    @Throws(TGBrowserException::class)
    fun refresh() {
        this.refresh(false)
    }

    @Throws(TGBrowserException::class)
    fun refresh(forceDefaults: Boolean) {
        this.refreshListView()
        this.refreshCollections(forceDefaults)
        this.updateItems()
    }

    fun isSameCollection(c1: ArrayAdapter<TGSelectableItem>?, c2: ArrayAdapter<TGSelectableItem>?): Boolean {
        if (c1 === c2) {
            return true
        }
        if (c1 != null && c2 != null && c1.count == c2.count) {
            val count = c1.count
            for (i in 0 until count) {
                if (!this.isSameObject(c1.getItem(i), c2.getItem(i))) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun isSameObject(c1: Any?, c2: Any?): Boolean = c1 === c2 || (c1 != null && c2 != null && c1 == c2)

    fun findContext(): TGContext = TGApplicationUtil.findContext(this)

    fun findActivity(): TGActivity = context as TGActivity
}
