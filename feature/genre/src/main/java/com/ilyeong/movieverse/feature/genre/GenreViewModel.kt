package com.ilyeong.movieverse.feature.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.movieverse.core.data.movie.repository.MovieRepository
import com.ilyeong.movieverse.core.model.Genre
import com.ilyeong.movieverse.feature.genre.model.GenreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class GenreViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _genreId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val genreMoviePaging = _genreId
        .filterNotNull()
        .flatMapLatest {
            delay(1000L)        // Shimmer Test
            movieRepository.getMovieListByGenrePaging(it)
        }
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow<GenreUiState>(GenreUiState(Genre(0, "")))
    val uiState = _uiState.asStateFlow()

    fun setGenreId(genreId: Int) {
        _genreId.value = genreId

        movieRepository.getMovieGenreList()
            .onEach { genreList ->
                _uiState.value =
                    GenreUiState(genreList.find { it.id == genreId } ?: Genre(genreId, ""))
            }.catch {
                _uiState.value = GenreUiState(Genre(genreId, ""))
            }.launchIn(viewModelScope)
    }
}