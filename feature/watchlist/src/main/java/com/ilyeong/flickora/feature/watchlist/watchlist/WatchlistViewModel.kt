package com.ilyeong.flickora.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.ui.common.model.toPosterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map as flowMap
import javax.inject.Inject

@HiltViewModel
internal class WatchlistViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val watchlistPaging = userRepository.getWatchlistMoviePaging()
        .flowMap { pagingData -> pagingData.map { it.toPosterUiModel() } }
        .cachedIn(viewModelScope)
}
