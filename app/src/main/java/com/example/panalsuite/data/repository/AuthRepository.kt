package com.example.panalsuite.data.repository

import com.example.panalsuite.model.Role
import com.example.panalsuite.model.User

/** Demo account used only for the academic MVP. No real credentials are stored. */
data class DemoAccount(val user: User, val password: String)

/**
 * Fake authentication with fictitious users.
 * It can be replaced later by a REST API without changing the screens.
 */
class AuthRepository {

    private val demoAccounts = listOf(
        DemoAccount(User("solicitante@panalsuite.test", "Camila Rojas", Role.REQUESTER), DEMO_PASSWORD),
        DemoAccount(User("resolutor@panalsuite.test", "Diego Fuentes", Role.RESOLVER), DEMO_PASSWORD),
        DemoAccount(User("resolutor2@panalsuite.test", "Valentina Soto", Role.RESOLVER), DEMO_PASSWORD),
        DemoAccount(User("supervisor@panalsuite.test", "Andrea Muñoz", Role.SUPERVISOR), DEMO_PASSWORD)
    )

    fun login(email: String, password: String): User? =
        demoAccounts.firstOrNull { account ->
            account.user.email.equals(email.trim(), ignoreCase = true) && account.password == password
        }?.user

    fun demoAccountFor(role: Role): DemoAccount = demoAccounts.first { it.user.role == role }

    fun users(): List<User> = demoAccounts.map { it.user }

    companion object {
        private const val DEMO_PASSWORD = "Demo1234"
    }
}
