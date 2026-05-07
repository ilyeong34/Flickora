package com.ilyeong.movieverse.core.data.movie.model

import com.ilyeong.movieverse.core.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Serializable
internal data class ReviewResponse(
    @SerialName("author") val author: String,
    @SerialName("author_details") val authorDetailsResponse: AuthorDetailsResponse,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("id") val id: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("url") val url: String
)

internal fun ReviewResponse.toDomain() = Review(
    author = author,
    authorDetails = authorDetailsResponse.toDomain(),
    content = content,
    createdAt = parseUtcToLocal(createdAt),
    id = id,
    updatedAt = parseUtcToLocal(updatedAt),
    url = url
)

private fun parseUtcToLocal(dateString: String): String {
    val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC") // UTC 기준으로 해석
    }

    val date = utcFormat.parse(dateString) ?: return "????-??-??"

    val localFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault() // 로컬 시간대로 변환
    }

    return localFormat.format(date)
}
