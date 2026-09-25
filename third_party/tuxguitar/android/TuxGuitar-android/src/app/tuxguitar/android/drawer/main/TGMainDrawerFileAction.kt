package app.tuxguitar.android.drawer.main

import app.tuxguitar.android.action.TGActionProcessorListener

class TGMainDrawerFileAction(
    private val label: Int,
    private val processor: TGActionProcessorListener
) {
    fun getLabel(): Int = label
    fun getProcessor(): TGActionProcessorListener = processor
}
