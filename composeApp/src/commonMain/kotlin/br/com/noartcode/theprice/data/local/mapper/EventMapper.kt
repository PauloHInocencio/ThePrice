package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.local.entities.EventEntity
import br.com.noartcode.theprice.domain.model.SyncEvent

fun SyncEvent.toEntity() =
    EventEntity(
        id = this.id,
        endpoint = this.endpoint,
        action = this.action,
        payload = this.payload,
        createdAt = this.createdAt
    )

fun EventEntity.toDomain() =
    SyncEvent(
        id = this.id,
        endpoint = this.endpoint,
        action = this.action,
        payload = this.payload,
        createdAt = this.createdAt
    )

fun Iterable<SyncEvent>.toEntity() = this.map { it.toEntity() }

fun Iterable<EventEntity>.toDomain() = this.map { it.toDomain() }


