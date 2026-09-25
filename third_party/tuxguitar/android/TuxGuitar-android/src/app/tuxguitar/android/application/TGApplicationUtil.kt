package app.tuxguitar.android.application

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.view.View
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

object TGApplicationUtil {
    @JvmStatic
    fun findContext(activity: Activity): TGContext = (activity as TGActivity).findContext()

    @JvmStatic
    fun findContext(fragment: Fragment): TGContext = findContext(fragment.activity)

    @JvmStatic
    fun findContext(view: View): TGContext = findContext(view.context as Activity)

    @JvmStatic
    fun findContext(context: Context): TGContext = findContext(context as Activity)
}
