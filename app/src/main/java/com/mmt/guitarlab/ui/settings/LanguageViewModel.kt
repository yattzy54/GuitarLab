package com.mmt.guitarlab.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.data.AppLanguage
import com.mmt.guitarlab.data.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val currentLanguageCode: StateFlow<String> = LanguageManager.getLanguageStream(context)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LanguageManager.getInitialLanguageCode(context))

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredLanguages: StateFlow<List<AppLanguage>> = combine(
        currentLanguageCode,
        searchQuery
    ) { _, query ->
        if (query.isBlank()) {
            LanguageManager.SUPPORTED_LANGUAGES
        } else {
            val q = query.trim().lowercase()
            LanguageManager.SUPPORTED_LANGUAGES.filter {
                it.nameNative.lowercase().contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.code.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LanguageManager.SUPPORTED_LANGUAGES)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            LanguageManager.setLanguage(context, code)
        }
    }
}
