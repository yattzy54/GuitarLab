package app.tuxguitar.android.graphics

import app.tuxguitar.ui.resource.UIFont
import app.tuxguitar.ui.resource.UIFontModel

class TGFontImpl(model: UIFontModel) : UIFont {
    private var model: UIFontModel? = model

    override fun dispose() {
        model = null
    }

    override fun isDisposed(): Boolean = model == null

    override fun getName(): String = model!!.name

    override fun getHeight(): Float = model!!.height

    override fun isBold(): Boolean = model!!.isBold

    override fun isItalic(): Boolean = model!!.isItalic
}
