package com.ilyeong.movieverse.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.movieverse.core.data.movie.repository.MovieRepository
import com.ilyeong.movieverse.core.data.user.repository.UserRepository
import com.ilyeong.movieverse.core.model.TimeWindow
import com.ilyeong.movieverse.feature.home.model.HomeUiState
import com.ilyeong.movieverse.feature.home.model.HomeUiState.Loading
import com.ilyeong.movieverse.feature.home.model.HomeUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val watchlistPaging = userRepository.getWatchlistMoviePaging().cachedIn(viewModelScope)
    val upcomingMoviePaging =
        movieRepository.getUpcomingMoviePaging(maxPage = 3).cachedIn(viewModelScope)
    val popularMoviePaging =
        movieRepository.getPopularMoviePaging(maxPage = 3).cachedIn(viewModelScope)
    val nowPlayingMoviePaging =
        movieRepository.getNowPlayingMoviePaging(maxPage = 3).cachedIn(viewModelScope)
    val trendingWeekMoviePaging =
        movieRepository.getTrendingMoviePaging(timeWindow = TimeWindow.WEEK, maxPage = 3)
            .cachedIn(viewModelScope)
    val topRatedMoviePaging =
        movieRepository.getTopRatedMoviePaging(maxPage = 3).cachedIn(viewModelScope)

    init {
        loadData()
    }

    fun loadData() {
        if (_uiState.value is Success) return

        val trendingDayFlow = movieRepository.getTrendingMovieList(TimeWindow.DAY)
        val genreFlow = movieRepository.getMovieGenreList()

        combine(trendingDayFlow, genreFlow, ::Pair)
            .onStart {
                _uiState.value = Loading
                delay(2000L)
            }
            .onEach { _uiState.value = Success(it.first, it.second) }
            .catch { _uiState.value = HomeUiState.Failure }
            .launchIn(viewModelScope)
    }
}