package com.ilyeong.flickora.feature.detail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ilyeong.flickora.feature.detail.InformationFragment
import com.ilyeong.flickora.feature.detail.RecommendedFragment
import com.ilyeong.flickora.feature.detail.ReviewFragment

internal class DetailTabAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {
    private val fragmentList = listOf(
        InformationFragment(),
        RecommendedFragment(),
        ReviewFragment(),
    )

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size
}