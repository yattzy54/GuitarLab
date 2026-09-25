package app.tuxguitar.android.properties

import android.content.Context
import app.tuxguitar.android.browser.config.TGBrowserProperties
import app.tuxguitar.android.transport.TGTransportProperties
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.configuration.TGConfigManager
import app.tuxguitar.util.properties.TGPropertiesManager
import app.tuxguitar.util.properties.TGPropertiesReader
import app.tuxguitar.util.properties.TGPropertiesWriter

class TGPropertiesAdapter private constructor() {
    companion object {
        @JvmStatic
        fun initialize(context: TGContext, androidContext: Context) {
            addFactory(context)
            addReader(context, TGBrowserProperties.RESOURCE, createSharedPreferencesReader(context, androidContext, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE))
            addWriter(androidContext, context, TGBrowserProperties.RESOURCE, TGBrowserProperties.MODULE)
            addReader(context, TGTransportProperties.RESOURCE, createSharedPreferencesReader(context, androidContext, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE))
            addWriter(androidContext, context, TGTransportProperties.RESOURCE, TGTransportProperties.MODULE)
            addReader(context, TGConfigManager.RESOURCE, TGResourcePropertiesReader(context, null, null))
        }

        @JvmStatic
        fun addFactory(context: TGContext) {
            TGPropertiesManager.getInstance(context).propertiesFactory = TGPropertiesFactoryImpl()
        }

        @JvmStatic
        fun addReader(context: TGContext, resource: String, reader: TGPropertiesReader) {
            TGPropertiesManager.getInstance(context).addPropertiesReader(resource, reader)
        }

        @JvmStatic
        fun addWriter(context: TGContext, resource: String, writer: TGPropertiesWriter) {
            TGPropertiesManager.getInstance(context).addPropertiesWriter(resource, writer)
        }

        @JvmStatic
        fun createSharedPreferencesReader(context: TGContext, androidContext: Context, module: String, resource: String): TGPropertiesReader {
            return TGSharedPreferencesReader(androidContext, module, resource, TGResourcePropertiesReader(context, null, "-$resource"))
        }

        @JvmStatic
        fun createSharedPreferencesWriter(androidContext: Context, module: String, resource: String): TGPropertiesWriter {
            return TGSharedPreferencesWriter(androidContext, module, resource)
        }

        private fun addWriter(androidContext: Context, context: TGContext, resource: String, module: String) {
            addWriter(context, resource, createSharedPreferencesWriter(androidContext, module, resource))
        }
    }
}
