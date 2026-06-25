package com.ilyeong.flickora.feature.detail.tvdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
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
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
internal class TvDetailViewModel @Inject constructor(
    private val tvRepository: TvRepository
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
        val castFlow = tvRepository.getTvCastPreview(tvSeriesId)
        val recommendationListFlow = tvRepository.getTvRecommendationList(tvSeriesId)
        val similarListFlow = tvRepository.getTvSimilarList(tvSeriesId)

        combine(
            detailFlow,
            castFlow,
            recommendationListFlow,
            similarListFlow,
        ) { tvSeries, cast, recommendationList, similarList ->

            _uiState.value = TvDetailUiState.Success(
                tvSeries = tvSeries,
                cast = cast,
                recommendationList = recommendationList,
                similarList = similarList,
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
}
