package com.ilyeong.flickora.core.data.movie.model

import com.ilyeong.flickora.core.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.map

@Serializable
internal data class MovieDetailResponse(
    @SerialName("adult") val adult: Boolean,
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("belongs_to_collection") val belongsToCollection: BelongsToCollectionResponse? = null,
    @SerialName("budget") val budget: Int,
    @SerialName("genres") val genreList: List<GenreResponse> = emptyList(),
    @SerialName("homepage") val homepage: String = "",
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") val imdbId: String = "",
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("production_companies") val productionCompanyList: List<ProductionCompanyResponse> = emptyList(),
    @SerialName("production_countries") val productionCountryList: List<ProductionCountryResponse> = emptyList(),
    @SerialName("release_date") val releaseDate: String,
    @SerialName("revenue") val revenue: Int,
    @SerialName("runtime") val runtime: Int,
    @SerialName("spoken_languages") val spokenLanguageList: List<SpokenLanguageResponse> = emptyList(),
    @SerialName("status") val status: String,
    @SerialName("tagline") val tagline: String,
    @SerialName("title") val title: String,
    @SerialName("video") val video: Boolean,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int
)

internal fun MovieDetailResponse.toDomain() = Movie(
    adult = adult,
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    collection = belongsToCollection?.toDomain(),
    genreList = genreList.map { it.toDomain() },
    id = id,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    releaseDate = releaseDate,
    runtime = runtime,
    spokenLanguageList = spokenLanguageList.map { it.toDomain() },
    title = title,
    video = video,
    videos = emptyList(),
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = false
)
