package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.SpokenLanguage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SpokenLanguageResponse(
    @SerialName("english_name") val englishName: String,
    @SerialName("iso_639_1") val iso_639_1: String,
    @SerialName("name") val name: String
)

internal fun SpokenLanguageResponse.toDomain() = SpokenLanguage(
    englishName = englishName,
    iso_639_1 = iso_639_1,
    name = name
)
