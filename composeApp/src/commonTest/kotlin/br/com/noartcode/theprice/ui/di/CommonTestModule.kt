package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

import org.koin.dsl.module

fun commonTestModule(testDispatcher: TestDispatcher = StandardTestDispatcher()) = module {
    single<IGetTodayDate> { GetTodayDateStub() }
    single<IGetPayments> { GetPayments(billLDS = get(), paymentLDS = get(), insertMissingPayments = get(), dispatcher = testDispatcher)}
}