package app.tuxguitar.android.properties

import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesException
import app.tuxguitar.util.properties.TGPropertiesFactory

class TGPropertiesFactoryImpl : TGPropertiesFactory {
    @Throws(TGPropertiesException::class)
    override fun createProperties(): TGProperties = TGPropertiesImpl()
}
