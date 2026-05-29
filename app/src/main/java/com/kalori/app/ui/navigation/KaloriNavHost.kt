package com.kalori.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kalori.app.ui.screens.AddFoodScreen
import com.kalori.app.ui.screens.SettingsScreen
import com.kalori.app.ui.screens.StatsScreen
import com.kalori.app.ui.screens.TodayScreen
import com.kalori.app.ui.screens.TrendScreen

/**
 * Single NavHost for the app. Route stubs render placeholder composables from fakes.
 * The bottom bar carries the three primary screens; the FAB opens Add-food.
 */
@Composable
fun KaloriNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    val showBottomBar = TopLevelDestination.entries.any { dest ->
        currentRoute?.hierarchy?.any { it.route == dest.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { dest ->
                        val selected = currentRoute?.hierarchy?.any { it.route == dest.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(iconFor(dest), contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute?.route == Routes.TODAY) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_FOOD) },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Logg") },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TODAY,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.TODAY) {
                TodayScreen(
                    onAddFood = { navController.navigate(Routes.ADD_FOOD) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.ADD_FOOD) {
                AddFoodScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.STATS) { StatsScreen() }
            composable(Routes.TREND) { TrendScreen() }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun iconFor(dest: TopLevelDestination) = when (dest) {
    TopLevelDestination.TODAY -> Icons.Outlined.Today
    TopLevelDestination.STATS -> Icons.Outlined.BarChart
    TopLevelDestination.TREND -> Icons.AutoMirrored.Outlined.ShowChart
}
