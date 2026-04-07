package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.usecases.datetime.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IUpdateBill {
    suspend operator fun invoke(bill: Bill) : Resource<Bill>
}

class UpdateBill(
    private val repository: BillsRepository,
    private val getTodayDate: IGetTodayDate,
    private val dispatcher: CoroutineDispatcher,
) : IUpdateBill {

    override suspend fun invoke(bill: Bill) = withContext(dispatcher) {
        try {
            val updatedBill = bill.copy(updatedAt = getTodayDate().toEpochMilliseconds())
            repository.update(updatedBill)
            return@withContext Resource.Success(updatedBill)

        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "A Error occurred when trying to update a bill"
            )
        }
    }
}