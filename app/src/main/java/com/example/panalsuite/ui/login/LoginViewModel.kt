package com.example.panalsuite.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.panalsuite.PanalSuiteApp
import com.example.panalsuite.R
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.repository.AuthRepository
import com.example.panalsuite.model.Role
import com.example.panalsuite.util.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    @param:StringRes val emailError: Int? = null,
    @param:StringRes val passwordError: Int? = null,
    @param:StringRes val loginError: Int? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
            emailError == null && passwordError == null && !isLoading
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (email.isEmpty()) null else Validators.validateEmail(email),
                loginError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (password.isEmpty()) null else Validators.validatePassword(password),
                loginError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun fillDemoAccount(role: Role) {
        val account = authRepository.demoAccountFor(role)
        _uiState.update {
            it.copy(
                email = account.user.email,
                password = account.password,
                emailError = null,
                passwordError = null,
                loginError = null
            )
        }
    }

    fun login() {
        val state = _uiState.value
        val emailError = Validators.validateEmail(state.email)
        val passwordError = Validators.validatePassword(state.password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            delay(SIMULATED_NETWORK_DELAY_MS)
            val user = authRepository.login(state.email, state.password)
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, loginError = R.string.error_invalid_credentials) }
            } else {
                sessionManager.saveSession(user)
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            }
        }
    }

    companion object {
        private const val SIMULATED_NETWORK_DELAY_MS = 600L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PanalSuiteApp
                LoginViewModel(app.container.authRepository, app.container.sessionManager)
            }
        }
    }
}
