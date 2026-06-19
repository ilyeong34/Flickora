package com.ilyeong.flickora.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.movie.repository.MovieRepository
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.feature.search.model.SearchUiState
import com.ilyeong.flickora.feature.search.model.TrendState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchMoviePaging = _uiState
        .map { it.query }
        .distinctUntilChanged()
        .flatMapLatest { movieRepository.searchMoviePaging(it) }
        .cachedIn(viewModelScope)

    init {
        movieRepository.getTrendingMovieList(TimeWindow.DAY)
            .onStart {
                _uiState.update { it.copy(trendState = TrendState.Loading) }
                delay(1000L)    // Loading Test
            }
            .onEach { trendMovieList ->
                when (trendMovieList.isEmpty()) {
                    true -> {
                        _uiState.update { it.copy(trendState = TrendState.Failure) }
                    }

                    false -> {
                        _uiState.update { it.copy(trendState = TrendState.Success(trendMovieList)) }
                    }
                }
            }
            .catch {
                _uiState.update { it.copy(trendState = TrendState.Failure) }
            }
            .launchIn(viewModelScope)
    }


    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }
}