package com.ilyeong.flickora.core.data.tv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SimilarListResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val similarList: List<TvResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

internal fun SimilarListResponse.toDomain() = similarList.map { it.toDomain() }
