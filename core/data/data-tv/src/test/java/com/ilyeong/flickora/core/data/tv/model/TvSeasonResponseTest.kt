package com.ilyeong.flickora.core.data.tv.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class TvSeasonResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun tvDetailResponse_mapsSeasonSummaryNameFromApi() {
        val response = json.decodeFromString<TvDetailResponse>(
            """
            {
              "id": 1,
              "seasons": [
                {
                  "air_date": "2008-01-20",
                  "episode_count": 7,
                  "id": 3572,
                  "name": "Season 1",
                  "overview": "Season overview",
                  "poster_path": "/season-poster.jpg",
                  "season_number": 1,
                  "vote_average": 8.5
                }
              ]
            }
            """.trimIndent()
        )

        val season = response.seasonList.single()

        assertEquals("Season 1", season.name)
        assertEquals(1, season.seasonNumber)
        assertEquals(7, season.episodeCount)
    }

    @Test
    fun tvSeasonDetailResponse_mapsSeasonNameAndEpisodes() {
        val response = json.decodeFromString<TvSeasonDetailResponse>(
            """
            {
              "air_date": "2008-01-20",
              "episodes": [
                {
                  "air_date": "2008-01-20",
                  "episode_number": 1,
                  "id": 62085,
                  "name": "Pilot",
                  "overview": "Episode overview",
                  "runtime": 58,
                  "season_number": 1,
                  "still_path": "/still.jpg",
                  "vote_average": 8.3
                }
              ],
              "id": 3572,
              "name": "Season 1",
              "overview": "Season overview",
              "poster_path": "/season-poster.jpg",
              "season_number": 1,
              "vote_average": 8.5
            }
            """.trimIndent()
        )

        val season = response.toDomain(
            TvSeasonSummaryResponse(
                id = 3572,
                name = "Season 1",
                seasonNumber = 1,
                episodeCount = 7
            )
        )
        val episode = season.episodeList.single()

        assertEquals("Season 1", season.name)
        assertEquals("Pilot", episode.name)
        assertEquals(1, episode.episodeNumber)
        assertEquals(58, episode.runtime)
        assertEquals("https://image.tmdb.org/t/p/original//still.jpg", episode.stillPath)
    }
}

