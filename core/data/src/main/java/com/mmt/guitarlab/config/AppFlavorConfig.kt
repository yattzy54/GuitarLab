package com.mmt.guitarlab.config

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppFlavorConfig @Inject constructor() {
    val flavorType: String = "standard"
    val isTabsFlavor: Boolean = false
    val appFlavorName: String = "GuitarLab"
}
