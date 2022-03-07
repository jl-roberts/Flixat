package com.jlroberts.flixat.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import coil.ImageLoader
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.ActivityMainBinding
import com.jlroberts.flixat.ui.common.MovieAdapter
import com.jlroberts.flixat.ui.popular.PopularFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var searchAdapter: MovieAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        binding.bottomNav.background = null

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        searchAdapter = MovieAdapter(imageLoader) { movie ->
            navController.navigate(
                PopularFragmentDirections.actionFeedFragmentToDetailFragment(
                    movie.movieId
                )
            )
        }
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            hasFixedSize()
        }
    }

    private fun setupListeners() {
        binding.etSearch.doOnTextChanged { charSequence: CharSequence?, _, _, _ ->
            viewModel.search(charSequence.toString())
        }
        binding.searchFab.setOnClickListener {
            viewModel.onSearchFabClicked()
        }
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailFragment -> {
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
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collectLatest { searchResults ->
                    searchAdapter.submitData(searchResults)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchActive.collect { searchActive ->
                    logcat { "lifecyclescope detected fab click" }
                    if (searchActive) {
                        onSearchActivated()
                    } else {
                        onSearchDeactivated()
                    }
                }
            }
        }
    }

    private fun onSearchActivated() {
        binding.searchResults.visibility = View.VISIBLE
        binding.searchFab.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_cancel_24
            )
        )
    }

    private fun onSearchDeactivated() {
        clearSearch()
        binding.searchResults.visibility = View.GONE
        binding.searchFab.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_search_24
            )
        )
    }

    private fun clearSearch() {
        lifecycleScope.launch {
            searchAdapter.submitData(PagingData.empty())
            binding.etSearch.text?.clear()
        }
    }

    fun hideBottomBar() {
        binding.bottomAppBar.visibility = View.GONE
        binding.bottomNav.visibility = View.VISIBLE
    }
}