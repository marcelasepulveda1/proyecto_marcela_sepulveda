package com.example.panalsuite.data.repository

import com.example.panalsuite.data.remote.PanalSuiteApi
import com.example.panalsuite.data.remote.dto.toDomain
import com.example.panalsuite.model.Role
import com.example.panalsuite.model.User
import kotlin.coroutines.cancellation.CancellationException

/** Demo account used only for the academic MVP. No real credentials are stored. */
data class DemoAccount(val user: User, val password: String)

/**
 * Authenticates against the REST API.
 * If the server cannot be reached, it falls back to the fictitious demo accounts.
 */
class AuthRepository(private val api: PanalSuiteApi) {

    private val demoAccounts = listOf(
        DemoAccount(User("solicitante@panalsuite.test", "Camila Rojas", Role.REQUESTER), DEMO_PASSWORD),
        DemoAccount(User("resolutor@panalsuite.test", "Diego Fuentes", Role.RESOLVER), DEMO_PASSWORD),
        DemoAccount(User("resolutor2@panalsuite.test", "Valentina Soto", Role.RESOLVER), DEMO_PASSWORD),
        DemoAccount(User("supervisor@panalsuite.test", "Andrea Muñoz", Role.SUPERVISOR), DEMO_PASSWORD)
    )

    suspend fun login(email: String, password: String): User? {
        val normalizedEmail = email.trim().lowercase()
        return try {
            api.findUser(normalizedEmail, password).firstOrNull()?.toDomain()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            loginOffline(normalizedEmail, password)
        }
    }

    private fun loginOffline(email: String, password: String): User? =
        demoAccounts.firstOrNull { account ->
            account.user.email.equals(email, ignoreCase = true) && account.password == password
        }?.user

    fun demoAccountFor(role: Role): DemoAccount = demoAccounts.first { it.user.role == role }

    fun users(): List<User> = demoAccounts.map { it.user }

    companion object {
        private const val DEMO_PASSWORD = "Demo1234"
    }
}
