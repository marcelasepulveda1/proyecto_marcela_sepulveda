package com.example.panalsuite.util

import androidx.annotation.StringRes
import com.example.panalsuite.R

/**
 * Reusable form validations.
 * Each function returns the string resource of the error, or null when the value is valid.
 */
object Validators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    const val MIN_PASSWORD_LENGTH = 8

    @StringRes
    fun validateEmail(email: String): Int? = when {
        email.isBlank() -> R.string.error_email_required
        !EMAIL_REGEX.matches(email.trim()) -> R.string.error_email_invalid
        else -> null
    }

    @StringRes
    fun validatePassword(password: String): Int? = when {
        password.isBlank() -> R.string.error_password_required
        password.length < MIN_PASSWORD_LENGTH -> R.string.error_password_length
        password.none { it.isDigit() } -> R.string.error_password_digit
        else -> null
    }
}
