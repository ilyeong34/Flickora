package com.ilyeong.flickora.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.PagingData
import androidx.paging.map
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.core.ui.common.model.PosterUiModel
import com.ilyeong.flickora.core.ui.common.model.toPosterUiModel
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
internal class WatchlistViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private val _selectedMediaType = MutableStateFlow(WatchlistMediaType.MOVIE)
    val selectedMediaType = _selectedMediaType.asStateFlow()

    private val movieWatchlistPaging: Flow<PagingData<PosterUiModel>> =
        userRepository.getWatchlistMoviePaging()
            .map { pagingData: PagingData<Movie> ->
                pagingData.map { movie: Movie -> movie.toPosterUiModel() }
            }

    private val tvWatchlistPaging: Flow<PagingData<PosterUiModel>> =
        userRepository.getWatchlistTvPaging()
            .map { pagingData: PagingData<TvSeries> ->
                pagingData.map { tvSeries: TvSeries -> tvSeries.toPosterUiModel() }
            }

    val watchlistPaging: Flow<PagingData<PosterUiModel>> =
        selectedMediaType.flatMapLatest { selectedType ->
            when (selectedType) {
                WatchlistMediaType.MOVIE -> movieWatchlistPaging
                WatchlistMediaType.TV_SERIES -> tvWatchlistPaging
            }
        }
        .cachedIn(viewModelScope)

    fun setSelectedMediaType(mediaType: WatchlistMediaType) {
        if (_selectedMediaType.value == mediaType) return

        _selectedMediaType.value = mediaType
    }
}
