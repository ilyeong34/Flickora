package com.ilyeong.flickora.core.data.user.model

import com.ilyeong.flickora.core.model.AccountStates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountStatesResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("favorite") val favorite: Boolean = false,
    @SerialName("rated") val rated: RatedResponse? = null,
    @SerialName("watchlist") val watchlist: Boolean = true
)

internal fun AccountStatesResponse.toDomain() = AccountStates(
    id = id,
    favorite = favorite,
    rated = rated?.value,
    watchlist = watchlist
)