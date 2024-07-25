package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import kotlinx.coroutines.flow.Flow


class BillsRepositoryImp(
    private val datasource: BillLocalDataSource,
) : BillsRepository {

    override fun getAllBills(): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override fun getBillsBy(status: String): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(bill: Bill) {

    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun getBill(name: String): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}