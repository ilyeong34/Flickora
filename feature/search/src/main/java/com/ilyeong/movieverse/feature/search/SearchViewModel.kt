package com.ilyeong.movieverse.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilyeong.movieverse.core.data.movie.repository.MovieRepository
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.model.TimeWindow
import com.ilyeong.movieverse.feature.search.model.SearchUiState
import com.ilyeong.movieverse.feature.search.model.SearchUiState.Failure
import com.ilyeong.movieverse.feature.search.model.SearchUiState.Loading
import com.ilyeong.movieverse.feature.search.model.SearchUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(Loading)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchMoviePaging = _query
        .flatMapLatest {
            when (it.isBlank()) {
                true -> flowOf(PagingData.empty<Movie>())
                false -> movieRepository.searchMoviePaging(it)
            }
        }
        .cachedIn(viewModelScope)

    init {
        movieRepository.getTrendingMovieList(TimeWindow.DAY)
            .onStart {
                _uiState.value = Loading
                delay(1000L)    // Loading Test
            }
            .onEach {
                when (it.isEmpty()) {
                    true -> _uiState.value = Failure
                    false -> _uiState.value = Success(it)
                }
            }
            .catch { _uiState.value = Failure }
            .launchIn(viewModelScope)
    }


    fun setQuery(query: String) {
        _query.value = query
    }
}