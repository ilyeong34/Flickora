package com.ilyeong.flickora.feature.detail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ilyeong.flickora.feature.detail.tvdetail.TvEpisodeFragment
import com.ilyeong.flickora.feature.detail.tvdetail.TvInformationFragment
import com.ilyeong.flickora.feature.detail.tvdetail.TvRecommendedFragment
import com.ilyeong.flickora.feature.detail.tvdetail.TvReviewFragment

internal class TvDetailTabAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {
    private val fragmentList = listOf(
        TvInformationFragment(),
        TvEpisodeFragment(),
        TvRecommendedFragment(),
        TvReviewFragment(),
    )

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size
}
