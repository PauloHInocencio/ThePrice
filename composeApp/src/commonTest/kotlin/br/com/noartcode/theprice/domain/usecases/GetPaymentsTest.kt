package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.ui.di.RobolectricTests
import org.koin.test.KoinTest
import org.koin.test.inject

class GetPaymentsTest : KoinTest, RobolectricTests() {

    private val database: ThePriceDatabase by inject()


}