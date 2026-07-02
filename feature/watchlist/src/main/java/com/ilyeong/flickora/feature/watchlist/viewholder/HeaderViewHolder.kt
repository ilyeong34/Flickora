package com.ilyeong.flickora.feature.watchlist.viewholder

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.chip.Chip
import com.google.android.material.R as MaterialR
import com.ilyeong.flickora.core.ui.R as CoreUiR
import com.ilyeong.flickora.feature.watchlist.R
import com.ilyeong.flickora.feature.watchlist.databinding.ItemWatchlistMediaTypeHeaderBinding
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType

internal class HeaderViewHolder private constructor(
    private val binding: ItemWatchlistMediaTypeHeaderBinding
) : ViewHolder(binding.root) {

    fun bind(
        mediaTypeList: List<WatchlistMediaType>,
        selectedMediaType: WatchlistMediaType,
        onMediaTypeClick: (WatchlistMediaType) -> Unit
    ) {
        binding.root.removeAllViews()
        mediaTypeList.forEachIndexed { index, mediaType ->
            binding.root.addView(
                createChip(mediaType, mediaType == selectedMediaType, onMediaTypeClick),
                createChipLayoutParams(hasEndMargin = index != mediaTypeList.lastIndex)
            )
        }
    }

    private fun createChip(
        mediaType: WatchlistMediaType,
        isSelected: Boolean,
        onMediaTypeClick: (WatchlistMediaType) -> Unit
    ): Chip {
        val context = binding.root.context
        val chipContext = ContextThemeWrapper(
            context,
            MaterialR.style.Widget_Material3_Chip_Assist_Elevated
        )
        return Chip(chipContext).apply {
            text = context.getString(mediaType.labelRes)
            this.isSelected = isSelected
            isClickable = true
            isFocusable = true
            isChipIconVisible = false
            setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.watchlist_media_type_chip_text_selector
                )
            )
            chipBackgroundColor =
                ContextCompat.getColorStateList(context, R.color.watchlist_media_type_chip_background_selector)
            chipStrokeColor =
                ContextCompat.getColorStateList(context, R.color.watchlist_media_type_chip_stroke_selector)
            chipStrokeWidth = resources.displayMetrics.density
            setOnClickListener { onMediaTypeClick(mediaType) }
        }
    }

    private fun createChipLayoutParams(hasEndMargin: Boolean): LinearLayout.LayoutParams {
        val context = binding.root.context
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            if (hasEndMargin) {
                marginEnd = context.resources.getDimensionPixelSize(CoreUiR.dimen.flickora_padding_medium)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup) = HeaderViewHolder(
            ItemWatchlistMediaTypeHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
