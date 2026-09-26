package com.mmt.guitarlab.config

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppFlavorConfig @Inject constructor() {
    val isTabsFlavor: Boolean get() = try {
        val clazz = Class.forName("com.mmt.guitarlab.BuildConfig")
        val field = clazz.getField("IS_TABS_FLAVOR")
        @Suppress("ExplicitItLambda", "RedundantExplicitLabel", "AccessStaticViaInstance")
        (field.get(null) as? Boolean) ?: false
    } catch (_: Exception) {
        false
    }
}
