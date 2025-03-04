package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow


class BillsRepositoryImp(
    private val local: BillLocalDataSource,
    private val remote: BillRemoteDataSource,
) : BillsRepository {

    override fun getAllBills() : Flow<List<Bill>> {
        return local.getAllBills()
    }

    override fun getBillsBy(status:Bill.Status) : Flow<List<Bill>> {
        return local.getBillsBy(status)
    }

    override suspend fun fetchAllBills(): Resource<Unit> {
        return when(val result = remote.fetchAllBills()) {
            is Resource.Error -> result
            is Resource.Loading -> result
            is Resource.Success -> {
                local.insert(result.data.toDomain())
                Resource.Success(Unit)
            }
        }
    }

    override suspend fun insert(bill: Bill) {
        TODO("Not yet implemented")
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