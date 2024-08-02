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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface IGetPayments {
    operator fun invoke(
        month:Int,
        year:Int,
        billStatus: Bill.Status
    ) : Flow<Resource<List<Payment>>>
}

class GetPayments(
    private val billLDS: BillLocalDataSource,
    private val paymentLDS: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IGetPayments {
    override fun invoke(
        month: Int,
        year: Int,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = flow{
        try {
            val bills = billLDS.getBillsBy(billStatus)
            val payments = bills.map { bill ->
                with(paymentLDS.getPayment(bill.id, month, year))  {
                    if (this != null) return@with this
                    val id = paymentLDS.insert(Payment(billId = bill.id, billTitle = bill.name))
                    return@with paymentLDS.getPayment(id)!!
                }
            }
            emit(Resource.Success(payments))
        } catch (e:Throwable) {
            emit(Resource.Error(
                message = "Something wrong happen when trying to get payments",
                exception = e
            ))
        }
    }.flowOn(dispatcher)
}