package app.tuxguitar.android.view.tablature

class TGScroll {
    private val xAxis = TGScrollAxis()
    private val yAxis = TGScrollAxis()

    fun getX(): TGScrollAxis = xAxis
    fun getY(): TGScrollAxis = yAxis
}
