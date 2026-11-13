package com.example.WeatherApp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.WeatherApp.ui.main.screens.DailyScreen
import com.example.WeatherApp.ui.main.screens.LocationsScreen
import com.example.WeatherApp.ui.main.screens.NowScreen
import android.util.Log

// The two screens in the bottom navigation bar: each has a route (its "address"),
// a label shown under the icon, and the icon itself
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Now : Screen("now", "Now", Icons.Filled.AccessTime)
    object Daily : Screen("daily", "Daily", Icons.Filled.CalendarMonth)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    // One ViewModel shared by all screens, so they all show the same weather data
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    // The navController moves between screens; the back stack entry tells us which is showing
    val navController = rememberNavController()
    val screens = listOf(Screen.Now, Screen.Daily)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Approximate location is enough for city-level weather (and is gentler on the emulator)
    val locationPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

    // Remembers whether the user refused, so we know when to send them to Settings instead
    var locationDenied by remember { mutableStateOf(false) }

    // Shows Android's permission dialog and tells us the answer
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            locationDenied = false
            viewModel.loadFromDeviceLocation()   // allowed: fetch the device's weather
        } else {
            locationDenied = true                // refused
        }
    }

    // Ask for permission once, when the app starts
    LaunchedEffect(Unit) { permissionLauncher.launch(locationPermissions) }

    Scaffold(
        // Top bar: current location name on the left, Locations button on the right
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        viewModel.locationName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis   // shows "..." if the name is too long
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("locations") }) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Locations")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        // Bottom bar: switch between the Now and Daily screens
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Avoid stacking up copies of the same screen
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        // The area between the bars, where the current screen is shown
        NavHost(
            navController = navController,
            startDestination = Screen.Now.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Now.route) { NowScreen(viewModel) }
            composable(Screen.Daily.route) { DailyScreen(viewModel) }
            composable("locations") {
                LocationsScreen(
                    viewModel = viewModel,
                    // Runs when the user taps "Tap to allow location access"
                    onRequestPermission = {
                        // Do we already have permission (e.g. granted earlier in Settings)?
                        val hasPermission = locationPermissions.any {
                            ContextCompat.checkSelfPermission(context, it) ==
                                    PackageManager.PERMISSION_GRANTED
                        }
                        Log.d("WeatherApp", "Row tapped: hasPermission=$hasPermission, denied=$locationDenied")  // NEW, temporary
                        when {
                            // Already allowed: just fetch the location
                            hasPermission -> viewModel.loadFromDeviceLocation()

                            locationDenied -> context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                            )

                            // Show the system permission dialog
                            else -> permissionLauncher.launch(locationPermissions)
                        }
                    },
                    // After picking a location, go back to the previous screen
                    onLocationChosen = { navController.popBackStack() }
                )
            }
        }
    }
}