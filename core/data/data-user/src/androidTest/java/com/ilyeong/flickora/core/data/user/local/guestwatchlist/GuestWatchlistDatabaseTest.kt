package com.ilyeong.flickora.core.data.user.local.guestwatchlist

import android.database.sqlite.SQLiteDatabase
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.ilyeong.flickora.core.data.user.datasource.UserLocalDataSourceImpl
import com.ilyeong.flickora.core.data.user.local.GuestWatchlistDatabase
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistMovieEntity
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistTvEntity
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.SpokenLanguage
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GuestWatchlistDatabaseTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var database: GuestWatchlistDatabase? = null

    @Before
    fun setUp() {
        database = Room.databaseBuilder(
            context,
            GuestWatchlistDatabase::class.java,
            TEST_DB_NAME
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database?.close()
        context.deleteDatabase(TEST_DB_NAME)
    }

    @Test
    fun roundTripPreservesSelectedFieldsAndInsertedAt() = runBlocking {
        val dao = requireNotNull(database).guestWatchlistDao()

        dao.upsert(
            GuestWatchlistMovieEntity(
                id = 3,
                posterPath = "poster-3",
                title = "Third",
                overview = "Third overview",
                voteAverage = 7.3,
                voteCount = 730,
                insertedAt = 3000L
            )
        )
        dao.upsert(
            GuestWatchlistMovieEntity(
                id = 1,
                posterPath = "poster-1",
                title = "First",
                overview = "First overview",
                voteAverage = 8.1,
                voteCount = 810,
                insertedAt = 1000L
            )
        )

        val page = dao.getWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val entities = requirePage(page)
        assertEquals(listOf(3, 1), entities.map { it.id })
        assertEquals(3000L, entities.first().insertedAt)
        assertEquals(1000L, entities.last().insertedAt)
    }

    @Test
    fun tvRoundTripPreservesSelectedFieldsAndInsertedAt() = runBlocking {
        val dao = requireNotNull(database).guestWatchlistDao()

        dao.upsert(
            GuestWatchlistTvEntity(
                id = 13,
                backdropPath = "backdrop-13",
                firstAirDate = "2020-01-13",
                name = "Thirteenth",
                overview = "Thirteenth overview",
                posterPath = "poster-13",
                originalLanguage = "en",
                originalName = "Thirteenth Original",
                popularity = 13.0,
                voteAverage = 8.3,
                voteCount = 1300,
                insertedAt = 13000L
            )
        )
        dao.upsert(
            GuestWatchlistTvEntity(
                id = 11,
                backdropPath = "backdrop-11",
                firstAirDate = "2020-01-11",
                name = "Eleventh",
                overview = "Eleventh overview",
                posterPath = "poster-11",
                originalLanguage = "en",
                originalName = "Eleventh Original",
                popularity = 11.0,
                voteAverage = 8.1,
                voteCount = 1100,
                insertedAt = 11000L
            )
        )

        val page = dao.getTvWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val entities = requireTvPage(page)
        assertEquals(listOf(13, 11), entities.map { it.id })
        assertEquals(13000L, entities.first().insertedAt)
        assertEquals(11000L, entities.last().insertedAt)
    }

    @Test
    fun reopeningDatabaseRetainsGuestWatchlistRows() = runBlocking {
        val dao = requireNotNull(database).guestWatchlistDao()
        dao.upsert(
            GuestWatchlistMovieEntity(
                id = 7,
                posterPath = "poster-7",
                title = "Persisted",
                overview = "Persisted overview",
                voteAverage = 6.7,
                voteCount = 670,
                insertedAt = 7000L
            )
        )
        database?.close()

        database = Room.databaseBuilder(
            context,
            GuestWatchlistDatabase::class.java,
            TEST_DB_NAME
        ).allowMainThreadQueries().build()

        val reopenedPage =
            requireNotNull(database).guestWatchlistDao().getWatchlistPagingSource().load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )

        val entities = requirePage(reopenedPage)
        assertEquals(1, entities.size)
        assertEquals(7, entities.single().id)
        assertEquals("Persisted", entities.single().title)
        assertEquals("Persisted overview", entities.single().overview)
        assertEquals(6.7, entities.single().voteAverage, 0.0)
        assertEquals(670, entities.single().voteCount)
    }

    @Test
    fun addMovieToWatchlistPreservesOriginalInsertedAtForExistingRows() = runBlocking {
        val dao = requireNotNull(database).guestWatchlistDao()
        val localDataSource = UserLocalDataSourceImpl(dao)

        localDataSource.addMovieToWatchlist(
            movie(
                id = 9,
                title = "Legacy Ninth",
                overview = "Legacy ninth overview",
                voteAverage = 9.9,
                voteCount = 990
            ),
            true
        ).collect {}
        Thread.sleep(5)

        localDataSource.addMovieToWatchlist(
            movie(
                id = 4,
                title = "Legacy Fourth",
                overview = "Legacy fourth overview",
                voteAverage = 4.4,
                voteCount = 440
            ),
            true
        ).collect {}

        val page = dao.getWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val entities = requirePage(page)
        assertEquals(listOf(4, 9), entities.map { it.id })
        assertTrue(entities.first().insertedAt >= entities.last().insertedAt)

        val originalInsertedAt = entities.single { it.id == 9 }.insertedAt

        localDataSource.addMovieToWatchlist(
            movie(
                id = 9,
                title = "Legacy Ninth Updated",
                overview = "Legacy ninth overview",
                voteAverage = 9.9,
                voteCount = 990
            ),
            true
        ).collect {}

        val reloadedPage = dao.getWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val reloadedEntities = requirePage(reloadedPage)
        assertEquals(listOf(4, 9), reloadedEntities.map { it.id })
        assertEquals(originalInsertedAt, reloadedEntities.single { it.id == 9 }.insertedAt)
        assertEquals("Legacy Ninth Updated", reloadedEntities.single { it.id == 9 }.title)
    }

    @Test
    fun addTvToWatchlistPreservesOriginalInsertedAtForExistingRows() = runBlocking {
        val dao = requireNotNull(database).guestWatchlistDao()
        val localDataSource = UserLocalDataSourceImpl(dao)

        localDataSource.addTvToWatchlist(
            tvSeries(
                id = 19,
                name = "Legacy Nineteenth",
                overview = "Legacy nineteenth overview",
                voteAverage = 9.9,
                voteCount = 990
            ),
            true
        ).collect {}
        Thread.sleep(5)

        localDataSource.addTvToWatchlist(
            tvSeries(
                id = 14,
                name = "Legacy Fourteenth",
                overview = "Legacy fourteenth overview",
                voteAverage = 4.4,
                voteCount = 440
            ),
            true
        ).collect {}

        val page = dao.getTvWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val entities = requireTvPage(page)
        assertEquals(listOf(14, 19), entities.map { it.id })
        assertTrue(entities.first().insertedAt >= entities.last().insertedAt)

        val originalInsertedAt = entities.single { it.id == 19 }.insertedAt

        localDataSource.addTvToWatchlist(
            tvSeries(
                id = 19,
                name = "Legacy Nineteenth Updated",
                overview = "Legacy nineteenth overview",
                voteAverage = 9.9,
                voteCount = 990
            ),
            true
        ).collect {}

        val reloadedPage = dao.getTvWatchlistPagingSource().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val reloadedEntities = requireTvPage(reloadedPage)
        assertEquals(listOf(14, 19), reloadedEntities.map { it.id })
        assertEquals(originalInsertedAt, reloadedEntities.single { it.id == 19 }.insertedAt)
        assertEquals("Legacy Nineteenth Updated", reloadedEntities.single { it.id == 19 }.name)
    }

    @Test
    fun openingLegacyDatabaseUsesDestructiveFallback() = runBlocking {
        createLegacyDatabaseVersionMismatch(
            rows = listOf(
                LegacyRow(
                    id = 9,
                    posterPath = "legacy-poster-9",
                    title = "Legacy Ninth",
                    overview = "Legacy ninth overview"
                ),
                LegacyRow(
                    id = 4,
                    posterPath = "legacy-poster-4",
                    title = "Legacy Fourth",
                    overview = "Legacy fourth overview"
                )
            )
        )

        database = Room.databaseBuilder(
            context,
            GuestWatchlistDatabase::class.java,
            TEST_DB_NAME
        ).fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
            .allowMainThreadQueries()
            .build()

        val migratedPage =
            requireNotNull(database).guestWatchlistDao().getWatchlistPagingSource().load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )

        val entities = requirePage(migratedPage)
        assertTrue(entities.isEmpty())
    }

    private fun requirePage(
        result: PagingSource.LoadResult<Int, GuestWatchlistMovieEntity>
    ): List<GuestWatchlistMovieEntity> {
        assertTrue(result is PagingSource.LoadResult.Page)
        return (result as PagingSource.LoadResult.Page).data
    }

    private fun requireTvPage(
        result: PagingSource.LoadResult<Int, GuestWatchlistTvEntity>
    ): List<GuestWatchlistTvEntity> {
        assertTrue(result is PagingSource.LoadResult.Page)
        return (result as PagingSource.LoadResult.Page).data
    }

    private companion object {
        const val TEST_DB_NAME = "guest_watchlist_test.db"
    }

    private fun movie(
        id: Int,
        title: String,
        overview: String,
        voteAverage: Double,
        voteCount: Int,
    ): Movie = Movie(
        adult = false,
        collection = null,
        backdropPath = "",
        genreList = emptyList<Genre>(),
        id = id,
        originalLanguage = "",
        originalTitle = title,
        overview = overview,
        popularity = 0.0,
        posterPath = "poster-$id",
        releaseDate = "",
        runtime = 0,
        spokenLanguageList = emptyList<SpokenLanguage>(),
        title = title,
        video = false,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isInWatchlist = false,
    )

    private fun tvSeries(
        id: Int,
        name: String,
        overview: String,
        voteAverage: Double,
        voteCount: Int,
    ): TvSeries = TvSeries(
        adult = false,
        backdropPath = "",
        genreList = emptyList(),
        id = id,
        originCountry = emptyList(),
        originalLanguage = "",
        originalName = name,
        overview = overview,
        popularity = 0.0,
        posterPath = "poster-$id",
        firstAirDate = "",
        name = name,
        voteAverage = voteAverage,
        voteCount = voteCount,
    )

    private fun createLegacyDatabaseVersionMismatch(rows: List<LegacyRow>) {
        context.deleteDatabase(TEST_DB_NAME)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TEST_DB_NAME), null).use { db ->
            db.execSQL(
                """
                CREATE TABLE guest_watchlist (
                    id INTEGER NOT NULL PRIMARY KEY,
                    posterPath TEXT NOT NULL,
                    title TEXT NOT NULL,
                    overview TEXT NOT NULL
                )
                """.trimIndent()
            )

            rows.forEach { row ->
                db.execSQL(
                    "INSERT INTO guest_watchlist (id, posterPath, title, overview) VALUES (?, ?, ?, ?)",
                    arrayOf<Any?>(row.id, row.posterPath, row.title, row.overview)
                )
            }

            db.version = 5
        }
    }

    private data class LegacyRow(
        val id: Int,
        val posterPath: String,
        val title: String,
        val overview: String,
    )
}
