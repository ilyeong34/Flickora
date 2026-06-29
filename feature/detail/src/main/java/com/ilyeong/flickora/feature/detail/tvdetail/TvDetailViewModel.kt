package com.ilyeong.flickora.feature.detail.tvdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.feature.detail.model.DetailEvent
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
internal class TvDetailViewModel @Inject constructor(
    private val tvRepository: TvRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvDetailUiState>(TvDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailEvent>()
    val events = _events.asSharedFlow()

    private val _tvSeriesId = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewPaging = _tvSeriesId.flatMapLatest {
        tvRepository.getTvReviewPaging(it)
    }.cachedIn(viewModelScope)

    fun loadData(tvSeriesId: Int) {
        _tvSeriesId.value = tvSeriesId

        val detailFlow = tvRepository.getTvDetail(tvSeriesId)
        val accountStatesFlow = userRepository.getTvAccountStates(tvSeriesId)
        val castFlow = tvRepository.getTvCast(tvSeriesId)
        val recommendationListFlow = tvRepository.getTvRecommendationList(tvSeriesId)
        val similarListFlow = tvRepository.getTvSimilarList(tvSeriesId)

        combine(
            detailFlow,
            accountStatesFlow,
            castFlow,
            recommendationListFlow,
            similarListFlow,
        ) { tvSeries, accountStates, cast, recommendationList, similarList ->

            _uiState.value = TvDetailUiState.Success(
                tvSeries = tvSeries,
                cast = cast,
                recommendationList = recommendationList,
                similarList = similarList,
                selectedSeasonNumber = getSelectedSeasonNumber(tvSeries),
                isInWatchlist = accountStates.watchlist,
            )
        }.onStart {
            if (_uiState.value is TvDetailUiState.Failure) {
                _uiState.value = TvDetailUiState.Loading
            }
        }.catch {
            if (_uiState.value is TvDetailUiState.Loading) {
                _uiState.value = TvDetailUiState.Failure
            } else {
                _events.emit(DetailEvent.ShowMessage(it))
            }
        }.launchIn(viewModelScope)
    }

    fun addTvToWatchlist() {
        val currentState = uiState.value as? TvDetailUiState.Success ?: return

        val watchlist = currentState.isInWatchlist.not()

        userRepository.addTvToWatchlist(currentState.tvSeries, watchlist)
            .onEach {
                val latestState = uiState.value as? TvDetailUiState.Success ?: return@onEach
                _uiState.value = latestState.copy(isInWatchlist = watchlist)
            }
            .catch {
                _events.emit(DetailEvent.ShowMessage(it))
            }
            .launchIn(viewModelScope)
    }

    fun selectSeason(seasonNumber: Int) {
        val state = _uiState.value as? TvDetailUiState.Success ?: return

        if (state.selectedSeasonNumber == seasonNumber) return
        if (state.tvSeries.seasonList.none { it.seasonNumber == seasonNumber }) return

        _uiState.value = state.copy(selectedSeasonNumber = seasonNumber)
    }

    private fun getSelectedSeasonNumber(tvSeries: TvSeries): Int? {
        val previousState = _uiState.value as? TvDetailUiState.Success
        val previousSelection = previousState?.selectedSeasonNumber

        if (
            previousState?.tvSeries?.id == tvSeries.id &&
            tvSeries.seasonList.any { it.seasonNumber == previousSelection }
        ) {
            return previousSelection
        }

        return tvSeries.seasonList.firstOrNull { it.seasonNumber > 0 }?.seasonNumber
            ?: tvSeries.seasonList.firstOrNull()?.seasonNumber
    }
}
