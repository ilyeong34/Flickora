package com.ilyeong.movieverse.core.data.movie.model

import com.ilyeong.movieverse.core.model.Crew
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CrewResponse(
    @SerialName("adult") val adult: Boolean,
    @SerialName("credit_id") val creditId: String,
    @SerialName("department") val department: String,
    @SerialName("gender") val gender: Int,
    @SerialName("id") val id: Int,
    @SerialName("job") val job: String,
    @SerialName("known_for_department") val knownForDepartment: String,
    @SerialName("name") val name: String,
    @SerialName("original_name") val originalName: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("profile_path") val profilePath: String = ""
)

internal fun CrewResponse.toDomain() = Crew(
    adult = adult,
    creditId = creditId,
    department = department,
    gender = gender,
    id = id,
    job = job,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = originalName,
    popularity = popularity,
    profilePath = "https://image.tmdb.org/t/p/original/$profilePath",
)