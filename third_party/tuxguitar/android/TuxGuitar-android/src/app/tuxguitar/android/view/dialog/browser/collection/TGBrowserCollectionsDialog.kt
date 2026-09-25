package app.tuxguitar.android.view.dialog.browser.collection

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.R
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserFactory
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserCollectionsDialog : TGModalFragment(R.layout.view_browser_collections_dialog) {
    private var eventListener: TGBrowserCollectionsEventListener? = null
    private var actionHandler: TGBrowserCollectionsActionHandler? = null

    fun getChannel(): TGChannel? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(false, false, R.string.browser_collections_dlg_title)
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        actionHandler = TGBrowserCollectionsActionHandler(this)
        eventListener = TGBrowserCollectionsEventListener(this)
        fillFactories()
        fillAddButton()
        fillColletions()
    }

    override fun onShowView() {
        eventListener?.let { TGActionManager.getInstance(findContext()).addPostExecutionListener(it) }
    }

    override fun onHideView() {
        eventListener?.let { TGActionManager.getInstance(findContext()).removePostExecutionListener(it) }
    }

    fun createFactoryValues(): Array<TGSelectableItem> {
        val selectableItems = mutableListOf<TGSelectableItem>()
        val factories = TGBrowserManager.getInstance(findContext()).getFactories()
        while (factories.hasNext()) {
            val factory = factories.next()
            selectableItems.add(TGSelectableItem(factory, factory.getName()))
        }
        return selectableItems.toTypedArray()
    }

    fun fillFactories() {
        val arrayAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createFactoryValues()
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requireView().findViewById<Spinner>(R.id.browser_collections_dlg_add_type)
            .adapter = arrayAdapter
    }

    fun fillAddButton() {
        requireView().findViewById<ImageButton>(R.id.browser_collections_dlg_add_button)
            .setOnClickListener { createCollection(findSelectedFactory()) }
    }

    fun fillColletions() {
        requireView().findViewById<ListView>(R.id.browser_collections_dlg_list)
            .adapter = TGBrowserCollectionsAdapter(this, requireView().context)
        refreshListView()
    }

    fun findSelectedFactory(): TGBrowserFactory? {
        val selectedItem = requireView()
            .findViewById<Spinner>(R.id.browser_collections_dlg_add_type)
            .selectedItem as? TGSelectableItem
        return selectedItem?.getItem() as? TGBrowserFactory
    }

    fun createCollection(factory: TGBrowserFactory?) {
        factory?.createSettings(TGBrowserCollectionsSettingsHandler(this, factory.getType()))
    }

    fun addCollection(collection: TGBrowserCollection) {
        getActionHandler().createAddCollectionAction(collection).process()
    }

    fun removeCollection(collection: TGBrowserCollection) {
        getActionHandler().createRemoveCollectionAction(collection).process()
    }

    fun refreshListView() {
        val adapter = requireView().findViewById<ListView>(R.id.browser_collections_dlg_list)
            .adapter as TGBrowserCollectionsAdapter
        adapter.clearCollections()

        val collections = TGBrowserManager.getInstance(findContext()).getCollections()
        while (collections.hasNext()) {
            adapter.addCollection(collections.next())
        }
        adapter.notifyDataSetChanged()
    }

    fun getActionHandler(): TGBrowserCollectionsActionHandler =
        requireNotNull(actionHandler) { "Browser collections dialog has not been initialized" }
}
