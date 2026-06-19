package com.ilyeong.flickora.core.data.movie.model

import com.ilyeong.flickora.core.model.AuthorDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthorDetailsResponse(
    @SerialName("avatar_path") val avatarPath: String = "",
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: Double = 0.0,
    @SerialName("username") val username: String
)

internal fun AuthorDetailsResponse.toDomain() = AuthorDetails(
    avatarPath = "https://image.tmdb.org/t/p/original/$avatarPath",
    name = name,
    rating = rating,
    username = username
)