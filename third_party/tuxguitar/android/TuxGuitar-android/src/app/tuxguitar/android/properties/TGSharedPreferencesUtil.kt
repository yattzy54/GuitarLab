package app.tuxguitar.android.properties

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat

class TGSharedPreferencesUtil private constructor() {
    companion object {
        @JvmStatic
        fun getSharedPreferencesName(activity: Activity?, module: String, resource: String): String {
            return getPreferencesPrefix(activity) + "." + module + "-" + resource
        }

        @JvmStatic
        fun getPreferencesPrefix(activity: Activity?): String {
            val targetActivity = activity!!
            val prefix = StringBuilder("tuxguitar")
            try {
                val packageInfo = targetActivity.packageManager.getPackageInfo(targetActivity.packageName, 0)
                if (packageInfo != null) {
                    prefix.append("-").append(PackageInfoCompat.getLongVersionCode(packageInfo))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return prefix.toString()
        }
    }
}
