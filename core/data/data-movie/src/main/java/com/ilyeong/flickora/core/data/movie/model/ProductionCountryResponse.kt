package com.ilyeong.flickora.core.data.movie.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ProductionCountryResponse(
    @SerialName("iso_3166_1") val iso_3166_1: String,
    @SerialName("name") val name: String
)