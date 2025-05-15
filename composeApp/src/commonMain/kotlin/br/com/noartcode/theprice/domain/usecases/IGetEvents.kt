package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.remote.datasource.events.EventRemoteDataSource
import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

interface IGetEvents {
    operator fun invoke() : Flow<Resource<EventDto>>
}

class GetEvent(
    private val remoteDataSource: EventRemoteDataSource,
    private val dispatcher: CoroutineDispatcher,
) : IGetEvents {
    override fun invoke(): Flow<Resource<EventDto>> = remoteDataSource
        .getEvents()
        .flowOn(dispatcher)

}