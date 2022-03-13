package com.jlroberts.flixat.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var destinationChangedListener: NavController.OnDestinationChangedListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        binding.bottomNav.background = null

        setupListeners()
    }


    private fun setupListeners() {
        binding.searchFab.setOnClickListener {
            navController.navigate(R.id.searchFragment)
        }
        destinationChangedListener = NavController.OnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailFragment, R.id.detailFragment2, R.id.searchFragment, R.id.preferenceFragment, R.id.aboutFragment -> {
                    binding.bottomNav.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                    binding.searchFab.visibility = View.GONE
                }
                else -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.searchFab.visibility = View.VISIBLE
                }
            }
        }
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onDestroy() {
        navController.removeOnDestinationChangedListener(destinationChangedListener)
        super.onDestroy()
    }
}