package app.tuxguitar.android.action.listener.cache

import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext

class TGUpdateBuffer(private val context: TGContext) {
    private var updateCache: Int? = null
    private var updateSong = false
    private var updateSongLoaded = false
    private var updateSongSaved = false
    private val updateMeasures = mutableListOf<Int>()
    private val updateRunnables = mutableListOf<Runnable>()

    fun clear() {
        updateRunnables.clear()
        updateMeasures.clear()
        updateCache = null
        updateSong = false
        updateSongLoaded = false
        updateSongSaved = false
    }

    fun apply(sourceContext: TGAbstractContext?) {
        applyUpdateSong(sourceContext)
        applyUpdateLoadedSong(sourceContext)
        applyUpdateSavedSong(sourceContext)
        applyUpdateMeasures(sourceContext)
        applyUpdateRunnables()
        applyUpdateCache(sourceContext)
    }

    fun applyUpdateSong(sourceContext: TGAbstractContext?) {
        if (updateSong) TGEditorManager.getInstance(context).updateSong(sourceContext)
    }
    fun applyUpdateLoadedSong(sourceContext: TGAbstractContext?) {
        if (updateSongLoaded) TGEditorManager.getInstance(context).updateLoadedSong(sourceContext)
    }
    fun applyUpdateSavedSong(sourceContext: TGAbstractContext?) {
        if (updateSongSaved) TGEditorManager.getInstance(context).updateSavedSong(sourceContext)
    }
    fun applyUpdateMeasures(sourceContext: TGAbstractContext?) {
        if (!updateSong && !updateSongLoaded && !updateSongSaved) {
            TGEditorManager.getInstance(context).updateMeasures(updateMeasures, sourceContext)
        }
    }
    fun applyUpdateCache(sourceContext: TGAbstractContext?) {
        val cache = updateCache ?: return
        if (cache >= UPDATE_CACHE) {
            TGActivityController.getInstance(context).activity
                ?.updateCache(cache == UPDATE_ITEMS, sourceContext)
        }
    }
    fun applyUpdateRunnables() = updateRunnables.forEach(Runnable::run)
    fun requestUpdateCache(updateItems: Boolean) {
        if (updateCache == null || updateCache!! < UPDATE_ITEMS) {
            updateCache = if (updateItems) UPDATE_ITEMS else UPDATE_CACHE
        }
    }
    fun requestUpdateMeasure(number: Int) {
        if (number !in updateMeasures) updateMeasures.add(number)
    }
    fun requestUpdateSong() { updateSong = true }
    fun requestUpdateLoadedSong() { updateSongLoaded = true }
    fun requestUpdateSavedSong() { updateSongSaved = true }
    fun doPostUpdate(runnable: Runnable) { updateRunnables.add(runnable) }

    companion object {
        private const val UPDATE_CACHE = 1
        private const val UPDATE_ITEMS = 2
    }
}
