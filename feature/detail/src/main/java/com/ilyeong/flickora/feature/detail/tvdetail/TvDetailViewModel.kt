package com.ilyeong.flickora.feature.detail.tvdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.feature.detail.tvdetail.model.TvDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TvDetailViewModel @Inject constructor(
    private val tvRepository: TvRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvDetailUiState>(TvDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _tvSeriesId = MutableStateFlow(-1)

    val castPreviewList: Flow<List<Cast>> = _tvSeriesId.flatMapLatest {
        when {
            it < 0 -> flowOf(emptyList())
            else -> tvRepository.getTvCastPreview(it)
        }
    }

    val recommendationList: Flow<List<TvSeries>> = _tvSeriesId.flatMapLatest {
        when {
            it < 0 -> flowOf(emptyList())
            else -> tvRepository.getTvRecommendationList(it)
        }
    }

    val similarList: Flow<List<TvSeries>> = _tvSeriesId.flatMapLatest {
        when {
            it < 0 -> flowOf(emptyList())
            else -> tvRepository.getTvSimilarList(it)
        }
    }

    val reviewPaging = _tvSeriesId.flatMapLatest {
        when {
            it < 0 -> flowOf(PagingData.empty<Review>())
            else -> tvRepository.getTvReviewPaging(it)
        }
    }.cachedIn(viewModelScope)

    fun loadData(tvSeriesId: Int) {
        _tvSeriesId.value = tvSeriesId
        _uiState.value = TvDetailUiState.Loading

        tvRepository.getTvDetail(tvSeriesId)
            .catch { _uiState.value = TvDetailUiState.Failure }
            .onEach { tvSeries ->
                _uiState.value = TvDetailUiState.Success(tvSeries)
            }
            .launchIn(viewModelScope)
    }
}
