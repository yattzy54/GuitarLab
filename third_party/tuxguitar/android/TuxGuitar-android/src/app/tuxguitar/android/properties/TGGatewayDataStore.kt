package app.tuxguitar.android.properties

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.tuxGuitarDataStore by preferencesDataStore(name = "tuxguitar_reactive_preferences")

class TGGatewayDataStore(private val context: Context) {
    private val preferencesFlow: Flow<androidx.datastore.preferences.core.Preferences> = context.tuxGuitarDataStore.data

    fun observeValue(key: String): Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return preferencesFlow.map { prefs -> prefs[prefKey] }
    }

    suspend fun getValue(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return preferencesFlow.first()[prefKey]
    }

    suspend fun setValue(key: String, value: String?) {
        context.tuxGuitarDataStore.edit { prefs ->
            val prefKey = stringPreferencesKey(key)
            if (value == null) {
                prefs.remove(prefKey)
            } else {
                prefs[prefKey] = value
            }
        }
    }

    fun getAllSync(prefix: String): Map<String, String?> {
        return runBlocking {
            val prefs = preferencesFlow.first()
            prefs.asMap().entries
                .filter { it.key.name.startsWith(prefix) }
                .associate { it.key.name.removePrefix(prefix) to it.value?.toString() }
        }
    }

    fun putStringSync(key: String, value: String?) {
        runBlocking {
            setValue(key, value)
        }
    }
}
