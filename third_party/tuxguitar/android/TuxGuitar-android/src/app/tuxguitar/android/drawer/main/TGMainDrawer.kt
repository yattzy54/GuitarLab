package app.tuxguitar.android.drawer.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.util.TGContext
import com.google.android.material.tabs.TabLayout

class TGMainDrawer(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val actionHandler = TGMainDrawerActionHandler(this)
    private val fileListAdapter = TGMainDrawerFileListAdapter(this)
    private val trackListAdapter = TGMainDrawerTrackListAdapter(this)

    fun findContext(): TGContext = TGApplicationUtil.findContext(this)

    fun findActivity(): TGActivity = context as TGActivity

    override fun onFinishInflate() {
        super.onFinishInflate()
        createTabs()
        fillFileListView()
        fillTrackListView()
        addActionListeners()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachListeners()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        detachListeners()
    }

    fun createTabs() {
        val selectedTab: Any? = findContext().getAttribute(ATTRIBUTE_SELECTED_TAB)
        val tabLayout = findViewById<TabLayout>(R.id.main_drawer_tabHost)
        createTabSelectionListener(tabLayout)
        createTab(
            tabLayout,
            R.id.main_drawer_file_tab,
            findActivity().getString(R.string.main_drawer_file),
            selectedTab
        )
        createTab(
            tabLayout,
            R.id.main_drawer_track_tab,
            findActivity().getString(R.string.main_drawer_tracks),
            selectedTab
        )
    }

    fun createTab(tabLayout: TabLayout, layoutId: Int, indicator: String, selectedTab: Any?) {
        val tab = tabLayout.newTab()
        tab.tag = layoutId
        tab.text = indicator
        tabLayout.addTab(tab)
        if (selectedTab == layoutId) {
            tab.select()
        }
    }

    fun createTabSelectionListener(tabLayout: TabLayout) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                onTabSelectionUpdate(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                onTabSelectionUpdate(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    fun onTabSelectionUpdate(tab: TabLayout.Tab, selected: Boolean) {
        val tag = tab.tag
        if (tag != null) {
            findViewById<View>(tag as Int).visibility =
                if (selected) View.VISIBLE else View.GONE
        }
        if (selected) {
            findContext().setAttribute(ATTRIBUTE_SELECTED_TAB, tag)
        }
    }

    fun addActionListeners() {
        findViewById<View>(R.id.main_drawer_transport_mixer)
            .setOnClickListener(actionHandler.createOpenInstrumentsAction())
        findViewById<View>(R.id.main_drawer_song_info)
            .setOnClickListener(actionHandler.createOpenInfoAction())
        findViewById<View>(R.id.main_drawer_track_add_button)
            .setOnClickListener(actionHandler.createAddTrackAction())
    }

    fun fillFileListView() {
        fillListView(R.id.main_drawer_file_items, fileListAdapter)
    }

    fun fillTrackListView() {
        fillListView(R.id.main_drawer_track_items, trackListAdapter)
    }

    fun fillListView(id: Int, adapter: TGMainDrawerListAdapter) {
        findViewById<ListView>(id).adapter = adapter
    }

    fun attachListeners() {
        fileListAdapter.attachListeners()
        trackListAdapter.attachListeners()
    }

    fun detachListeners() {
        fileListAdapter.detachListeners()
        trackListAdapter.detachListeners()
    }

    fun getActionHandler(): TGMainDrawerActionHandler = actionHandler

    companion object {
        private val ATTRIBUTE_SELECTED_TAB = TGMainDrawer::class.java.name + "-selectedTab"
    }
}
