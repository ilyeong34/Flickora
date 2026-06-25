package com.ilyeong.flickora.core.ui.common.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.ilyeong.flickora.core.ui.common.model.PosterUiModel

object PosterUiModelDiffUtil : DiffUtil.ItemCallback<PosterUiModel>() {
    override fun areItemsTheSame(
        oldItem: PosterUiModel,
        newItem: PosterUiModel
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: PosterUiModel,
        newItem: PosterUiModel
    ) = oldItem == newItem
}
