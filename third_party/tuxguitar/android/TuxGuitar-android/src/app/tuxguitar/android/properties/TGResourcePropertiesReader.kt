package app.tuxguitar.android.properties

import app.tuxguitar.resource.TGResourceManager
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesException
import app.tuxguitar.util.properties.TGPropertiesReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

class TGResourcePropertiesReader(
    private val context: TGContext,
    private val modulePrefix: String?,
    private val moduleSuffix: String?,
) : TGPropertiesReader {
    @Throws(TGPropertiesException::class)
    override fun readProperties(targetProperties: TGProperties, module: String) {
        try {
            val resourceName = buildString {
                if (modulePrefix != null) {
                    append(modulePrefix)
                }
                append(module)
                if (moduleSuffix != null) {
                    append(moduleSuffix)
                }
                append(".cfg")
            }
            TGResourceManager.getInstance(context).getResourceAsStream(resourceName)?.use { inputStream ->
                val properties = Properties()
                InputStreamReader(inputStream, StandardCharsets.UTF_8).use { reader ->
                    properties.load(reader)
                }
                for ((key, value) in properties.entries) {
                    targetProperties.setValue(key as String, value?.toString())
                }
            }
        } catch (throwable: Throwable) {
            throw TGPropertiesException(throwable)
        }
    }
}
