package com.ilyeong.flickora

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ilyeong.flickora.core.ui.common.activity.BaseActivity
import com.ilyeong.flickora.databinding.ActivityMainBinding
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
            val destinationIds = destination.hierarchy.map { it.id }.toSet()

            when {
                R.id.home_navigation in destinationIds -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, 0, systemBars.right, 0)
                        insets
                    }
                }

                R.id.watchlist_navigation in destinationIds ||
                        R.id.profile_navigation in destinationIds -> {
                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
                        insets
                    }
                }

                R.id.detail_navigation in destinationIds -> {
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
            val bottomNavigationDestinations = setOf(
                R.id.home_navigation,
                R.id.watchlist_navigation,
                R.id.profile_navigation
            )

            binding.bnv.isVisible = destination.hierarchy.any {
                it.id in bottomNavigationDestinations
            }
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
