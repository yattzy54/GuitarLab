package app.tuxguitar.android.properties

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat

class TGSharedPreferencesUtil private constructor() {
    companion object {
        @JvmStatic
        fun getSharedPreferencesName(context: Context?, module: String, resource: String): String {
            return getPreferencesPrefix(context) + "." + module + "-" + resource
        }

        @JvmStatic
        fun getPreferencesPrefix(context: Context?): String {
            val targetContext = context!!
            val prefix = StringBuilder("tuxguitar")
            try {
                val packageInfo = targetContext.packageManager.getPackageInfo(targetContext.packageName, 0)
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
