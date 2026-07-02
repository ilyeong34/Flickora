package com.ilyeong.flickora.core.ui.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ilyeong.flickora.core.ui.R

class LoadingProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar = ProgressBar(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        indeterminateTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.white)
        )
    }

    var isLoading: Boolean = false
        set(value) {
            field = value
            this.isVisible = value
            this.isClickable = value
        }

    init {
        addView(progressBar)
    }
}
