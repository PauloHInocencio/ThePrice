package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentDto(
    val id:String,
    @SerialName("bill_id")
    val billID: String,
    @SerialName("is_payed")
    val isPayed:Boolean,
    val price:Long,
    @SerialName("due_date")
    val dueDate:String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)
