package app.tuxguitar.android.view.tablature

import app.tuxguitar.graphics.control.TGResourceBuffer
import app.tuxguitar.util.TGSynchronizer

class TGSongViewBufferController(private val songView: TGSongViewController) {

    private var selection = 0
    private var resourceBuffer: TGResourceBuffer? = null

    fun updateSelection() {
        val selection = this.songView.trackSelection
        if (selection != this.selection) {
            this.selection = selection
            if (this.selection != -1) {
                this.disposeBufferLater(this.resourceBuffer)
                this.resourceBuffer = null
            }
        }
    }

    fun getResourceBuffer(): TGResourceBuffer {
        if (this.resourceBuffer == null) {
            this.resourceBuffer = TGResourceBuffer()
        }
        return this.resourceBuffer!!
    }

    fun disposeBufferLater(buffer: TGResourceBuffer?) {
        if (buffer == null) return
        TGSynchronizer.getInstance(this.songView.context).executeLater { buffer.disposeAllResources() }
    }
}
