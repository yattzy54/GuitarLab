package app.tuxguitar.android.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.ContextMenu
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import app.tuxguitar.android.R
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionAdapterManager
import app.tuxguitar.android.action.impl.caret.*
import app.tuxguitar.android.action.impl.gui.*
import app.tuxguitar.android.action.impl.intent.TGProcessIntentAction
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction
import app.tuxguitar.android.drawer.TGDrawerManager
import app.tuxguitar.android.error.TGErrorHandlerImpl
import app.tuxguitar.android.fragment.impl.TGMainFragmentController
import app.tuxguitar.android.menu.controller.TGMenuContextualInflater
import app.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu
import app.tuxguitar.android.navigation.TGNavigationManager
import app.tuxguitar.android.properties.TGPropertiesAdapter
import app.tuxguitar.android.resource.TGResourceLoaderImpl
import app.tuxguitar.android.synchronizer.TGSynchronizerControllerImpl
import app.tuxguitar.android.transport.TGTransportAdapter
import app.tuxguitar.android.variables.TGVarAdapter
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.duration.*
import app.tuxguitar.editor.action.effect.*
import app.tuxguitar.editor.action.file.TGLoadTemplateAction
import app.tuxguitar.editor.action.note.*
import app.tuxguitar.resource.TGResourceBundle
import app.tuxguitar.resource.TGResourceManager
import app.tuxguitar.song.helpers.tuning.TuningManager
import app.tuxguitar.thread.TGMultiThreadHandler
import app.tuxguitar.thread.TGThreadManager
import app.tuxguitar.util.*
import app.tuxguitar.util.error.TGErrorManager
import app.tuxguitar.util.plugin.TGPluginManager
import java.util.Locale

/**
 * Legacy Android engine adapter, owned by LegacyEditorRepository.
 * EditorViewModel owns the observable presentation state; LegacyEditorHost
 * supplies the composition that renders existing View/Canvas and dialog code.
 * This class is not an Android Activity and must not escape the adapter boundary.
 */
open class TGActivity {
    open val isReadOnly: Boolean = false
    private var destroyed = false
    private var context: TGContext? = null
    private val navigationManager = TGNavigationManager(this)
    private val drawerManager = TGDrawerManager(this)
    private val actionBar = TGActivityActionBarController(this)
    private val resultManager = TGActivityResultManager()
    private val permissionResultManager = TGActivityPermissionResultManager()
    private var maintainDisplayON = false
    private var pendingIntent: Intent? = null
    private var modulesInitialized = false
    private var editorReleased = false

    private lateinit var hostActivity: FragmentActivity
    private lateinit var themedContext: Context
    private var rootView: View? = null
    private var composeContent: (@Composable () -> Unit)? = null
    private var parentComposition: CompositionContext? = null

    var currentDialog: TGComposeDialog? = null
        private set
    var onUiStateChanged: (() -> Unit)? = null

    fun notifyUiStateChanged() {
        onUiStateChanged?.invoke()
    }

    val intent: Intent?
        get() = pendingIntent

    fun setIntent(intent: Intent) {
        pendingIntent = intent
    }

    /**
     * Creates (on first call) or returns the cached root [View] for this
     * instance. [host] is captured for the lifetime of this [TGActivity].
     */
    fun getOrCreateRootView(
        host: FragmentActivity,
        compositionContext: CompositionContext,
        content: @Composable () -> Unit,
    ): View {
        rootView?.let { return it }
        hostActivity = host
        themedContext = ContextThemeWrapper(host, R.style.TGTheme)
        destroyed = false
        editorReleased = false
        composeContent = content
        parentComposition = compositionContext
        currentInstance = this
        clearContext()
        attachInstance()
        pendingIntent = host.intent
        createModules()
        TGMessagesManager.getInstance().setResources(
            TGResourceBundle.getBundle(findContext(), LANGUAGE_RESOURCE, Locale.getDefault())
        )
        resultManager.initialize()
        permissionResultManager.initialize()
        navigationManager.initialize()

        val view = getThemedLayoutInflater().inflate(R.layout.activity_tg, null, false)
        rootView = view
        onViewCreated(view)
        return view
    }

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val manager = TGActionManager.getInstance(findContext())
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> manager.execute(if (event.isShiftPressed) TGShiftNoteUpAction.NAME else TGGoUpAction.NAME)
            KeyEvent.KEYCODE_DPAD_DOWN -> manager.execute(if (event.isShiftPressed) TGShiftNoteDownAction.NAME else TGGoDownAction.NAME)
            KeyEvent.KEYCODE_DPAD_LEFT -> manager.execute(TGGoLeftAction.NAME)
            KeyEvent.KEYCODE_DPAD_RIGHT -> manager.execute(TGGoRightAction.NAME)
            KeyEvent.KEYCODE_DEL -> manager.execute(TGDeleteNoteOrRestAction.NAME)
            KeyEvent.KEYCODE_TAB -> manager.execute(TGInsertRestBeatAction.NAME)
            KeyEvent.KEYCODE_MINUS -> manager.execute(if (event.isShiftPressed) TGDecrementNoteSemitoneAction.NAME else TGDecrementDurationAction.NAME)
            KeyEvent.KEYCODE_EQUALS -> manager.execute(if (event.isShiftPressed) TGIncrementNoteSemitoneAction.NAME else TGIncrementDurationAction.NAME)
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> manager.execute(TGSetNoteFretNumberAction.getActionName(keyCode - KeyEvent.KEYCODE_0))
            KeyEvent.KEYCODE_PERIOD -> manager.execute(TGChangeDottedDurationAction.NAME)
            KeyEvent.KEYCODE_D -> {
                val context = manager.createActionContext()
                context.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, TGDurationMenu(this))
                context.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this)
                manager.execute(TGOpenMenuAction.NAME, context)
            }
            KeyEvent.KEYCODE_G -> manager.execute(TGTransportPlayAction.NAME)
            KeyEvent.KEYCODE_I -> manager.execute(TGChangeLetRingAction.NAME)
            KeyEvent.KEYCODE_M -> manager.execute(TGShowSmartMenuAction.NAME)
            KeyEvent.KEYCODE_P -> manager.execute(TGChangePalmMuteAction.NAME)
            KeyEvent.KEYCODE_V -> manager.execute(TGChangeVibratoNoteAction.NAME)
            else -> return false
        }
        return true
    }

    /** Layout inflater resolving TGTheme's custom attrs. */
    fun getThemedLayoutInflater(): LayoutInflater = LayoutInflater.from(themedContext)

    open fun onViewCreated(view: View) {
        val rootLayout = view.findViewById<View>(R.id.root_layout)
        rootLayout.isLongClickable = true
        rootLayout.setOnCreateContextMenuListener { menu, v, menuInfo ->
            onCreateContextMenu(menu, v, menuInfo)
        }
        (hostActivity as AppCompatActivity).setSupportActionBar(view.findViewById<Toolbar>(R.id.tg_toolbar))
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        installContentComposeView(view)
        drawerManager.initialize()
        loadDefaultFragment()
        connectPlugins()
        loadDefaultSong()
        drawerManager.syncState()
    }

    /**
     * Installs a single [ComposeView] as `content_frame`'s content, rendering
     * the currently navigated-to [app.tuxguitar.android.fragment.TGScreen]
     * plus any active [TGComposeDialog] on top of it. Replaces the previous
     * `FragmentManager` transaction into that same container.
     */
    private fun installContentComposeView(view: View) {
        val contentFrame = view.findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.addView(
            ComposeView(themedContext).apply {
                setParentCompositionContext(parentComposition)
                setContent {
                    composeContent?.invoke()
                }
            }
        )
    }


    /** Called by the repository when its AndroidView is released or replaced. */
    fun destroyRootView() {
        if (destroyed) return
        destroyed = true
        onFinishRequested = null
        onUiStateChanged = null
        currentDialog?.onHide()
        currentDialog = null
        navigationManager.dispose()
        setDisplayOn(false)
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        (contentFrame?.getChildAt(0) as? ComposeView)?.disposeComposition()
        composeContent = null
        parentComposition = null
        (rootView as? ViewGroup)?.removeAllViews()
        (hostActivity as AppCompatActivity).setSupportActionBar(null)
        if (modulesInitialized) {
            releaseEditor()
            detachInstance()
            destroyModules()
            modulesInitialized = false
        }
        context?.clear()
        if (currentInstance === this) {
            currentInstance = null
        }
        rootView = null
    }

    /** Called from MainActivity.onNewIntent when this is the current editor. */
    fun onNewIntent(intent: Intent) {
        setIntent(intent)
        callProcessIntent()
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        drawerManager.onConfigurationChanged(newConfig)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean = drawerManager.onOptionsItemSelected(item)

    /** Rebuilds the toolbar's options menu from the currently shown screen. */
    fun rebuildOptionsMenu() {
        val toolbar = findViewById<Toolbar>(R.id.tg_toolbar) ?: return
        toolbar.menu.clear()
        val screen = navigationManager.currentScreen ?: return
        if (screen.hasOptionsMenu) {
            screen.onPostCreateOptionsMenu(toolbar.menu, hostActivity.menuInflater)
        }
    }

    open fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        TGMenuContextualInflater.getInstance(findContext()).inflate(menu, hostActivity.menuInflater)
    }

    fun openContextMenu() = hostActivity.openContextMenu(requireNotNull(findViewById(R.id.root_layout)))

    /** Replacement for `Activity.findViewById`/`Fragment.requireView().findViewById`. */
    fun <T : View> findViewById(id: Int): T? = rootView?.findViewById(id)

    fun requireActivity(): FragmentActivity = hostActivity

    fun requireContext(): Context = themedContext

    fun getString(resId: Int): String = themedContext.getString(resId)
    fun getString(resId: Int, vararg formatArgs: Any?): String = themedContext.getString(resId, *formatArgs)

    @Suppress("DEPRECATION")
    fun startActivityForResult(intent: Intent, requestCode: Int) =
        hostActivity.startActivityForResult(intent, requestCode)

    @Suppress("DEPRECATION")
    fun requestPermissions(permissions: Array<String>, requestCode: Int) =
        hostActivity.requestPermissions(permissions, requestCode)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean =
        hostActivity.shouldShowRequestPermissionRationale(permission)

    /** Forwarded from `MainActivity.onActivityResult`. */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultManager.onActivityResult(requestCode, resultCode, data)
    }

    /** Forwarded from `MainActivity.onRequestPermissionsResult`. */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionResultManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun attachInstance() { TGActivityController.getInstance(findContext()).activity = this }
    fun detachInstance() { TGActivityController.getInstance(findContext()).activity = null }

    fun createModules() {
        val context = findContext()
        TGThreadManager.getInstance(context).setThreadHandler(TGMultiThreadHandler())
        modulesInitialized = true
        TGSynchronizer.getInstance(context).setController(TGSynchronizerControllerImpl(context))
        TGErrorManager.getInstance(context).addErrorHandler(TGErrorHandlerImpl(this))
        TGResourceManager.getInstance(context).setResourceLoader(TGResourceLoaderImpl(this))
        TGActionAdapterManager.getInstance(context).initialize(this)
        TGEditorManager.getInstance(context).setLockControl(TGLock(context))
        TGVarAdapter.initialize(context)
        TGPropertiesAdapter.initialize(context, requireContext())
        TGTransportAdapter.getInstance(context).initialize()
    }

    fun destroyModules() { TGThreadManager.getInstance(findContext()).dispose() }
    fun connectPlugins() { TGPluginManager.getInstance(findContext()).connectEnabled() }
    fun disconnectPlugins() { TGPluginManager.getInstance(findContext()).disconnectAll() }
    fun destroyEditor() { TGEditorManager.getInstance(findContext()).destroy(null) }
    @Synchronized
    private fun releaseEditor() {
        if (!editorReleased) {
            editorReleased = true
            disconnectPlugins()
            destroyEditor()
        }
    }

    fun destroy() { releaseEditor(); callFinishAction() }

    fun updateCache(updateItems: Boolean) = updateCache(updateItems, null)
    fun updateCache(updateItems: Boolean, sourceContext: TGAbstractContext?) {
        val editorManager = TGEditorManager.getInstance(findContext())
        if (updateItems) editorManager.updateSelection(sourceContext)
        editorManager.redraw(sourceContext)
    }

    fun getDrawerManager() = drawerManager
    fun getNavigationManager() = navigationManager
    fun getResultManager() = resultManager
    fun getTuningManager() = TuningManager.getInstance(findContext())
    fun getPermissionResultManager() = permissionResultManager
    fun getActionBarController() = actionBar

    fun findContext(): TGContext {
        if (context == null) context = TGContext()
        return context!!
    }

    fun clearContext() { findContext().clear() }
    fun loadDefaultFragment() { navigationManager.callOpenFragment(TGMainFragmentController.getInstance(findContext())) }

    fun loadDefaultSong() {
        if (intent?.action == Intent.ACTION_VIEW) callProcessIntent() else callLoadDefaultSong()
    }

    fun callLoadDefaultSong() { TGActionProcessor(findContext(), TGLoadTemplateAction.NAME).process() }
    fun callProcessIntent() {
        TGActionProcessor(findContext(), TGProcessIntentAction.NAME).apply {
            setAttribute(TGProcessIntentAction.ATTRIBUTE_ACTIVITY, this@TGActivity)
            process()
        }
    }
    fun callBackAction() {
        TGActionProcessor(findContext(), TGBackAction.NAME).apply {
            setAttribute(TGBackAction.ATTRIBUTE_ACTIVITY, this@TGActivity)
            process()
        }
    }
    fun callFinishAction() {
        TGActionProcessor(findContext(), TGFinishAction.NAME).apply {
            setAttribute(TGFinishAction.ATTRIBUTE_ACTIVITY, this@TGActivity)
            process()
        }
    }

    /** Replacement for the removed `Activity.isDestroyed()` override. */
    fun isFragmentDestroyed() = destroyed

    /**
     * Requests that the host navigate away from the editor destination. Set
     * by the repository and delivered to the UI through EditorState.
     */
    var onFinishRequested: (() -> Unit)? = null
    fun finish() {
        onFinishRequested?.invoke()
    }

    fun setDisplayOn(displayOn: Boolean) {
        if (maintainDisplayON != displayOn) {
            hostActivity.runOnUiThread {
                if (displayOn) hostActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else hostActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        maintainDisplayON = displayOn
    }

    /** Shows [dialog] as a Compose `ModalBottomSheet` over this activity's content. */
    fun showComposeDialog(dialog: TGComposeDialog) {
        hostActivity.runOnUiThread {
            if (!destroyed) {
                currentDialog?.onHide()
                currentDialog = dialog
                dialog.onShow()
                notifyUiStateChanged()
            }
        }
    }

    fun dismissComposeDialog(dialog: TGComposeDialog) {
        hostActivity.runOnUiThread {
            if (!destroyed && currentDialog === dialog) {
                currentDialog = null
                dialog.onHide()
                notifyUiStateChanged()
            }
        }
    }

    companion object {
        private const val LANGUAGE_RESOURCE = "lang/messages"

        /**
         * The currently attached [TGActivity] instance, if any. Bridges
         * legacy context lookups within the adapter. The application uses the
         * injected EditorHost instead. There is at most one engine attached,
         * owned and released by LegacyEditorRepository.
         */
        @JvmStatic
        @Volatile
        var currentInstance: TGActivity? = null
            private set

        @JvmStatic
        fun requireCurrent(): TGActivity =
            requireNotNull(currentInstance) { "No TGActivity instance is currently attached" }
    }
}
