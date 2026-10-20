package com.example.panalsuite

import com.example.panalsuite.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun validEmail_returnsNoError() {
        assertNull(Validators.validateEmail("resolutor@panalsuite.test"))
    }

    @Test
    fun emptyEmail_returnsRequiredError() {
        assertEquals(R.string.error_email_required, Validators.validateEmail(""))
    }

    @Test
    fun malformedEmail_returnsInvalidError() {
        assertEquals(R.string.error_email_invalid, Validators.validateEmail("resolutor@panalsuite"))
    }

    @Test
    fun shortPassword_returnsLengthError() {
        assertEquals(R.string.error_password_length, Validators.validatePassword("Demo1"))
    }

    @Test
    fun passwordWithoutDigit_returnsDigitError() {
        assertEquals(R.string.error_password_digit, Validators.validatePassword("DemoDemo"))
    }

    @Test
    fun validPassword_returnsNoError() {
        assertNull(Validators.validatePassword("Demo1234"))
    }
}
