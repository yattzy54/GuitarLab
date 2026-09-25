package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.tuxguitar.android.ui.state.editorViewModel
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionAdapterManager
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction
import app.tuxguitar.android.browser.TGBrowserEmptyCallBack
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.assets.TGAssetBrowserFactory
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.fragment.TGComposeCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGBrowserMenu
import app.tuxguitar.android.view.browser.TGBrowserActionHandler
import app.tuxguitar.android.view.browser.TGBrowserDestroyListener
import app.tuxguitar.android.view.browser.TGBrowserElementList
import app.tuxguitar.android.view.browser.TGBrowserEventListener
import app.tuxguitar.android.view.browser.TGBrowserItemListener
import app.tuxguitar.android.view.browser.TGBrowserScreen
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.io.base.TGFileFormatManager
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserFragment : TGComposeCachedFragment() {
    val actionHandler = TGBrowserActionHandler(this)
    private val eventListener = TGBrowserEventListener(this)
    private val destroyListener = TGBrowserDestroyListener(this)
    private val itemListener = TGBrowserItemListener(this)

    private val viewModel by lazy { editorViewModel { TGBrowserViewModel() } }
    private var listenersRegistered = false

    override fun onPostCreate() {
        attachInstance()
        createActionBar(true, false, null)
        initializeBrowser()
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGBrowserMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    override fun onShowView() {
        registerListeners()
        updateSavePanel(resetName = true)
        refreshSafely(forceDefaults = false)
    }

    override fun onHideView() {
        unregisterListeners()
    }

    fun attachInstance() {
        TGBrowserFragmentController.getInstance(findContext()).attachInstance(this)
    }

    fun initializeBrowser() {
        try {
            fillFormats()
            addBrowserDefaults()
            refresh(true)
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(findContext()).handleError(e)
        }
    }

    fun registerListeners() {
        if (listenersRegistered) {
            return
        }
        TGActionManager.getInstance(findContext()).addPostExecutionListener(eventListener)
        TGActionManager.getInstance(findContext()).addErrorListener(eventListener)
        TGActionAdapterManager.getInstance(findContext()).addAsyncProcessFinishListener(eventListener)
        TGActionAdapterManager.getInstance(findContext()).addAsyncProcessErrorListener(eventListener)
        TGEditorManager.getInstance(findContext()).addDestroyListener(destroyListener)
        listenersRegistered = true
    }

    fun unregisterListeners() {
        if (!listenersRegistered) {
            return
        }
        TGActionManager.getInstance(findContext()).removePostExecutionListener(eventListener)
        TGActionManager.getInstance(findContext()).removeErrorListener(eventListener)
        TGActionAdapterManager.getInstance(findContext()).removeAsyncProcessEndListener(eventListener)
        TGActionAdapterManager.getInstance(findContext()).removeAsyncProcessErrorListener(eventListener)
        TGEditorManager.getInstance(findContext()).removeDestroyListener(destroyListener)
        listenersRegistered = false
    }

    @Throws(TGBrowserException::class)
    fun onDestroyBrowser() {
        val browserManager = TGBrowserManager.getInstance(findContext())
        val session = browserManager.session
        val browser = session.browser
        if (browser != null) {
            browser.close(TGBrowserEmptyCallBack())
        }
        browserManager.closeSession()
    }

    fun createCollectionValues(): List<TGSelectableItem> {
        val selectableItems = mutableListOf<TGSelectableItem>()
        val collections = TGBrowserManager.getInstance(findContext()).getCollections()
        while (collections.hasNext()) {
            val collection = collections.next()
            selectableItems.add(TGSelectableItem(collection, collection.settings.title))
        }
        if (selectableItems.isEmpty()) {
            selectableItems.add(TGSelectableItem(null, findActivity().getString(R.string.global_spinner_select_option)))
        }
        return selectableItems
    }

    fun refreshCollections(forceDefaults: Boolean) {
        val selectedCollectionValue = if (forceDefaults) {
            TGBrowserManager.getInstance(findContext()).getDefaultCollection()
        } else {
            findCurrentCollection()
        }

        val newOptions = createCollectionValues()
        viewModel.updateCollections(newOptions, selectedCollectionValue)
    }

    fun findSelectedCollection(): TGBrowserCollection? = viewModel.state.value.selectedCollection?.getItem() as? TGBrowserCollection

    fun findCurrentCollection(): TGBrowserCollection? = TGBrowserManager.getInstance(findContext()).session.collection

    fun createFormatLabel(format: TGFileFormat): String = createExtension(format, format.name)

    fun createFormatDropDownLabel(format: TGFileFormat): String = format.name

    fun createFormatExportDropDownLabel(format: TGFileFormat): String =
        findActivity().getString(R.string.storage_export_to, format.name)

    fun createFormatValues(): List<TGSelectableItem> {
        val selectableItems = mutableListOf<TGSelectableItem>()
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

    fun fillFormats() {
        val newOptions = createFormatValues()
        viewModel.updateFormats(newOptions)
    }

    fun findSelectedFormat(): TGFileFormat? = viewModel.state.value.selectedFormat?.getItem() as? TGFileFormat

    @Throws(TGBrowserException::class)
    fun addBrowserDefaults() {
        val context = findContext()
        val browserManager = TGBrowserManager.getInstance(context)
        val assetBrowserFactory = TGAssetBrowserFactory(context)
        browserManager.addFactory(assetBrowserFactory)
        browserManager.restoreCollections()

        if (!browserManager.hasStoredCollections()) {
            browserManager.addCollection(assetBrowserFactory.createDemoCollection())
        }
    }

    fun requestRefresh() {
        actionHandler.createBrowserAction(TGBrowserRefreshAction.NAME).process()
    }

    fun updateSavePanel(resetName: Boolean) {
        val session = TGBrowserManager.getInstance(findContext()).session
        viewModel.updateSavePanel(
            visible = session.sessionType == TGBrowserSession.WRITE_MODE,
            defaultName = if (resetName) findActivity().getString(R.string.storage_default_filename) else null,
        )
    }

    @Throws(TGBrowserException::class)
    fun updateItems() {
        val session = TGBrowserManager.getInstance(findContext()).session
        val browser = session.browser
        viewModel.updateSavePanel(
            visible = session.sessionType == TGBrowserSession.WRITE_MODE,
            writable = browser != null && browser.isWritable(),
        )
    }

    fun processOpenCloseSession(collection: TGBrowserCollection?) {
        if (collection != null) {
            actionHandler.createOpenSessionAction(collection).process()
        } else {
            actionHandler.createCloseSessionAction().process()
        }
    }

    fun processSelectedCollection(item: TGSelectableItem) {
        viewModel.selectCollection(item)
        val session = TGBrowserManager.getInstance(findContext()).session
        val currentCollection = session.collection
        val selectedCollectionValue = findSelectedCollection()
        if (!isSameObject(selectedCollectionValue, currentCollection)) {
            processOpenCloseSession(selectedCollectionValue)
        }
    }

    fun onSaveElementNameChange(value: String) {
        viewModel.setSaveName(value)
    }

    fun onFormatSelected(item: TGSelectableItem) {
        viewModel.selectFormat(item)
    }

    fun processSaveButton() {
        try {
            val session = TGBrowserManager.getInstance(findContext()).session
            if (session.browser != null) {
                val format = findSelectedFormat() ?: return
                val elementName = viewModel.state.value.saveElementName + createExtension(format, TGFileFormatUtils.DEFAULT_EXTENSION)
                val element = findElement(elementName)
                if (element != null) {
                    if (element.isWritable()) {
                        val actionProcessor = actionHandler.createBrowserSaveElementAction(element, format)
                        actionHandler.processConfirmableAction(
                            actionProcessor,
                            findActivity().getString(R.string.browser_file_overwrite_question),
                        )
                    } else {
                        throw TGBrowserException(findActivity().getString(R.string.browser_file_overwrite_readonly_error))
                    }
                } else if (session.browser != null && session.browser!!.isWritable()) {
                    actionHandler.createBrowserSaveNewElementAction(elementName, format).process()
                }
            }
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(findContext()).handleError(e)
        }
    }

    fun createExtension(format: TGFileFormat, defaultValue: String): String {
        val supportedFormats = format.supportedFormats
        if (supportedFormats != null && supportedFormats.isNotEmpty()) {
            return createExtension(supportedFormats[0])
        }
        return defaultValue
    }

    fun createExtension(supportedFormat: String): String = "." + supportedFormat

    @Throws(TGBrowserException::class)
    fun findElement(name: String): TGBrowserElement? {
        val browserSession = TGBrowserManager.getInstance(findContext()).session
        val currentElements = browserSession.currentElements ?: return null
        for (browserElement in currentElements) {
            if (browserElement.getName() == name) {
                return browserElement
            }
        }
        return null
    }

    fun refreshListView() {
        val browserSession = TGBrowserManager.getInstance(findContext()).session
        viewModel.updateElements(browserSession.currentElements.orEmpty())
    }

    @Throws(TGBrowserException::class)
    fun refresh() {
        refresh(false)
    }

    @Throws(TGBrowserException::class)
    fun refresh(forceDefaults: Boolean) {
        refreshListView()
        refreshCollections(forceDefaults)
        updateItems()
    }

    fun refreshSafely(forceDefaults: Boolean) {
        try {
            refresh(forceDefaults)
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(findContext()).handleError(e)
        }
    }

    fun isSameObject(c1: Any?, c2: Any?): Boolean = c1 === c2 || (c1 != null && c2 != null && c1 == c2)

    @Composable
    override fun FragmentContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        TGBrowserScreen(
            collections = state.collectionOptions,
            selectedCollection = state.selectedCollection,
            onCollectionSelected = ::processSelectedCollection,
            elementsContent = {
                TGBrowserElementList(
                    elements = state.elements,
                    onElementClick = itemListener::processElementAction,
                )
            },
            showSavePanel = state.showSavePanel,
            saveElementName = state.saveElementName,
            onSaveElementNameChange = ::onSaveElementNameChange,
            formatOptions = state.formatOptions,
            selectedFormat = state.selectedFormat,
            onFormatSelected = ::onFormatSelected,
            onSaveClick = ::processSaveButton,
            saveControlsEnabled = state.saveControlsEnabled,
        )
    }
}
