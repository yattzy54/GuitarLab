package app.tuxguitar.android.graphics

import app.tuxguitar.ui.resource.UIColor
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIFont
import app.tuxguitar.ui.resource.UIFontModel
import app.tuxguitar.ui.resource.UIImage
import app.tuxguitar.ui.resource.UIResourceFactory
import java.io.InputStream

class TGResourceFactoryImpl : UIResourceFactory {
    override fun createColor(colorModel: UIColorModel): UIColor = TGColorImpl(colorModel)

    override fun createColor(red: Int, green: Int, blue: Int): UIColor = createColor(UIColorModel(red, green, blue))

    override fun createFont(fontModel: UIFontModel): UIFont = TGFontImpl(fontModel)

    override fun createFont(name: String, height: Float, bold: Boolean, italic: Boolean): UIFont {
        return createFont(UIFontModel(name, height, bold, italic))
    }

    override fun createImage(width: Float, height: Float): UIImage = TGImageImpl(width, height)

    override fun createImage(inputStream: InputStream): UIImage = TGImageImpl(inputStream)
}
