package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

interface IGetPayments {
    operator fun invoke(
        month:Int,
        year:Int,
        billStatus: Bill.Status
    ) : Flow<Resource<List<Payment>>>
}

class GetPayments(
    private val billLocalDataSource: BillLocalDataSource,
    private val paymentLocalDataSource: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IGetPayments {
    override fun invoke(
        month: Int,
        year: Int,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> {
        TODO("Not yet implemented")
    }
}