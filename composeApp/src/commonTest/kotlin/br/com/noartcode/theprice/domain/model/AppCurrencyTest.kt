package br.com.noartcode.theprice.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AppCurrencyTest {

    @Test
    fun `When the code is unknown or missing it should fall back to US Dollar`() {
        assertEquals(AppCurrency.USD, AppCurrency.fromCode("EUR"))
        assertEquals(AppCurrency.USD, AppCurrency.fromCode(null))
    }
}
