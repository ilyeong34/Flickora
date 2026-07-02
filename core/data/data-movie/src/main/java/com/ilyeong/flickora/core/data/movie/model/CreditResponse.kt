package com.ilyeong.flickora.core.data.movie.model

import com.ilyeong.flickora.core.model.Credit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreditResponse(
    @SerialName("cast") val cast: List<CastResponse> = emptyList(),
    @SerialName("crew") val crew: List<CrewResponse> = emptyList(),
    @SerialName("id") val id: Int
)

internal fun CreditResponse.toDomain() = Credit(
    cast = cast.map { it.toDomain() },
    crew = crew.map { it.toDomain() },
    id = id
)