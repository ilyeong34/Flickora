package com.ilyeong.flickora.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WatchlistViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val watchlistPaging = userRepository.getWatchlistMoviePaging().cachedIn(viewModelScope)
}