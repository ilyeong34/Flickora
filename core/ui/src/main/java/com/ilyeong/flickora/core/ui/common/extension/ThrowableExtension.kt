package com.ilyeong.flickora.core.ui.common.extension

import androidx.annotation.StringRes
import com.ilyeong.flickora.core.model.FlickoraError
import com.ilyeong.flickora.core.ui.R

@StringRes
fun Throwable.toMessageResId(): Int {
    if (this is Error) throw this

    return when (this) {
        is FlickoraError.Network -> R.string.error_network
        is FlickoraError.Authentication -> R.string.error_authentication
        is FlickoraError.NotFound -> R.string.error_not_found
        is FlickoraError.Server -> R.string.error_server
        is FlickoraError.Unknown -> R.string.error_unknown
        else -> R.string.error_unknown
    }
}
