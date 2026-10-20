package com.example.panalsuite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panalsuite.ui.login.LoginScreen
import com.example.panalsuite.ui.profile.ProfileScreen

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

/** Route used right after login. It will change to the inbox in the next step. */
val HOME_ROUTE = Screen.Profile.route

@Composable
fun AppNavGraph(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
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
    }
}
