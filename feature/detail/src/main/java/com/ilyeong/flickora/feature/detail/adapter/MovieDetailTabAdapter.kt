package com.ilyeong.flickora.feature.detail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ilyeong.flickora.feature.detail.movie.MovieInformationFragment
import com.ilyeong.flickora.feature.detail.movie.MovieRecommendedFragment
import com.ilyeong.flickora.feature.detail.movie.MovieReviewFragment

internal class MovieDetailTabAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {
    private val fragmentList = listOf(
        MovieInformationFragment(),
        MovieRecommendedFragment(),
        MovieReviewFragment(),
    )

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size
}