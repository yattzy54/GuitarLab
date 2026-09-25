package app.tuxguitar.android.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
 * Hosts the legacy TuxGuitar editor engine as a Fragment inside the app's
 * single Activity ([app.tuxguitar.android.MainActivity], via reflection to
 * avoid a circular module dependency is not needed here since this class
 * never references it directly).
 *
 * This used to be an `AppCompatActivity` subclass; it is now a plain
 * [Fragment]. Android APIs that only exist on `Activity` (window flags,
 * `supportActionBar`, `ActionBarDrawerToggle`, key events, `onNewIntent`,
 * ...) are reached through [requireActivity] or bridged in by the host
 * Activity via [currentInstance]/[onKeyDown]/[setIntent]. [TGContext] stays
 * scoped to this Fragment's own lifetime exactly like it was previously
 * scoped to the Activity's lifetime.
 */
open class TGActivity : Fragment() {
    private var destroyed = false
    private var context: TGContext? = null
    private val navigationManager = TGNavigationManager(this)
    private val drawerManager = TGDrawerManager(this)
    private val actionBar = TGActivityActionBarController(this)
    private val resultManager = TGActivityResultManager()
    private val permissionResultManager = TGActivityPermissionResultManager()
    private var maintainDisplayON = false
    private var pendingIntent: Intent? = null

    val intent: Intent?
        get() = pendingIntent

    fun setIntent(intent: Intent) {
        pendingIntent = intent
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentInstance = this
    }

    override fun onDetach() {
        super.onDetach()
        if (currentInstance === this) {
            currentInstance = null
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        destroyed = false
        clearContext()
        attachInstance()
        pendingIntent = requireActivity().intent
        createModules()
        TGMessagesManager.getInstance().setResources(
            TGResourceBundle.getBundle(findContext(), LANGUAGE_RESOURCE, Locale.getDefault())
        )
        resultManager.initialize()
        permissionResultManager.initialize()
        navigationManager.initialize()
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val themedContext = ContextThemeWrapper(requireActivity(), R.style.TGTheme)
        return super.onGetLayoutInflater(savedInstanceState).cloneInContext(themedContext)
    }

    /** Layout inflater resolving TGTheme's custom attrs, for use outside `onCreateView`. */
    fun getThemedLayoutInflater(): LayoutInflater =
        LayoutInflater.from(ContextThemeWrapper(requireActivity(), R.style.TGTheme))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.activity_tg, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(view.findViewById(R.id.root_layout))
        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById<Toolbar>(R.id.tg_toolbar))
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        drawerManager.initialize()
        loadDefaultFragment()
        connectPlugins()
        loadDefaultSong()
        drawerManager.syncState()
    }

    override fun onDestroyView() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachInstance()
        destroyModules()
        clearContext()
        destroyed = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    /** Called from MainActivity.onNewIntent when this fragment is the current editor. */
    fun onNewIntent(intent: Intent) {
        setIntent(intent)
        callProcessIntent()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerManager.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (drawerManager.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        TGMenuContextualInflater.getInstance(findContext()).inflate(menu, requireActivity().menuInflater)
    }

    fun openContextMenu() = requireActivity().openContextMenu(requireView().findViewById(R.id.root_layout))

    /** Fragment-compatible replacement for `Activity.findViewById`. */
    fun <T : View> findViewById(id: Int): T? = view?.findViewById(id)

    @Deprecated("Legacy activity result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultManager.onActivityResult(requestCode, resultCode, data)
    }

    @Deprecated("Legacy permission result API")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        @Suppress("UNCHECKED_CAST")
        val permissionNames = permissions as Array<String>
        permissionResultManager.onRequestPermissionsResult(requestCode, permissionNames, grantResults)
    }

    fun attachInstance() { TGActivityController.getInstance(findContext()).activity = this }
    fun detachInstance() { TGActivityController.getInstance(findContext()).activity = null }

    fun createModules() {
        val context = findContext()
        TGThreadManager.getInstance(context).setThreadHandler(TGMultiThreadHandler())
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
    fun destroy() { disconnectPlugins(); destroyEditor(); callFinishAction() }

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
     * Replacement for the removed `Activity.finish()`: requests that the
     * host navigate away from the editor destination. Set by whichever
     * Composable hosts this fragment (see `TuxGuitarScreen`).
     */
    var onFinishRequested: (() -> Unit)? = null
    fun finish() {
        onFinishRequested?.invoke()
    }

    fun setDisplayOn(displayOn: Boolean) {
        if (maintainDisplayON != displayOn) {
            requireActivity().runOnUiThread {
                if (displayOn) requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        maintainDisplayON = displayOn
    }

    companion object {
        private const val LANGUAGE_RESOURCE = "lang/messages"

        /**
         * The currently attached [TGActivity] fragment instance, if any.
         * Bridges Android APIs that are only dispatched to the real host
         * Activity (key events, `onNewIntent`) and lets legacy code that
         * used to reach the TGActivity through its own Context (a View's
         * `context`, `requireActivity()` from a child fragment, etc.) find
         * it again now that it is not a Context itself. There is at most one
         * such instance alive at a time, matching the previous singleTop
         * Activity behaviour.
         */
        @JvmStatic
        @Volatile
        var currentInstance: TGActivity? = null
            private set

        @JvmStatic
        fun requireCurrent(): TGActivity =
            requireNotNull(currentInstance) { "No TGActivity fragment is currently attached" }
    }
}
