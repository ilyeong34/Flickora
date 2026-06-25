package com.ilyeong.flickora.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.movie.repository.MovieRepository
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.core.ui.common.model.toPosterUiModel
import com.ilyeong.flickora.feature.home.model.HomeUiState
import com.ilyeong.flickora.feature.home.model.HomeUiState.Loading
import com.ilyeong.flickora.feature.home.model.HomeUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.map as flowMap
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    tvRepository: TvRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val watchlistPaging = userRepository.getWatchlistMoviePaging()
        .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
        .cachedIn(viewModelScope)
    val upcomingMoviePaging =
        movieRepository.getUpcomingMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
            .cachedIn(viewModelScope)
    val popularMoviePaging =
        movieRepository.getPopularMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
            .cachedIn(viewModelScope)
    val nowPlayingMoviePaging =
        movieRepository.getNowPlayingMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
            .cachedIn(viewModelScope)
    val trendingWeekMoviePaging =
        movieRepository.getTrendingMoviePaging(timeWindow = TimeWindow.WEEK, maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
            .cachedIn(viewModelScope)
    val topRatedMoviePaging =
        movieRepository.getTopRatedMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
            .cachedIn(viewModelScope)
    val popularTvPaging = tvRepository.getPopularTvPaging(maxPage = 3)
        .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
        .cachedIn(viewModelScope)

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
                // delay(2000L)
            }
            .onEach { _uiState.value = Success(it.first, it.second) }
            .catch { _uiState.value = HomeUiState.Failure }
            .launchIn(viewModelScope)
    }
}
