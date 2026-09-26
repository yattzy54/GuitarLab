package app.tuxguitar.android.properties

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.tgDataStore by preferencesDataStore(name = "tuxguitar_preferences")

abstract class TGDataStoreHandler(
    protected val context: Context,
    protected val module: String,
    protected val resource: String,
) {
    protected fun getDataStoreKey(key: String): androidx.datastore.preferences.core.Preferences.Key<String> {
        val name = TGSharedPreferencesUtil.getSharedPreferencesName(context, module, resource)
        return stringPreferencesKey("${name}_$key")
    }

    fun getAll(): Map<String, Any?> {
        return runBlocking {
            val prefs = context.tgDataStore.data.first()
            val name = TGSharedPreferencesUtil.getSharedPreferencesName(context, module, resource)
            val prefix = "${name}_"
            prefs.asMap().entries
                .filter { it.key.name.startsWith(prefix) }
                .associate { it.key.name.removePrefix(prefix) to it.value }
        }
    }

    fun putString(key: String, value: String?) {
        runBlocking {
            context.tgDataStore.edit { prefs ->
                val datastoreKey = getDataStoreKey(key)
                if (value == null) {
                    prefs.remove(datastoreKey)
                } else {
                    prefs[datastoreKey] = value
                }
            }
        }
    }
}
