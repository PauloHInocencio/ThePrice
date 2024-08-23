package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform

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
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(
        month: Int,
        year: Int,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = billLDS
        .getBillsBy(billStatus)
        .flatMapLatest { bills ->
            flow {
                try {
                    val payments = bills.map { bill ->
                        with(paymentLDS.getPayment(bill.id, month, year))  {
                            if (this != null) return@with this
                            val id = paymentLDS.insert(
                                billID = bill.id,
                                dueDate = DayMonthAndYear(
                                    day = bill.invoiceDueDay,
                                    month = month,
                                    year = year
                                )
                            )
                            return@with paymentLDS.getPayment(id, bill.id)!!
                        }
                    }
                    emit(Resource.Success(payments))
                } catch (e:Throwable) {
                    emit(Resource.Error(
                        message = "Something wrong happen when trying to get payments",
                        exception = e
                    ))
                }

            }
        }.flowOn(dispatcher)
}