package com.ilyeong.movieverse

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ilyeong.movieverse.core.ui.common.activity.BaseActivity
import com.ilyeong.movieverse.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val viewBindingInflater: (inflater: LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeNavController()
        setSystemInsetPadding()
        setUpBottomNavigationView()
        setBackPressedDispatcher()
    }

    private fun initializeNavController() {
        val navHostFragment = binding.navHostFragment.getFragment<NavHostFragment>()
        navController = navHostFragment.navController
    }

    private fun setSystemInsetPadding() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home_fragment -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, 0, systemBars.right, 0)
                        insets
                    }
                }

                R.id.watchlist_fragment, R.id.profile_fragment -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
                        insets
                    }
                }

                R.id.detail_fragment -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                        insets
                    }
                }

                else -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                        )
                        insets
                    }
                }
            }
        }
    }

    private fun setUpBottomNavigationView() {
        binding.bnv.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnv.isVisible = (destination.id in listOf(
                R.id.home_fragment,
                R.id.watchlist_fragment,
                R.id.profile_fragment
            ))
        }
    }

    private fun setBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.popBackStack().not()) {
                    finish()
                }
            }
        })
    }
}