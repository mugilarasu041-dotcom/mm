package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonthlyProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TrackerScreen
import com.example.ui.screens.YearlyProgressScreen
import com.example.ui.theme.WinterArcTheme
import com.example.viewmodel.TrackerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WinterArcTheme {
                WinterArcApp(viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Tracker : Screen("tracker", "Tracker", Icons.Default.GridOn)
    object Monthly : Screen("monthly", "Monthly", Icons.Default.BarChart)
    object Yearly : Screen("yearly", "Yearly", Icons.Default.CalendarMonth)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun WinterArcApp(viewModel: TrackerViewModel) {
    val navController = rememberNavController()

    val currentTracker by viewModel.trackerData.collectAsStateWithLifecycle()
    val allTrackers by viewModel.allTrackers.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val currentSaveState by viewModel.saveState.collectAsStateWithLifecycle()

    val items = listOf(
        Screen.Home,
        Screen.Tracker,
        Screen.Monthly,
        Screen.Yearly,
        Screen.History,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.onSurface)
                    .testTag("bottom_navigation_bar")
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.surface,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.onSurface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    currentTracker = currentTracker,
                    onNavigateToTracker = { navController.navigate(Screen.Tracker.route) },
                    onNavigateToMonthlyProgress = { navController.navigate(Screen.Monthly.route) },
                    onNavigateToYearlyProgress = { navController.navigate(Screen.Yearly.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.Tracker.route) {
                TrackerScreen(
                    tracker = currentTracker,
                    saveState = currentSaveState,
                    onProfileNameChange = { viewModel.updateProfileName(it) },
                    onYearMonthSelected = { yr, mth -> viewModel.selectYearAndMonth(yr, mth) },
                    onHabitNameChange = { id, name -> viewModel.updateHabitName(id, name) },
                    onDayToggle = { id, day -> viewModel.toggleHabitDay(id, day) },
                    onSleepSelect = { day, hrs -> viewModel.setSleepForDay(day, hrs) },
                    onGoalsChange = { viewModel.updateGoals(it) },
                    onAchievementsChange = { viewModel.updateAchievements(it) },
                    onImprovementsChange = { viewModel.updateImprovements(it) }
                )
            }

            composable(Screen.Monthly.route) {
                MonthlyProgressScreen(
                    tracker = currentTracker
                )
            }

            composable(Screen.Yearly.route) {
                YearlyProgressScreen(
                    allTrackers = allTrackers,
                    selectedYear = selectedYear,
                    onYearSelected = { yr -> viewModel.selectYearAndMonth(yr, selectedMonth) },
                    onMonthTrackerClicked = { yr, mth ->
                        viewModel.selectYearAndMonth(yr, mth)
                        navController.navigate(Screen.Tracker.route)
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    allTrackers = allTrackers,
                    onSelectTracker = { yr, mth ->
                        viewModel.selectYearAndMonth(yr, mth)
                        navController.navigate(Screen.Tracker.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    profileName = currentTracker.profileName,
                    onProfileNameChange = { viewModel.updateProfileName(it) },
                    onExportJsonRequested = { onReady -> viewModel.exportDataJson(onReady) },
                    onImportJsonSubmitted = { jsonStr, onResult -> viewModel.importDataJson(jsonStr, onResult) },
                    onResetAllDataRequested = { onComplete -> viewModel.resetAllData(onComplete) }
                )
            }
        }
    }
}
