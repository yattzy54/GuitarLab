package com.mmt.guitarlab.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.R
import com.mmt.guitarlab.data.AppLanguage
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBg
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioCardElevated
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel = hiltViewModel(),
) {
    val currentCode by viewModel.currentLanguageCode.collectAsStateWithLifecycle()
    val selectedCodeState by viewModel.selectedLanguageCode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val languages by viewModel.filteredLanguages.collectAsStateWithLifecycle()
    val activeCode = selectedCodeState ?: currentCode
    val isChanged = activeCode != currentCode

    LanguageContent(
        languages = languages,
        currentCode = currentCode,
        activeCode = activeCode,
        searchQuery = searchQuery,
        isChanged = isChanged,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSelectLanguage = viewModel::selectLanguage,
        onApplyLanguage = { viewModel.applyLanguage(activeCode) },
    )
}

@Composable
fun LanguageContent(
    languages: List<AppLanguage>,
    currentCode: String,
    activeCode: String,
    searchQuery: String,
    isChanged: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onApplyLanguage: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            LanguageHeader()

            Spacer(Modifier.height(16.dp))

            LanguageSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(languages, key = { it.code }) { lang ->
                    val isSelected = lang.code == activeCode
                    val isSavedCurrent = lang.code == currentCode

                    LanguageListItem(
                        lang = lang,
                        isSelected = isSelected,
                        isSavedCurrent = isSavedCurrent,
                        onClick = { onSelectLanguage(lang.code) },
                    )
                }
            }

            Spacer(Modifier.height(72.dp))
        }

        ApplyLanguageButton(
            isChanged = isChanged,
            onApply = onApplyLanguage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Composable
private fun LanguageHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Studio3DIconBadge(
            icon = Icons.Default.Language,
            contentDescription = "Language",
            size = 46.dp,
            accent = Studio3DAccent.TEAL,
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = stringResource(R.string.language_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = StudioTextPrimary,
            )
            Text(
                text = stringResource(R.string.language_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = ElectricTeal,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LanguageSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(stringResource(R.string.lang_search_placeholder), color = StudioTextMuted) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = StudioTextSecondary)
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StudioCardElevated,
            unfocusedContainerColor = StudioCardElevated,
            focusedBorderColor = ElectricAmber,
            unfocusedBorderColor = StudioCardBorder,
            focusedTextColor = StudioTextPrimary,
            unfocusedTextColor = StudioTextPrimary,
        ),
    )
}

@Composable
private fun LanguageListItem(
    lang: AppLanguage,
    isSelected: Boolean,
    isSavedCurrent: Boolean,
    onClick: () -> Unit,
) {
    val itemBg = if (isSelected) Color(0xFF382600) else StudioCardBg
    val borderColor = if (isSelected) ElectricAmber else StudioCardBorder

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(itemBg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = lang.flag,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 14.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lang.nameNative,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) ElectricAmber else StudioTextPrimary,
                )
                if (isSavedCurrent) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.current_active).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = ElectricTeal,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (lang.isRtl) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "RTL",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                text = "${lang.nameEnglish} (${lang.code})",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextSecondary,
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElectricAmber),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ApplyLanguageButton(
    isChanged: Boolean,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isChanged,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
        modifier = modifier,
    ) {
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = ElectricAmber,
                    spotColor = ElectricAmber,
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricAmber,
                contentColor = Color(0xFF1E1200),
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF1E1200),
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 6.dp),
                )
                Text(
                    text = stringResource(R.string.apply_language).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LanguageListItemPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LanguageListItem(
                lang = AppLanguage("ru", "Русский", "Russian", "🇷🇺"),
                isSelected = true,
                isSavedCurrent = true,
                onClick = {},
            )
            LanguageListItem(
                lang = AppLanguage("en", "English", "English", "🇬🇧"),
                isSelected = false,
                isSavedCurrent = false,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LanguageScreenPreview() {
    GuitarLabTheme {
        LanguageContent(
            languages = listOf(
                AppLanguage("ru", "Русский", "Russian", "🇷🇺"),
                AppLanguage("en", "English", "English", "🇬🇧"),
                AppLanguage("ar", "العربية", "Arabic", "🇸🇦", isRtl = true),
            ),
            currentCode = "en",
            activeCode = "ru",
            searchQuery = "",
            isChanged = true,
            onSearchQueryChange = {},
            onSelectLanguage = {},
            onApplyLanguage = {},
        )
    }
}