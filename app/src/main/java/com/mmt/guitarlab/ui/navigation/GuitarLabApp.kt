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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Mic
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.metronome.AutoSpeedTrainerScreen
import com.mmt.guitarlab.ui.metronome.MetronomeScreen
import com.mmt.guitarlab.ui.practice.ChordScaleScreen
import com.mmt.guitarlab.ui.practice.ReverseChordFinderScreen
import com.mmt.guitarlab.ui.practice.PracticeTrackerScreen
import com.mmt.guitarlab.ui.practice.RiffRecorderScreen
import com.mmt.guitarlab.ui.practice.SlowDownerScreen
import com.mmt.guitarlab.ui.tab.TabViewerScreen
import com.mmt.guitarlab.ui.tab.TabEditorScreen
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import com.mmt.guitarlab.ui.tuner.TunerScreen
import kotlinx.coroutines.launch

enum class AppDest(
    val route: String,
    val title: String,
    val shortLabel: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Tabs("tabs", "Табы", "Табы", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
    TabEditor("tab_editor", "Tab Editor", "Редактор", Icons.Filled.Tune, Icons.Outlined.Tune),
    Tuner("tuner", "Guitar Tuner", "Тюнер", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq),
    Metronome("metronome", "Metronome", "Метроном", Icons.Filled.Timer, Icons.Outlined.Timer),
    Fretboard("fretboard", "Гриф & Аккорды", "Гриф", Icons.Filled.GridOn, Icons.Outlined.GridOn),
    ReverseChord("reverse_chord", "Reverse Chord Finder", "Аккорды", Icons.Filled.Search, Icons.Outlined.Search),
    Trainer("trainer", "Auto-Speed Trainer", "Тренер", Icons.Filled.Speed, Icons.Outlined.Speed),
    SlowDowner("slowdowner", "Audio Slow-Downer", "Плеер", Icons.Filled.SlowMotionVideo, Icons.Outlined.SlowMotionVideo),
    Recorder("recorder", "Riff Quick Recorder", "Диктофон", Icons.Filled.Mic, Icons.Outlined.Mic),
    Tracker("tracker", "Practice Tracker", "Трекер", Icons.Filled.Timeline, Icons.Outlined.Timeline),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuitarLabApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: AppDest.Tabs.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentDest = AppDest.entries.find { it.route == currentRoute } ?: AppDest.Tabs

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
                            icon = Icons.Default.MusicNote,
                            contentDescription = null,
                            size = 46.dp,
                            accent = Studio3DAccent.AMBER,
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "GuitarLab Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = StudioTextPrimary,
                            )
                            Text(
                                text = "Pro Guitarist Toolkit",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricTeal,
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
                        text = "CORE INSTRUMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                    )

                    AppDest.entries.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = dest.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) ElectricAmber else StudioTextPrimary,
                                )
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
                                    contentDescription = dest.title,
                                    tint = if (selected) ElectricAmber else StudioTextSecondary,
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
                }
            }
        },
    ) {
        val isTabsScreen = currentRoute == AppDest.Tabs.route

        Scaffold(
            topBar = {
                if (!isTabsScreen) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = currentDest.title,
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
                startDestination = AppDest.Tabs.route,
                modifier = Modifier.padding(if (isTabsScreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else padding),
            ) {
                composable(AppDest.Tuner.route) { TunerScreen() }
                composable(AppDest.Metronome.route) { MetronomeScreen() }
                composable(AppDest.Trainer.route) { AutoSpeedTrainerScreen() }
                composable(AppDest.Tabs.route) { 
                    TabViewerScreen(onOpenDrawer = { scope.launch { drawerState.open() } })
                }
                composable(AppDest.TabEditor.route) { TabEditorScreen() }
                composable(AppDest.Fretboard.route) { ChordScaleScreen() }
                composable(AppDest.ReverseChord.route) { ReverseChordFinderScreen() }
                composable(AppDest.SlowDowner.route) { SlowDownerScreen() }
                composable(AppDest.Recorder.route) { RiffRecorderScreen() }
                composable(AppDest.Tracker.route) { PracticeTrackerScreen() }
            }
        }
    }
}
