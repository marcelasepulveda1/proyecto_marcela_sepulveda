package com.example.panalsuite.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.R
import com.example.panalsuite.model.TicketWorkflow
import com.example.panalsuite.model.User
import com.example.panalsuite.ui.create.CreateTicketScreen
import com.example.panalsuite.ui.detail.TicketDetailScreen
import com.example.panalsuite.ui.inbox.InboxScreen
import com.example.panalsuite.ui.login.LoginScreen
import com.example.panalsuite.ui.profile.ProfileScreen
import com.example.panalsuite.ui.summary.SummaryScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Inbox : Screen("inbox")
    data object CreateTicket : Screen("create_ticket")
    data object Summary : Screen("summary")
    data object Profile : Screen("profile")
    data object TicketDetail : Screen("ticket_detail/{ticketId}") {
        const val ARG_TICKET_ID = "ticketId"
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
}

/** Route used right after login. */
val HOME_ROUTE = Screen.Inbox.route

private data class BottomNavItem(
    val screen: Screen,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int
)

/** The bottom bar changes with the role: resolvers cannot create tickets, only supervisors see the summary. */
private fun bottomItemsFor(user: User?): List<BottomNavItem> {
    if (user == null) return emptyList()
    return buildList {
        add(BottomNavItem(Screen.Inbox, R.string.nav_inbox, R.drawable.ic_nav_inbox))
        if (TicketWorkflow.canCreateTickets(user)) {
            add(BottomNavItem(Screen.CreateTicket, R.string.nav_create, R.drawable.ic_nav_add))
        }
        if (TicketWorkflow.canSeeSummary(user)) {
            add(BottomNavItem(Screen.Summary, R.string.nav_summary, R.drawable.ic_nav_summary))
        }
        add(BottomNavItem(Screen.Profile, R.string.nav_profile, R.drawable.ic_nav_profile))
    }
}

@Composable
fun AppNavGraph(
    startDestination: String,
    currentUser: User?,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomItems = bottomItemsFor(currentUser)
    val showBottomBar = bottomItems.any { it.screen.route == currentRoute }

    Scaffold(
        // Every screen draws its own header under the status bar.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = { navController.navigateToTab(item.screen.route) },
                            icon = { Icon(painterResource(item.iconRes), contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Inbox.route) {
                InboxScreen(
                    onTicketClick = { ticketId ->
                        navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                    }
                )
            }

            composable(Screen.CreateTicket.route) {
                CreateTicketScreen(
                    onTicketCreated = { ticketId ->
                        navController.navigate(Screen.TicketDetail.createRoute(ticketId)) {
                            popUpTo(Screen.Inbox.route)
                        }
                    }
                )
            }

            composable(Screen.Summary.route) {
                SummaryScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.TicketDetail.route,
                arguments = listOf(navArgument(Screen.TicketDetail.ARG_TICKET_ID) { type = NavType.StringType })
            ) {
                TicketDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

/** Standard bottom navigation behavior: one copy of each tab, keeping its state. */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Screen.Inbox.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
