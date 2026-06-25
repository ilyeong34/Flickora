package com.ilyeong.flickora.feature.detail.tvdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
internal class TvDetailViewModel @Inject constructor(
    private val tvRepository: TvRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvDetailUiState>(TvDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _tvSeriesId = MutableStateFlow(-1)

    val reviewPaging = _tvSeriesId.flatMapLatest {
        when {
            it < 0 -> flowOf(PagingData.empty<Review>())
            else -> tvRepository.getTvReviewPaging(it)
        }
    }.cachedIn(viewModelScope)

    fun loadData(tvSeriesId: Int) {
        _tvSeriesId.value = tvSeriesId
        _uiState.value = TvDetailUiState.Loading

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
            TvDetailUiState.Success(
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
            }
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }
}
