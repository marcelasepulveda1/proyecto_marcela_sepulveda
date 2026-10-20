package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.model.User
import com.example.panalsuite.ui.navigation.AppNavGraph
import com.example.panalsuite.ui.navigation.HOME_ROUTE
import com.example.panalsuite.ui.navigation.Screen
import com.example.panalsuite.ui.theme.PanalSuiteTheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = (application as PanalSuiteApp).container.sessionManager
        setContent {
            PanalSuiteTheme {
                PanalSuiteRoot(sessionManager)
            }
        }
    }
}

private sealed interface SessionState {
    data object Loading : SessionState
    data class Ready(val user: User?) : SessionState
}

@Composable
private fun PanalSuiteRoot(sessionManager: SessionManager) {
    val sessionFlow = remember(sessionManager) {
        sessionManager.currentUser.map<User?, SessionState> { SessionState.Ready(it) }
    }
    val sessionState by sessionFlow.collectAsState(initial = SessionState.Loading)

    when (val state = sessionState) {
        SessionState.Loading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is SessionState.Ready -> {
            // The start destination is decided only once, when the saved session is first read.
            val startDestination = remember {
                if (state.user != null) HOME_ROUTE else Screen.Login.route
            }
            AppNavGraph(startDestination = startDestination, currentUser = state.user)
        }
    }
}
