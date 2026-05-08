package com.ilyeong.movieverse.core.model

data class Credit(
    val cast: List<Cast>,
    val crew: List<Crew>,
    val id: Int
)