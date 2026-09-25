package app.tuxguitar.android.properties

import android.app.Activity
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
        fun initialize(context: TGContext, activity: Activity) {
            addFactory(context)
            addReader(context, TGBrowserProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE))
            addWriter(activity, context, TGBrowserProperties.RESOURCE, TGBrowserProperties.MODULE)
            addReader(context, TGTransportProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE))
            addWriter(activity, context, TGTransportProperties.RESOURCE, TGTransportProperties.MODULE)
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
        fun createSharedPreferencesReader(context: TGContext, activity: Activity, module: String, resource: String): TGPropertiesReader {
            return TGSharedPreferencesReader(activity, module, resource, TGResourcePropertiesReader(context, null, "-$resource"))
        }

        @JvmStatic
        fun createSharedPreferencesWriter(activity: Activity, module: String, resource: String): TGPropertiesWriter {
            return TGSharedPreferencesWriter(activity, module, resource)
        }

        private fun addWriter(activity: Activity, context: TGContext, resource: String, module: String) {
            addWriter(context, resource, createSharedPreferencesWriter(activity, module, resource))
        }
    }
}
