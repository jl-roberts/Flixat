package com.jlroberts.flixat.ui.nowplaying

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.jlroberts.flixat.databinding.FragmentNowplayingBinding
import com.jlroberts.flixat.ui.common.MovieAdapter
import com.jlroberts.flixat.ui.popular.PopularFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel by viewModels<NowPlayingViewModel>()

    private var _binding: FragmentNowplayingBinding? = null
    private val binding get() = _binding!!

    private var _nowPlayingAdapter: MovieAdapter? = null
    private val nowPlayingAdapter get() = _nowPlayingAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowplayingBinding.inflate(inflater)
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
    }

    private fun setupFeedList() {
        _nowPlayingAdapter = MovieAdapter(imageLoader) { movie ->
            findNavController().navigate(
                NowPlayingFragmentDirections.actionNowPlayingToDetailFragment(
                    movie.movieId
                )
            )
        }
        binding.recyclerView.apply {
            adapter = nowPlayingAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
        binding.feedRefresh.setOnRefreshListener {
            nowPlayingAdapter.refresh()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { movies ->
                    nowPlayingAdapter.submitData(movies)
                    binding.feedRefresh.isRefreshing = false
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                nowPlayingAdapter.loadStateFlow.collectLatest { loadState ->
                    if (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error) {
                        showNoNetworkSnackbar()
                    }
                }
            }
        }
    }

    private fun showNoNetworkSnackbar() {
        val fab: FloatingActionButton = activity?.findViewById(R.id.search_fab)!!
        val snackbar = Snackbar.make(fab, "No internet connection, cannot refresh", Snackbar.LENGTH_LONG).apply {
            anchorView = fab
        }.setAction("Retry") {
           nowPlayingAdapter.retry()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _nowPlayingAdapter = null
    }
}