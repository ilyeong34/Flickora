package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.Cast
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AggregateCreditsResponse(
    @SerialName("cast") val cast: List<AggregateCastResponse> = emptyList(),
    @SerialName("id") val id: Int
)

@Serializable
internal data class AggregateCastResponse(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("cast_id") val castId: Int = 0,
    @SerialName("character") val character: String = "",
    @SerialName("credit_id") val creditId: String = "",
    @SerialName("gender") val gender: Int = 0,
    @SerialName("id") val id: Int,
    @SerialName("known_for_department") val knownForDepartment: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("order") val order: Int = 0,
    @SerialName("original_name") val originalName: String = "",
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("profile_path") val profilePath: String = "",
    @SerialName("roles") val roles: List<AggregateCastRoleResponse> = emptyList(),
    @SerialName("total_episode_count") val totalEpisodeCount: Int = 0
)

@Serializable
internal data class AggregateCastRoleResponse(
    @SerialName("character") val character: String = "",
    @SerialName("episode_count") val episodeCount: Int = 0
)

internal fun AggregateCreditsResponse.toDomain() = cast.map { it.toDomain() }

internal fun AggregateCastResponse.toDomain() = Cast(
    adult = adult,
    castId = castId,
    character = character.ifBlank { roles.firstOrNull()?.character.orEmpty() },
    creditId = creditId,
    gender = gender,
    id = id,
    knownForDepartment = knownForDepartment,
    name = name,
    order = order,
    originalName = originalName,
    popularity = popularity,
    profilePath = "https://image.tmdb.org/t/p/original/$profilePath",
)
