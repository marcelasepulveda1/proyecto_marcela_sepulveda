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

    const val TITLE_MIN_LENGTH = 5
    const val TITLE_MAX_LENGTH = 60
    const val DESCRIPTION_MIN_LENGTH = 15
    const val DESCRIPTION_MAX_LENGTH = 400

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

    @StringRes
    fun validateTitle(title: String): Int? = when {
        title.isBlank() -> R.string.error_title_required
        title.trim().length < TITLE_MIN_LENGTH -> R.string.error_title_short
        title.length > TITLE_MAX_LENGTH -> R.string.error_title_long
        else -> null
    }

    @StringRes
    fun validateDescription(description: String): Int? = when {
        description.isBlank() -> R.string.error_description_required
        description.trim().length < DESCRIPTION_MIN_LENGTH -> R.string.error_description_short
        description.length > DESCRIPTION_MAX_LENGTH -> R.string.error_description_long
        else -> null
    }

    /** Used by chip selectors (category, branch): the user must pick one option. */
    @StringRes
    fun validateSelection(value: Any?, @StringRes errorRes: Int): Int? =
        if (value == null) errorRes else null
}
