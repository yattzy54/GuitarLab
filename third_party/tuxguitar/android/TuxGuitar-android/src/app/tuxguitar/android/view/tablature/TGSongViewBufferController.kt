package app.tuxguitar.android.view.tablature

import app.tuxguitar.graphics.control.TGResourceBuffer
import app.tuxguitar.util.TGSynchronizer

class TGSongViewBufferController(private val songView: TGSongViewController) {
    private var selection = 0
    private var resourceBuffer: TGResourceBuffer? = null

    fun updateSelection() {
        val currentSelection = songView.getTrackSelection()
        if (currentSelection != selection) {
            selection = currentSelection
            if (selection != -1) {
                disposeBufferLater(getResourceBuffer())
                resourceBuffer = null
            }
        }
    }

    fun getResourceBuffer(): TGResourceBuffer {
        if (resourceBuffer == null) {
            resourceBuffer = TGResourceBuffer()
        }
        return resourceBuffer!!
    }

    fun disposeBufferLater(buffer: TGResourceBuffer) {
        TGSynchronizer.getInstance(songView.context).executeLater {
            buffer.disposeAllResources()
        }
    }
}
