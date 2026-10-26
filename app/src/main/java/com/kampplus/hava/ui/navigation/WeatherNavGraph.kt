package com.kampplus.hava.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kampplus.hava.ui.screens.BioCoachScreen
import com.kampplus.hava.ui.screens.DetailScreen
import com.kampplus.hava.ui.screens.HomeScreen
import com.kampplus.hava.ui.screens.SimulationScreen
import com.kampplus.hava.ui.screens.TrendScreen
import com.kampplus.hava.ui.viewmodel.WeatherViewModel

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun WeatherNavGraph(viewModel: WeatherViewModel = viewModel()) {
    val navController = rememberNavController()
    val loadState by viewModel.state.collectAsStateWithLifecycle()
    val metropolises by viewModel.metropolisesState.collectAsStateWithLifecycle()

    val items = listOf(
        BottomNavItem("home", "Dashboard", Icons.Default.WbSunny),
        BottomNavItem("bio_coach", "Bio-Coach", Icons.Default.Lightbulb),
        BottomNavItem("simulation", "Simülasyon", Icons.Default.Science),
        BottomNavItem("trends", "Trendler", Icons.Default.Insights)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show BottomBar only on main 4 tabs, hide on detail screen
    val showBottomBar = currentRoute in items.map { item -> item.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 8.dp,
                        shadowElevation = 16.dp
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            items.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(if (selected) 26.dp else 22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    selected = selected,
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF4F46E5),
                                        unselectedIconColor = Color(0xFF64748B),
                                        selectedTextColor = Color(0xFF4F46E5),
                                        unselectedTextColor = Color(0xFF64748B),
                                        indicatorColor = Color(0xFF4F46E5).copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    state = loadState,
                    metropolises = metropolises,
                    onSearch = { query -> viewModel.searchCity(query) },
                    onSelectCity = { city ->
                        viewModel.selectCity(city) {
                            navController.navigate("detail")
                        }
                    },
                    onQuickSelect = { city ->
                        viewModel.selectCity(city) {
                            navController.navigate("detail")
                        }
                    }
                )
            }
            composable("bio_coach") {
                BioCoachScreen(
                    state = loadState,
                    onBack = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
                )
            }
            composable("simulation") {
                SimulationScreen()
            }
            composable("trends") {
                TrendScreen(state = loadState)
            }
            composable("detail") {
                DetailScreen(
                    state = loadState,
                    onBack = { navController.popBackStack() },
                    onRetry = { viewModel.refreshCurrentWeather() },
                    onNavigateToBioCoach = { navController.navigate("bio_coach") }
                )
            }
        }
    }
}
