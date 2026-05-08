package com.ilyeong.movieverse.core.data.user.model

import com.ilyeong.movieverse.core.data.user.RatedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = RatedSerializer::class)
internal data class RatedResponse(
    @SerialName("value") val value: Int
)