package com.example.panalsuite.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.panalsuite.model.Role
import com.example.panalsuite.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "session")

/** Stores the logged-in user locally so the session survives app restarts. */
class SessionManager(private val context: Context) {

    private object Keys {
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val ROLE = stringPreferencesKey("role")
    }

    val currentUser: Flow<User?> = context.sessionDataStore.data.map { preferences ->
        val email = preferences[Keys.EMAIL] ?: return@map null
        val role = runCatching { Role.valueOf(preferences[Keys.ROLE].orEmpty()) }
            .getOrDefault(Role.REQUESTER)
        User(email = email, name = preferences[Keys.NAME].orEmpty(), role = role)
    }

    suspend fun saveSession(user: User) {
        context.sessionDataStore.edit { preferences ->
            preferences[Keys.EMAIL] = user.email
            preferences[Keys.NAME] = user.name
            preferences[Keys.ROLE] = user.role.name
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences -> preferences.clear() }
    }
}
