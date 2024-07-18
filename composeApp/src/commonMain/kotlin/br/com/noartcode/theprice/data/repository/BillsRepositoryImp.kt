package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.dao.BillDao
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow


class BillsRepositoryImp(
    private val database: ThePrinceDatabase,
) : BillsRepository {

    private val dao:BillDao by lazy {
        database.getExpenseDao()
    }
    override fun getAllBills(): Flow<Resource<List<Bill>>> {
        TODO("Not yet implemented")
    }

    override fun getBillsBy(status: String): Flow<Resource<List<Bill>>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(bill: Bill) {
        TODO("Not yet implemented")
    }

    override suspend fun getBill(id: Int): Resource<Bill> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}