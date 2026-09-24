package com.mmt.guitarlab.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mmt.guitarlab.config.AppFlavorConfig
import com.mmt.guitarlab.config.FlavorType
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.drums.DrumsScreen
import com.mmt.guitarlab.ui.metronome.AutoSpeedTrainerScreen
import com.mmt.guitarlab.ui.metronome.MetronomeScreen
import com.mmt.guitarlab.ui.practice.ChordScaleScreen
import com.mmt.guitarlab.ui.practice.PracticeTrackerScreen
import com.mmt.guitarlab.ui.practice.ReverseChordFinderScreen
import com.mmt.guitarlab.ui.practice.RiffRecorderScreen
import com.mmt.guitarlab.ui.practice.SlowDownerScreen
import com.mmt.guitarlab.ui.guitartabedit.GuitarTabEditScreen
import com.mmt.guitarlab.ui.guitartabedit.TuxGuitarReaderScreen
import com.mmt.guitarlab.ui.guitartabedit.TuxGuitarScreen
import com.mmt.guitarlab.ui.settings.LanguageScreen
import com.mmt.guitarlab.ui.tab.TabEditorScreen
import com.mmt.guitarlab.ui.tab.TabViewerScreen
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import com.mmt.guitarlab.ui.tuner.TunerScreen
import kotlinx.coroutines.launch

import androidx.compose.ui.res.stringResource
import com.mmt.guitarlab.R

enum class AppDest(
    val route: String,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badge: String? = null,
    val isTabFeature: Boolean = false,
) {
    Tabs("tabs", R.string.tab_tabs, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic, isTabFeature = true),
    GuitarTabEdit("guitartabedit", R.string.tab_guitartabedit, Icons.Filled.Edit, Icons.Outlined.Edit, "NEW", isTabFeature = true),
    TuxGuitar("tuxguitar", R.string.tab_tuxguitar, Icons.Filled.MusicNote, Icons.Outlined.MusicNote, "TG", isTabFeature = true),
    TuxGuitarReader("tuxguitar_reader", R.string.tab_tuxguitar_reader, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic, "VIEW", isTabFeature = true),
    TabEditor("tab_editor", R.string.tab_tab_editor, Icons.Filled.Tune, Icons.Outlined.Tune, "GP5", isTabFeature = true),
    Tuner("tuner", R.string.tab_tuner, Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq),
    Metronome("metronome", R.string.tab_metronome, Icons.Filled.Timer, Icons.Outlined.Timer),
    Drums("drums", R.string.tab_drums, Icons.Filled.Album, Icons.Outlined.Album),
    Fretboard("fretboard", R.string.tab_fretboard, Icons.Filled.GridOn, Icons.Outlined.GridOn),
    ReverseChord("reverse_chord", R.string.tab_chords, Icons.Filled.Search, Icons.Outlined.Search),
    Trainer("trainer", R.string.tab_trainer, Icons.Filled.Speed, Icons.Outlined.Speed),
    SlowDowner("slowdowner", R.string.tab_slowdowner, Icons.Filled.SlowMotionVideo, Icons.Outlined.SlowMotionVideo),
    Recorder("recorder", R.string.tab_recorder, Icons.Filled.Mic, Icons.Outlined.Mic),
    Tracker("tracker", R.string.tab_tracker, Icons.Filled.Timeline, Icons.Outlined.Timeline),
    Language("language", R.string.tab_language, Icons.Filled.Language, Icons.Outlined.Language),
    ;

    companion object {
        fun availableDestinations(isTabsFlavor: Boolean = AppFlavorConfig.isTabsFlavor): List<AppDest> {
            return if (isTabsFlavor) {
                // В новом флейворе показывай только эти 3 раздела (Табы, GuitarTabEdit, TabLab)
                entries.filter { it.isTabFeature }
            } else {
                // В основном флейворе скрой эти 3 раздела (Табы, GuitarTabEdit, TabLab)
                entries.filter { !it.isTabFeature }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuitarLabApp() {
    var isTabsFlavor by remember { mutableStateOf(AppFlavorConfig.isTabsFlavor) }
    val availableDests = remember(isTabsFlavor) { AppDest.availableDestinations(isTabsFlavor) }
    val defaultStartDest = remember(isTabsFlavor) {
        if (isTabsFlavor) AppDest.Tabs.route else AppDest.Tuner.route
    }

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: defaultStartDest
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentDest = availableDests.find { it.route == currentRoute } ?: availableDests.firstOrNull() ?: AppDest.Tuner

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = StudioDarkBg,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    // Studio Header with 3D Brand Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp),
                    ) {
                        Studio3DIconBadge(
                            icon = if (isTabsFlavor) Icons.Default.LibraryMusic else Icons.Default.MusicNote,
                            contentDescription = null,
                            size = 46.dp,
                            accent = if (isTabsFlavor) Studio3DAccent.AMBER else Studio3DAccent.TEAL,
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isTabsFlavor) "TabLab Studio" else "GuitarLab Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = StudioTextPrimary,
                            )
                            Text(
                                text = if (isTabsFlavor) "Tabs & Scores Suite" else stringResource(R.string.nav_studio_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isTabsFlavor) ElectricAmber else ElectricTeal,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    HorizontalDivider(
                        color = StudioCardBorder,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    // Core Navigation Items
                    Text(
                        text = if (isTabsFlavor) "TAB SUITE" else stringResource(R.string.nav_core_instruments),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                    )

                    availableDests.forEach { dest ->
                        val selected = currentRoute == dest.route
                        val destTitle = stringResource(dest.titleRes)
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = destTitle,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) ElectricAmber else StudioTextPrimary,
                                    )
                                    if (dest.badge != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ElectricAmber.copy(alpha = 0.2f))
                                                .border(1.dp, ElectricAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = dest.badge,
                                                color = ElectricAmber,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            },
                            selected = selected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (!selected) {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = destTitle,
                                    tint = if (selected) ElectricAmber else StudioTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF382600),
                                unselectedContainerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(vertical = 3.dp),
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(
                        color = StudioCardBorder,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )

                    // Flavor Mode Information / Switcher Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF161B26))
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "FLAVOR: ${if (isTabsFlavor) "TABS" else "MAIN"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isTabsFlavor) ElectricAmber else ElectricTeal,
                            )
                            Text(
                                text = if (isTabsFlavor) "Showing 3 tab sections" else "3 tab sections hidden",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = StudioTextMuted,
                            )
                        }
                    }
                }
            }
        },
    ) {
        val isTabsScreen = isTabsFlavor && (
            currentRoute == AppDest.Tabs.route ||
                currentRoute == AppDest.GuitarTabEdit.route ||
                currentRoute == AppDest.TuxGuitar.route
                || currentRoute == AppDest.TuxGuitarReader.route
            )
        Scaffold(
            topBar = {
                if (!isTabsScreen) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(currentDest.titleRes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Menu",
                                    tint = ElectricAmber,
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = StudioDarkBg,
                        ),
                    )
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = defaultStartDest,
                modifier = Modifier.padding(if (isTabsScreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else padding),
            ) {
                composable(AppDest.Tabs.route) { 
                    TabViewerScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateToEditor = {
                            navController.navigate(AppDest.TabEditor.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(AppDest.GuitarTabEdit.route) { GuitarTabEditScreen() }
                composable(AppDest.TuxGuitar.route) { TuxGuitarScreen() }
                composable(AppDest.TuxGuitarReader.route) { TuxGuitarReaderScreen() }
                composable(AppDest.Drums.route) { DrumsScreen() }
                composable(AppDest.Tuner.route) { TunerScreen() }
                composable(AppDest.Metronome.route) { MetronomeScreen() }
                composable(AppDest.Trainer.route) { AutoSpeedTrainerScreen() }
                composable(AppDest.TabEditor.route) { TabEditorScreen() }
                composable(AppDest.Fretboard.route) { ChordScaleScreen() }
                composable(AppDest.ReverseChord.route) { ReverseChordFinderScreen() }
                composable(AppDest.SlowDowner.route) { SlowDownerScreen() }
                composable(AppDest.Recorder.route) { RiffRecorderScreen() }
                composable(AppDest.Tracker.route) { PracticeTrackerScreen() }
                composable(AppDest.Language.route) { LanguageScreen() }
            }
        }
    }
}
