package com.mmt.guitarlab.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TimeSignature
import com.mmt.guitarlab.domain.model.TrainerConfig
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import com.mmt.guitarlab.domain.repository.SettingsRepository
import com.mmt.guitarlab.domain.repository.TunerSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "guitarlab_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {

    private object Keys {
        val a4 = floatPreferencesKey("a4_hz")
        val metronome = stringPreferencesKey("metronome_json")
    }

    override val tunerSettings: Flow<TunerSettings> = context.dataStore.data.map { prefs: Preferences ->
        TunerSettings(a4Hz = prefs[Keys.a4] ?: 440f)
    }

    override val metronomeJson: Flow<String?> = context.dataStore.data.map { it[Keys.metronome] }

    override suspend fun setA4(hz: Float) {
        context.dataStore.edit { it[Keys.a4] = hz }
    }

    override suspend fun setMetronomeJson(json: String) {
        context.dataStore.edit { it[Keys.metronome] = json }
    }
}

object MetronomeConfigCodec {
    fun encode(config: MetronomeConfig): String = JSONObject().apply {
        put("bpm", config.bpm)
        put("ts", config.timeSignature.name)
        put("volume", config.volume.toDouble())
        put("trainer", JSONObject().apply {
            put("enabled", config.trainer.enabled)
            put("startBpm", config.trainer.startBpm)
            put("targetBpm", config.trainer.targetBpm)
            put("incrementBpm", config.trainer.incrementBpm)
            put("intervalKind", config.trainer.intervalKind.name)
            put("intervalValue", config.trainer.intervalValue)
        })
    }.toString()

    fun decode(raw: String?): MetronomeConfig {
        if (raw.isNullOrBlank()) return MetronomeConfig()
        return runCatching {
            val json = JSONObject(raw)
            val trainerJson = json.optJSONObject("trainer") ?: JSONObject()
            MetronomeConfig(
                bpm = json.optInt("bpm", 100),
                timeSignature = runCatching {
                    TimeSignature.valueOf(json.optString("ts", TimeSignature.FOUR_FOUR.name))
                }.getOrDefault(TimeSignature.FOUR_FOUR),
                volume = json.optDouble("volume", 0.85).toFloat(),
                trainer = TrainerConfig(
                    enabled = trainerJson.optBoolean("enabled", false),
                    startBpm = trainerJson.optInt("startBpm", 80),
                    targetBpm = trainerJson.optInt("targetBpm", 140),
                    incrementBpm = trainerJson.optInt("incrementBpm", 2),
                    intervalKind = runCatching {
                        TrainerIntervalKind.valueOf(
                            trainerJson.optString("intervalKind", TrainerIntervalKind.BARS.name),
                        )
                    }.getOrDefault(TrainerIntervalKind.BARS),
                    intervalValue = trainerJson.optInt("intervalValue", 4),
                ),
            )
        }.getOrDefault(MetronomeConfig())
    }
}
