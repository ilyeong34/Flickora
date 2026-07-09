package com.ilyeong.flickora.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilyeong.flickora.core.data.media.repository.MediaRepository
import com.ilyeong.flickora.core.data.movie.repository.MovieRepository
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.feature.home.model.HomeUiState
import com.ilyeong.flickora.feature.home.model.HomeUiState.Loading
import com.ilyeong.flickora.feature.home.model.HomeUiState.Success
import com.ilyeong.flickora.feature.home.model.TrailerPlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlinx.coroutines.flow.map as flowMap

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _trailerPlaybackState = MutableStateFlow<TrailerPlaybackState?>(null)
    val trailerPlaybackState: StateFlow<TrailerPlaybackState?> =
        _trailerPlaybackState.asStateFlow()

    val watchlistPaging = userRepository.getWatchlistMoviePaging()
        .flowMap { pagingData -> pagingData.map { it as Media } }
        .cachedIn(viewModelScope)
    val upcomingMoviePaging =
        movieRepository.getUpcomingMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it as Media } }
            .cachedIn(viewModelScope)
    val popularMoviePaging =
        movieRepository.getPopularMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it as Media } }
            .cachedIn(viewModelScope)
    val topRatedMoviePaging =
        movieRepository.getTopRatedMoviePaging(maxPage = 3)
            .flowMap { pagingData -> pagingData.map { it as Media } }
            .cachedIn(viewModelScope)
    val popularTvPaging = tvRepository.getPopularTvPaging(maxPage = 3)
        .flowMap { pagingData -> pagingData.map { it as Media } }
        .cachedIn(viewModelScope)
    val topRatedTvPaging = tvRepository.getTopRatedTvPaging(maxPage = 3)
        .flowMap { pagingData -> pagingData.map { it as Media } }
        .cachedIn(viewModelScope)
    val onTheAirTvPaging = tvRepository.getOnTheAirTvPaging(maxPage = 3)
        .flowMap { pagingData -> pagingData.map { it as Media } }
        .cachedIn(viewModelScope)
    val airingTodayTvPaging = tvRepository.getAiringTodayTvPaging(maxPage = 3)
        .flowMap { pagingData -> pagingData.map { it as Media } }
        .cachedIn(viewModelScope)

    init {
        loadData()
    }

    fun loadData() {
        if (_uiState.value is Success) return

        val trendingDayFlow = mediaRepository.getTrendingMediaList(TimeWindow.DAY)
        val trendingMovieWeekFlow = movieRepository.getTrendingMovieList(TimeWindow.WEEK)
        val trendingTvWeekFlow = tvRepository.getTrendingTvList(TimeWindow.WEEK)
        val genreFlow = movieRepository.getMovieGenreList()
        val nowPlayingTrailerFlow = movieRepository.getNowPlayingMovieListWithVideos()

        combine(
            trendingDayFlow,
            trendingMovieWeekFlow,
            trendingTvWeekFlow,
            genreFlow,
            nowPlayingTrailerFlow
        ) { dayList, movieWeekList, tvWeekList, genreList, nowPlayingTrailerList ->
            Success(
                bannerMediaList = dayList.take(5),
                rankingMovieList = movieWeekList.take(10),
                rankingTvList = tvWeekList.take(10),
                genreList = genreList,
                nowPlayingTrailerList = nowPlayingTrailerList
            )
        }
            .onStart {
                _uiState.value = Loading
                // delay(2000L)
            }
            .onEach { _uiState.value = it }
            .catch { _uiState.value = HomeUiState.Failure }
            .launchIn(viewModelScope)
    }

    fun saveTrailerPlaybackState(state: TrailerPlaybackState?) {
        _trailerPlaybackState.value = state
    }

    fun clearTrailerPlaybackState() {
        _trailerPlaybackState.value = null
    }
}
