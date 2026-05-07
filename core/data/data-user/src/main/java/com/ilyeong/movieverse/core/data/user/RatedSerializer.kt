package com.ilyeong.movieverse.core.data.user

import com.ilyeong.movieverse.core.data.user.model.RatedResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

internal object RatedSerializer : KSerializer<RatedResponse?> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("com.ilyeong.movieverse.core.data.user.model.RatedResponse") {
            element("value", PrimitiveSerialDescriptor("value", PrimitiveKind.INT))
        }

    override fun serialize(encoder: Encoder, value: RatedResponse?) {
        val output = encoder as? JsonEncoder
            ?: throw UnsupportedOperationException("This class can be loaded only by Json")
        val jsonElement = when (value) {
            null -> JsonPrimitive(false)
            else -> JsonObject(mapOf("value" to JsonPrimitive(value.value)))
        }
        output.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): RatedResponse? {
        val input = decoder as? JsonDecoder
            ?: throw UnsupportedOperationException("This class can be loaded only by Json")
        val jsonElement = input.decodeJsonElement()

        return when (jsonElement) {
            is JsonPrimitive -> {
                when (jsonElement.booleanOrNull == false) {
                    true -> null
                    else -> throw SerializationException("Unexpected primitive: $jsonElement")
                }
            }

            is JsonObject -> {
                val value = jsonElement["value"]?.jsonPrimitive?.intOrNull
                when (value != null) {
                    true -> RatedResponse(value)
                    else -> throw SerializationException("Unexpected object: $jsonElement")
                }
            }

            else -> throw SerializationException("Unexpected JSON for rated: $jsonElement")
        }
    }
}