package com.mmt.guitarlab.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mmt.guitarlab.ui.metronome.AutoSpeedTrainerScreen
import com.mmt.guitarlab.ui.metronome.MetronomeScreen
import com.mmt.guitarlab.ui.practice.ChordScaleScreen
import com.mmt.guitarlab.ui.practice.PracticeTrackerScreen
import com.mmt.guitarlab.ui.practice.RiffRecorderScreen
import com.mmt.guitarlab.ui.practice.SlowDownerScreen
import com.mmt.guitarlab.ui.tab.TabViewerScreen
import com.mmt.guitarlab.ui.tuner.TunerScreen
import kotlinx.coroutines.launch

enum class AppDest(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Tuner("tuner", "Guitar Tuner", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq),
    Metronome("metronome", "Metronome", Icons.Filled.Timer, Icons.Outlined.Timer),
    Trainer("trainer", "Auto-Speed Trainer", Icons.Filled.Speed, Icons.Outlined.Speed),
    Tabs("tabs", "Tablature & MIDI Suite", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
    Fretboard("fretboard", "Fretboard & Chords", Icons.Filled.GridOn, Icons.Outlined.GridOn),
    SlowDowner("slowdowner", "Audio Slow-Downer", Icons.Filled.SlowMotionVideo, Icons.Outlined.SlowMotionVideo),
    Recorder("recorder", "Riff Quick Recorder", Icons.Filled.Mic, Icons.Outlined.Mic),
    Tracker("tracker", "Session Tracker", Icons.Filled.Timeline, Icons.Outlined.Timeline),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuitarLabApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: AppDest.Tuner.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDest = AppDest.entries.find { it.route == currentRoute } ?: AppDest.Tuner

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Guitar Lab",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                    AppDest.entries.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = dest.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
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
                                )
                            },
                            modifier = Modifier.padding(vertical = 2.dp),
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = currentDest.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Menu",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = AppDest.Tuner.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(AppDest.Tuner.route) { TunerScreen() }
                composable(AppDest.Metronome.route) { MetronomeScreen() }
                composable(AppDest.Trainer.route) { AutoSpeedTrainerScreen() }
                composable(AppDest.Tabs.route) { TabViewerScreen() }
                composable(AppDest.Fretboard.route) { ChordScaleScreen() }
                composable(AppDest.SlowDowner.route) { SlowDownerScreen() }
                composable(AppDest.Recorder.route) { RiffRecorderScreen() }
                composable(AppDest.Tracker.route) { PracticeTrackerScreen() }
            }
        }
    }
}
