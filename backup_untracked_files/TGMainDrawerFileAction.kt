package app.tuxguitar.android.drawer.main

import app.tuxguitar.android.action.TGActionProcessorListener

data class TGMainDrawerFileAction(
    val label: Int,
    val processor: TGActionProcessorListener,
)
