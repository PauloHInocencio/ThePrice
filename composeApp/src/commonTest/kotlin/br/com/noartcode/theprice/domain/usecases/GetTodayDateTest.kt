package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.ui.di.commonTestModule
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class GetTodayDateTest : KoinTest {

    private val getTodayDate : IGetTodayDate by inject()

    @BeforeTest
    fun before() {
        startKoin { modules(commonTestModule()) }
    }

    @AfterTest
    fun after() {

    }

}