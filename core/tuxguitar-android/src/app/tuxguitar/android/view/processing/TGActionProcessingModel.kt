package app.tuxguitar.android.view.processing

class TGActionProcessingModel {
    private var processing = false
    private var processingTime: Long = 0

    fun update(processing: Boolean) {
        this.processing = processing
        this.processingTime = System.currentTimeMillis()
    }

    fun isProcessing(): Boolean = processing

    fun getProcessingTime(): Long = processingTime
}
