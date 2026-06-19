package com.ilyeong.flickora.core.data.movie.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RecommendationListResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val recommendationList: List<RecommendationResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

internal fun RecommendationListResponse.toDomain() = recommendationList.map { it.toDomain() }