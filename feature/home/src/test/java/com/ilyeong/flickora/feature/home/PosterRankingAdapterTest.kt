package com.ilyeong.flickora.feature.home

import com.ilyeong.flickora.feature.home.adapter.PosterRankingAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

class PosterRankingAdapterTest {

    @Test
    fun rankText_usesOneBasedIndex() {
        val adapter = PosterRankingAdapter {}
        val rankText = PosterRankingAdapter::class.java
            .getDeclaredMethod("rankText", Int::class.javaPrimitiveType)
            .apply { isAccessible = true }

        assertEquals("1", rankText.invoke(adapter, 0))
        assertEquals("10", rankText.invoke(adapter, 9))
    }
}
