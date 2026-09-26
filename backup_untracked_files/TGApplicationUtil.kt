package app.tuxguitar.android.application

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.view.View
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

/**
 * Central resolver from various Android objects to the app's [TGContext].
 *
 * Now that [TGActivity] is a Fragment rather than a real [Activity]/[Context],
 * only the most-specific overload below (taking a [TGActivity] directly) can
 * resolve it without going through a lookup: callers that already hold a
 * [TGActivity] reference (menus, dialog controllers, ...) bind to that
 * overload automatically thanks to Kotlin overload resolution. All other,
 * loosely-typed callers (a View's `context`, a legacy platform [Fragment]'s
 * `activity`, ...) fall back to [TGActivity.requireCurrent], which tracks
 * whichever [TGActivity] instance is currently attached - mirroring how,
 * previously, there was always exactly one live [TGActivity] Activity.
 */
object TGApplicationUtil {
    @JvmStatic
    fun findContext(activity: TGActivity): TGContext = activity.findContext()

    @JvmStatic
    fun findContext(activity: Activity): TGContext = TGActivity.requireCurrent().findContext()

    @JvmStatic
    fun findContext(fragment: Fragment): TGContext = TGActivity.requireCurrent().findContext()

    @JvmStatic
    fun findContext(view: View): TGContext = TGActivity.requireCurrent().findContext()

    @JvmStatic
    fun findContext(context: Context): TGContext = TGActivity.requireCurrent().findContext()
}
