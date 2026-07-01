package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

// Navigation Routes
const val AuthRoute = "auth"
const val HomeRoute = "home"
const val HostRoute = "host"
const val HistoryRoute = "history"
const val ProfileRoute = "profile"

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        com.example.utils.NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ParkzoneApp(viewModel)
            }
        }
    }
}

@Composable
fun ParkzoneApp(viewModel: MainViewModel) {
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val availableSpots by viewModel.availableSpots.collectAsState()
    val myBookings by viewModel.myBookings.collectAsState()
    val navController = rememberNavController()
    
    val startDestination = if (isUserLoggedIn) HomeRoute else AuthRoute

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isUserLoggedIn) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    if (userRole == com.example.model.UserRole.DRIVER) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Explore") },
                            label = { Text("Explore", style = MaterialTheme.typography.labelSmall) },
                            selected = currentDestination?.hierarchy?.any { it.route == HomeRoute } == true,
                            onClick = {
                                navController.navigate(HomeRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    if (userRole == com.example.model.UserRole.HOST) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Host") },
                            label = { Text("Host", style = MaterialTheme.typography.labelSmall) },
                            selected = currentDestination?.hierarchy?.any { it.route == HostRoute } == true,
                            onClick = {
                                navController.navigate(HostRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
                        label = { Text("History", style = MaterialTheme.typography.labelSmall) },
                        selected = currentDestination?.hierarchy?.any { it.route == HistoryRoute } == true,
                        onClick = {
                            viewModel.loadBookings()
                            navController.navigate(HistoryRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                        selected = currentDestination?.hierarchy?.any { it.route == ProfileRoute } == true,
                        onClick = {
                            navController.navigate(ProfileRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AuthRoute) {
                AuthScreen(
                    onSignInSuccess = {
                        viewModel.onSignInSuccess()
                        navController.navigate(HomeRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMockSignIn = { viewModel.authManager.mockSignIn() },
                    onGoogleSignIn = { context -> viewModel.authManager.signInWithGoogle(context) },
                    onEmailSignIn = { email, password -> viewModel.authManager.signInWithEmail(email, password) },
                    onEmailSignUp = { email, password -> viewModel.authManager.signUpWithEmail(email, password) }
                )
            }
            composable(HomeRoute) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val favoriteIds by viewModel.favoriteSpotIds.collectAsState()
                HomeScreen(
                    spots = availableSpots,
                    favoriteSpotIds = favoriteIds,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onBookSpot = { spot ->
                        viewModel.bookSpot(spot, durationHours = 2) {
                            com.example.utils.NotificationHelper.showExpirationAlert(context, spot.title)
                            viewModel.loadBookings()
                            navController.navigate(HistoryRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable(HostRoute) {
                HostScreen(
                    onAddSpot = { title, desc, price, lat, lng, ev, cov, sec, dyn ->
                        viewModel.addSpot(title, desc, price, lat, lng, ev, cov, sec, dyn) {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable(HistoryRoute) {
                HistoryScreen(bookings = myBookings)
            }
            composable(ProfileRoute) {
                ProfileScreen(
                    currentRole = userRole,
                    onRoleChange = { role ->
                        viewModel.switchRole(role)
                        if (role == com.example.model.UserRole.DRIVER) {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(HostRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onSignOut = {
                        viewModel.signOut()
                        navController.navigate(AuthRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

