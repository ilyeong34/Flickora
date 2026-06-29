package com.ilyeong.flickora.core.data.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WatchlistMovieResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val resultList: List<WatchlistMovieListResponse> = emptyList(),
    @SerialName("total_pages") val totalPage: Int,
    @SerialName("total_results") val totalResults: Int
)
