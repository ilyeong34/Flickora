package com.ilyeong.flickora.core.data.tv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PopularResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val resultList: List<TvResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
