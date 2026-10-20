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

    @Test
    fun shortTitle_returnsShortError() {
        assertEquals(R.string.error_title_short, Validators.validateTitle("Caja"))
    }

    @Test
    fun validTitle_returnsNoError() {
        assertNull(Validators.validateTitle("Caja 3 no imprime boletas"))
    }

    @Test
    fun emptyDescription_returnsRequiredError() {
        assertEquals(R.string.error_description_required, Validators.validateDescription("   "))
    }

    @Test
    fun shortDescription_returnsShortError() {
        assertEquals(R.string.error_description_short, Validators.validateDescription("No funciona"))
    }

    @Test
    fun missingSelection_returnsGivenError() {
        assertEquals(
            R.string.error_category_required,
            Validators.validateSelection(null, R.string.error_category_required)
        )
        assertNull(Validators.validateSelection("Sucursal Alameda", R.string.error_branch_required))
    }
}
