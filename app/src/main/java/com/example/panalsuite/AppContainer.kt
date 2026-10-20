package com.example.panalsuite

import android.app.Application
import android.content.Context
import com.example.panalsuite.data.local.TicketLocalDataSource
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.remote.ApiClient
import com.example.panalsuite.data.repository.AuthRepository
import com.example.panalsuite.data.repository.TicketRepository

/** Simple manual dependency container shared by the whole app. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val sessionManager = SessionManager(appContext)
    val authRepository = AuthRepository(ApiClient.api)
    val ticketRepository = TicketRepository(ApiClient.api, TicketLocalDataSource(appContext))
}

class PanalSuiteApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
