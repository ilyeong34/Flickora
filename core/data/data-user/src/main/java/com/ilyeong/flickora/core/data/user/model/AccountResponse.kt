package com.ilyeong.flickora.core.data.user.model

import com.ilyeong.flickora.core.model.Account
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountResponse(
    @SerialName("avatar") val avatar: AvatarResponse = AvatarResponse(
        GravatarResponse(""),
        TmdbResponse("")
    ),
    @SerialName("id") val id: Int = -1,
    @SerialName("include_adult") val includeAdult: Boolean = false,
    @SerialName("iso_3166_1") val iso_3166_1: String = "",
    @SerialName("iso_639_1") val iso_639_1: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("username") val username: String = ""
)

internal fun AccountResponse.toDomain() = Account(
    id = id,
    avatarPath = avatar.tmdbResponse.avatarPath,
    name = name,
    username = username
)