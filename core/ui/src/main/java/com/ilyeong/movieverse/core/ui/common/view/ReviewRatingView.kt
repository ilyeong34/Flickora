package com.ilyeong.movieverse.core.ui.common.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.ilyeong.movieverse.core.ui.R

class ReviewRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val starImageViews = mutableListOf<ImageView>()
    private val ratingTextView: TextView

    var rating: Double = 0.0
        set(value) {
            field = value
            val fullStars = (value / 2).toInt()
            val halfStar = if (value % 2 >= 1) 1 else 0
            ratingTextView.text = "$value"
            updateStars(fullStars, halfStar)
        }

    var ratingCount: Int = 0
        set(value) {
            field = value
            ratingTextView.text = context.getString(R.string.rating_count, ratingCount)
        }

    var ratingCountIsVisible: Boolean = true
        set(value) {
            field = value
            ratingTextView.isVisible = value
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        repeat(5) {
            val starImageView = ImageView(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            }
            starImageViews.add(starImageView)
            addView(starImageView)
        }

        ratingTextView = TextView(context).apply {
            setTextColor(resources.getColor(R.color.gray, context.theme))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = resources.getDimensionPixelSize(R.dimen.movieverse_padding_small)
            }
        }
        addView(ratingTextView)
    }

    private fun updateStars(fullStars: Int, halfStar: Int) {
        repeat(5) {
            val starImageView = starImageViews[it]
            when {
                (it < fullStars) -> {
                    starImageView.setImageResource(R.drawable.ic_star_filled_12)
                }

                (it == fullStars && halfStar == 1) -> {
                    starImageView.setImageResource(R.drawable.ic_star_half_12)
                }

                else -> {
                    starImageView.setImageResource(R.drawable.ic_star_outlined_12)
                }
            }
        }
    }
}