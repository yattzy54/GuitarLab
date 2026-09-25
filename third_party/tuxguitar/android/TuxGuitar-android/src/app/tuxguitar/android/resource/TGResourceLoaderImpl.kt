package app.tuxguitar.android.resource

import android.content.Context
import android.content.res.AssetManager
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.resource.TGResourceException
import app.tuxguitar.resource.TGResourceLoader
import dalvik.system.DexClassLoader
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.Enumeration

class TGResourceLoaderImpl(private val activity: TGActivity) : TGResourceLoader {
    init {
        synchronized(TGResourceLoaderImpl::class.java) {
            if (classLoader == null) {
                classLoader = createClassLoader()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> loadClass(name: String): Class<T> {
        try {
            return getClassLoader().loadClass(name) as Class<T>
        } catch (e: Throwable) {
            throw TGResourceException(e)
        }
    }

    override fun getResourceAsStream(name: String): InputStream? {
        try {
            return getClassLoader().getResourceAsStream(name)
        } catch (e: Throwable) {
            throw TGResourceException(e)
        }
    }

    override fun getResource(name: String): URL? {
        try {
            return getClassLoader().getResource(name)
        } catch (e: Throwable) {
            throw TGResourceException(e)
        }
    }

    override fun getResources(name: String): Enumeration<URL> {
        try {
            return getClassLoader().getResources(name)
        } catch (e: Throwable) {
            throw TGResourceException(e)
        }
    }

    fun getClassLoader(): ClassLoader = classLoader!!

    fun createClassLoader(): ClassLoader {
        val context = activity.requireContext().applicationContext
        val optimizedDirectory = context.getDir("dex", Context.MODE_PRIVATE).absolutePath
        val fileNames = unpackPlugins(optimizedDirectory)
        return if (fileNames.isNotEmpty()) {
            DexClassLoader(createPath(fileNames), optimizedDirectory, createLibraryPath(), context.classLoader)
        } else {
            context.classLoader
        }
    }

    fun unpackPlugins(path: String): List<String> {
        try {
            val fileNames = ArrayList<String>()
            val assetManager: AssetManager = activity.requireContext().assets
            val assets = assetManager.list(ASSET_PLUGINS)
            if (assets != null) {
                for (asset in assets) {
                    val pluginFileName = File(path, asset)
                    BufferedInputStream(assetManager.open(ASSET_PLUGINS + File.separator + asset)).use { inputStream ->
                        BufferedOutputStream(FileOutputStream(pluginFileName)).use { outputStream ->
                            val buffer = ByteArray(ASSET_BUFFER_SIZE)
                            while (true) {
                                val length = inputStream.read(buffer, 0, ASSET_BUFFER_SIZE)
                                if (length <= 0) {
                                    break
                                }
                                outputStream.write(buffer, 0, length)
                            }
                        }
                    }
                    fileNames.add(pluginFileName.absolutePath)
                }
            }
            return fileNames
        } catch (e: IOException) {
            throw TGResourceException(e)
        }
    }

    fun createPath(fileNames: List<String>): String = fileNames.joinToString(File.pathSeparator)

    fun createLibraryPath(): String {
        val ctx = activity.requireContext()
        return ctx.packageManager.getApplicationInfo(ctx.packageName, 0).nativeLibraryDir
    }

    companion object {
        private const val ASSET_PLUGINS = "plugins"
        private const val ASSET_BUFFER_SIZE = 8 * 1024
        private var classLoader: ClassLoader? = null
    }
}
