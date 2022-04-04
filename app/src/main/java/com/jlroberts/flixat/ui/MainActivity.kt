package com.jlroberts.flixat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var destinationChangedListener: NavController.OnDestinationChangedListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
        destinationChangedListener = NavController.OnDestinationChangedListener { _, _, bundle ->
            with(listOf(binding.bottomNav, binding.bottomAppBar, binding.searchFab)) {
                this.onEach {
                    it.isVisible = bundle?.getBoolean("showBottomBar", true) == true
                }
            }
        }
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onDestroy() {
        navController.removeOnDestinationChangedListener(destinationChangedListener)
        super.onDestroy()
        _binding = null
    }
}