package br.com.noartcode.theprice.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLanguageTest {

    @Test
    fun `When the code is unknown or missing it should fall back to English`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
    }
}
