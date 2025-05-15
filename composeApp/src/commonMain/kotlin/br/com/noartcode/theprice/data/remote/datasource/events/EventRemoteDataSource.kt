package br.com.noartcode.theprice.data.remote.datasource.events

import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

interface EventRemoteDataSource {
    fun getEvents() : Flow<Resource<EventDto>>
}