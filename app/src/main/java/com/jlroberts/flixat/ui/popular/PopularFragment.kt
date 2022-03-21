package com.jlroberts.flixat.ui.popular

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import coil.ImageLoader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.FragmentPopularBinding
import com.jlroberts.flixat.ui.common.MovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PopularFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel by viewModels<PopularViewModel>()

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!

    private var _feedAdapter: MovieAdapter? = null
    private val feedAdapter get() = _feedAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupToolbar()
        setupFeedList()
        setupObservers()

        return binding.root
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        binding.toolbar.inflateMenu(R.menu.toolbar_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> {
                    findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToAboutFragment())
                    return@setOnMenuItemClickListener true
                }
                R.id.preferences -> {
                    findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToPreferenceFragment())
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun setupFeedList() {
        _feedAdapter = MovieAdapter(imageLoader) { movie ->
            findNavController().navigate(
                PopularFragmentDirections.actionFeedFragmentToDetailFragment(
                    movie.movieId
                )
            )
        }
        binding.recyclerView.apply {
            adapter = feedAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
        binding.feedRefresh.setOnRefreshListener {
            feedAdapter.refresh()
        }

    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { movies ->
                    feedAdapter.submitData(movies)
                    binding.feedRefresh.isRefreshing = false
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedAdapter.loadStateFlow.collectLatest { loadState ->
                    if (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error) {
                        showNoNetworkSnackbar()
                    }
                }
            }
        }
    }

    private fun showNoNetworkSnackbar() {
        val coordinatorLayout: CoordinatorLayout =
            activity?.findViewById(R.id.main_activity_coordinator)!!
        val fab: FloatingActionButton? = activity?.findViewById(R.id.search_fab)
        Snackbar.make(
            coordinatorLayout,
            "No internet connection, cannot refresh",
            Snackbar.LENGTH_LONG
        )
            .apply {
                anchorView = fab
            }.setAction("Retry") {
                feedAdapter.retry()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _feedAdapter = null
    }
}