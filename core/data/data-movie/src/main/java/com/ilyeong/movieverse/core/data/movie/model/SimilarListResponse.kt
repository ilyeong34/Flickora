package com.ilyeong.movieverse.core.data.movie.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SimilarListResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val similarList: List<SimilarResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

internal fun SimilarListResponse.toDomain() = similarList.map { it.toDomain() }