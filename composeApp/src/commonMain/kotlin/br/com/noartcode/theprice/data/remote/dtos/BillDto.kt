package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BillDto(
    val id: String,
    val name:String,
    val description:String?,
    val price:Long,
    val type:String,
    @SerialName("billing_start_date")
    val billingStartDate:String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)
