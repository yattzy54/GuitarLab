package com.mmt.guitarlab.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.languageDataStore by preferencesDataStore(name = "language_prefs")

data class AppLanguage(
    val code: String,
    val nameNative: String,
    val nameEnglish: String,
    val flag: String,
    val isRtl: Boolean = false,
)

object LanguageManager {
    val KEY_LANGUAGE = stringPreferencesKey("app_language_code")

    val SUPPORTED_LANGUAGES = listOf(
        AppLanguage("en", "English", "English", "🇺🇸"),
        AppLanguage("es", "Español", "Spanish", "🇪🇸"),
        AppLanguage("zh", "中文 (简体)", "Chinese Simplified", "🇨🇳"),
        AppLanguage("hi", "हिन्दी", "Hindi", "🇮🇳"),
        AppLanguage("ar", "العربية", "Arabic", "🇸🇦", isRtl = true),
        AppLanguage("fr", "Français", "French", "🇫🇷"),
        AppLanguage("pt", "Português", "Portuguese", "🇧🇷"),
        AppLanguage("ru", "Русский", "Russian", "🇷🇺"),
        AppLanguage("de", "Deutsch", "German", "🇩🇪"),
        AppLanguage("ja", "日本語", "Japanese", "🇯🇵"),
        AppLanguage("ko", "한국어", "Korean", "🇰🇷"),
        AppLanguage("it", "Italiano", "Italian", "🇮🇹"),
        AppLanguage("tr", "Türkçe", "Turkish", "🇹🇷"),
        AppLanguage("nl", "Nederlands", "Dutch", "🇳🇱"),
        AppLanguage("pl", "Polski", "Polish", "🇵🇱"),
        AppLanguage("in", "Bahasa Indonesia", "Indonesian", "🇮🇩"),
        AppLanguage("vi", "Tiếng Việt", "Vietnamese", "🇻🇳"),
        AppLanguage("uk", "Українська", "Ukrainian", "🇺🇦"),
        AppLanguage("sv", "Svenska", "Swedish", "🇸🇪"),
        AppLanguage("fa", "فارسی", "Persian", "🇮🇷", isRtl = true),
    )

    fun getInitialLanguageCode(context: Context): String {
        val deviceLang = Locale.getDefault().language.lowercase()
        return if (SUPPORTED_LANGUAGES.any { it.code == deviceLang }) deviceLang else "en"
    }

    fun getLanguageStream(context: Context): Flow<String> {
        return context.languageDataStore.data.map { prefs ->
            prefs[KEY_LANGUAGE] ?: getInitialLanguageCode(context)
        }
    }

    suspend fun setLanguage(context: Context, code: String) {
        context.languageDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = code
        }
    }
}
