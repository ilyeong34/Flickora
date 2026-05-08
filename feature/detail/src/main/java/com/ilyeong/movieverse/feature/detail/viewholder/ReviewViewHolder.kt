package com.ilyeong.movieverse.feature.detail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import com.ilyeong.movieverse.core.model.Review
import com.ilyeong.movieverse.core.ui.R
import com.ilyeong.movieverse.feature.detail.databinding.ItemMovieReviewBinding

class ReviewViewHolder private constructor(
    private val binding: ItemMovieReviewBinding
) : ViewHolder(binding.root) {

    fun bind(review: Review) {
        binding.ivAvatar.load(review.authorDetails.avatarPath) {
            crossfade(true)
            error(R.drawable.ic_profile_filled_gray_24)
        }

        binding.tvUserName.text = review.authorDetails.username
        binding.tvDay.text = review.updatedAt

        binding.rrv.rating = review.authorDetails.rating
        binding.rrv.ratingCountIsVisible = false

        binding.tvContent.text = review.content
        binding.tvContent.maxLines = 3
        binding.tvContent.setOnClickListener {
            when (binding.tvContent.maxLines) {
                3 -> binding.tvContent.maxLines = Int.MAX_VALUE
                else -> binding.tvContent.maxLines = 3
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): ReviewViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMovieReviewBinding.inflate(layoutInflater, parent, false)
            return ReviewViewHolder(binding)
        }
    }
}