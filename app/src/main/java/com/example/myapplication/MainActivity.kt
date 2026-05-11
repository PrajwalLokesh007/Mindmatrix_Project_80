package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AuthState
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.ReportViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val authViewModel: AuthViewModel = viewModel()
                val reportViewModel: ReportViewModel = viewModel()
                AppNavigation(authViewModel, reportViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel, reportViewModel: ReportViewModel) {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    val authState by authViewModel.authState.collectAsState()

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
    } else {
        val startDestination = if (authViewModel.currentUser != null) "main" else "login"
        
        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterNavigate = {
                        navController.navigate("register")
                    },
                    viewModel = authViewModel
                )
            }
            composable("register") {
                RegisterScreen(
                    onBackToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    viewModel = authViewModel
                )
            }
            composable("main") {
                MainScaffold(authViewModel, reportViewModel)
            }
        }
    }
}

@Composable
fun MainScaffold(authViewModel: AuthViewModel, reportViewModel: ReportViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = currentDestination == destination.route,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = AppDestinations.DASHBOARD.route,
            modifier = Modifier.padding()
        ) {
            composable(AppDestinations.DASHBOARD.route) { DashboardScreen(navController, reportViewModel) }
            composable(AppDestinations.REPORT.route) { ReportWasteScreen(reportViewModel, authViewModel) }
            composable(AppDestinations.MAPS.route) { MapScreen(reportViewModel) }
            composable(AppDestinations.REWARDS.route) { RewardsProfileScreen() }
            composable(AppDestinations.VOLUNTEER.route) { VolunteerScreen(reportViewModel) }
        }
    }
}

enum class AppDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD("dashboard", "Home", Icons.Default.Dashboard),
    REPORT("report", "Report", Icons.Default.Report),
    MAPS("maps", "Map", Icons.Default.Map),
    REWARDS("rewards", "Eco Score", Icons.Default.EmojiEvents),
    VOLUNTEER("volunteer", "Volunteer", Icons.Default.VolunteerActivism),
}
