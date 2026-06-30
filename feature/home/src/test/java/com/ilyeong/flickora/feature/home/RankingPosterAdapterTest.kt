package com.ilyeong.flickora.feature.home

import com.ilyeong.flickora.feature.home.adapter.PosterRankingAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

class PosterRankingAdapterTest {

    @Test
    fun rankText_usesOneBasedIndex() {
        assertEquals("1", PosterRankingAdapter.rankText(0))
        assertEquals("10", PosterRankingAdapter.rankText(9))
    }
}
