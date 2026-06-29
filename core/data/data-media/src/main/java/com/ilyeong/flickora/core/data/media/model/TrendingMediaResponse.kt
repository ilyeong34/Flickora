package com.ilyeong.flickora.core.data.media.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TrendingMediaResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val resultList: List<MultiSearchItemResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
