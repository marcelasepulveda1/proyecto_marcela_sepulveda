package com.example.panalsuite

import android.app.Application
import android.content.Context
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.repository.AuthRepository

/** Simple manual dependency container shared by the whole app. */
class AppContainer(context: Context) {
    val sessionManager = SessionManager(context.applicationContext)
    val authRepository = AuthRepository()
}

class PanalSuiteApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
