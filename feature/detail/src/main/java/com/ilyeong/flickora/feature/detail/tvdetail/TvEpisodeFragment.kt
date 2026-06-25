package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvEpisodeBinding

internal class TvEpisodeFragment : BaseFragment<FragmentTvEpisodeBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvEpisodeBinding =
        FragmentTvEpisodeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
