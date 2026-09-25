package app.tuxguitar.android.drawer.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.ComposeView
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.util.TGContext

class TGMainDrawer(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val actionHandler = TGMainDrawerActionHandler(this)
    private val selectedTab = mutableIntStateOf(R.id.main_drawer_file_tab)
    private val fileActions = listOf(
        TGMainDrawerFileAction(R.string.action_file_new, actionHandler.createNewFileAction()),
        TGMainDrawerFileAction(R.string.action_file_open, actionHandler.createOpenFileAction()),
        TGMainDrawerFileAction(R.string.action_file_save, actionHandler.createSaveFileAction()),
        TGMainDrawerFileAction(R.string.action_file_save_as, actionHandler.createSaveFileAsAction()),
    )
    private val trackListState = TGMainDrawerTrackListState(this)

    fun findContext(): TGContext = TGApplicationUtil.findContext(this)

    fun findActivity(): TGActivity = TGActivity.requireCurrent()

    override fun onFinishInflate() {
        super.onFinishInflate()
        selectedTab.intValue = findContext().getAttribute(ATTRIBUTE_SELECTED_TAB) as? Int
            ?: R.id.main_drawer_file_tab
        createComposeContent()
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

    private fun createComposeContent() {
        findViewById<FrameLayout>(R.id.main_drawer_compose_container).addView(
            ComposeView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
                setContent {
                    MaterialTheme {
                        TGMainDrawerContent(
                            selectedTabId = selectedTab.intValue,
                            onTabSelected = ::selectTab,
                            fileActions = fileActions,
                            trackItems = trackListState.items,
                            onFileActionClick = { action -> dispatchAction(action.processor) },
                            onTrackClick = { item -> dispatchAction(actionHandler.createGoToTrackAction(item.track)) },
                            onTrackLongClick = {
                                dispatchAction(actionHandler.createGoToTrackWithSmartMenuAction(it.track))
                            },
                            onAddTrackClick = { dispatchAction(actionHandler.createAddTrackAction()) },
                        )
                    }
                }
            },
        )
    }

    private fun selectTab(tabId: Int) {
        selectedTab.intValue = tabId
        findContext().setAttribute(ATTRIBUTE_SELECTED_TAB, tabId)
    }

    private fun dispatchAction(processor: app.tuxguitar.android.action.TGActionProcessorListener) {
        processor.processEvent(this, null)
    }

    fun addActionListeners() {
        findViewById<View>(R.id.main_drawer_transport_mixer)
            .setOnClickListener(actionHandler.createOpenInstrumentsAction())
        findViewById<View>(R.id.main_drawer_song_info)
            .setOnClickListener(actionHandler.createOpenInfoAction())
    }

    fun attachListeners() {
        trackListState.attachListeners()
    }

    fun detachListeners() {
        trackListState.detachListeners()
    }

    fun getActionHandler(): TGMainDrawerActionHandler = actionHandler

    companion object {
        private val ATTRIBUTE_SELECTED_TAB = TGMainDrawer::class.java.name + "-selectedTab"
    }
}
