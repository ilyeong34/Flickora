package com.ilyeong.movieverse.core.data.movie.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DiscoverResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val discoverMovieList: List<MovieResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)