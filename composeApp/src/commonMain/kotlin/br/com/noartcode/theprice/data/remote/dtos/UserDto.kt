@file:OptIn(ExperimentalUuidApi::class)

package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class UserDto(
    @Serializable(with = UuidSerializer::class)
    val id:Uuid,
    val name:String,
    val email:String,
    val picture:String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant
)

@OptIn(ExperimentalUuidApi::class)
object UuidSerializer : KSerializer<Uuid> {

    // Serial names of descriptors should be unique, this is why we advise including app package in the name.
    // from: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#custom-serializers
    override val descriptor: SerialDescriptor
        get() =  PrimitiveSerialDescriptor("kotlin.uuid.Uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) =
        Uuid.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }
}