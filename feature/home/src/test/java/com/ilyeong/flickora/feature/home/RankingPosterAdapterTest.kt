package com.ilyeong.flickora.feature.home

import com.ilyeong.flickora.feature.home.adapter.RankingPosterAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

class RankingPosterAdapterTest {

    @Test
    fun rankText_usesOneBasedIndex() {
        assertEquals("1", RankingPosterAdapter.rankText(0))
        assertEquals("10", RankingPosterAdapter.rankText(9))
    }
}
