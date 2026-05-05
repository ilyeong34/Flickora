package com.ilyeong.movieverse.core.model

data class AccountStates(
    val id: Int,
    val favorite: Boolean,
    val rated: Int?,
    val watchlist: Boolean
)
