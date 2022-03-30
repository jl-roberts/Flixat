package com.jlroberts.flixat.ui.detail

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
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.material.appbar.AppBarLayout
import com.jlroberts.flixat.databinding.FragmentDetailBinding
import com.jlroberts.flixat.domain.model.DetailMovie
import com.jlroberts.flixat.utils.useClearStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupToolbar()
        setupRecyclerViews()
        setupListeners()
        setupObservers()

        return binding.root
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener() { appBarLayout, verticalOffset ->
            if (verticalOffset != 0) {
                appBarLayout.visibility = View.INVISIBLE
            } else {
                appBarLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun setupRecyclerViews() {
        val castAdapter = CastAdapter(imageLoader)
        binding.castRecycler.adapter = castAdapter

        val similarAdapter = SimilarMoviesAdapter(imageLoader) { movie ->
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToDetailFragment(
                    movie.movieId
                )
            )
        }
        binding.rvSimilar.adapter = similarAdapter

        val watchProviderAdapter = WatchProviderAdapter(imageLoader)
        binding.providerRecycler.adapter = watchProviderAdapter
    }

    private fun loadBackdrop(movie: DetailMovie?) {
        val request = ImageRequest.Builder(binding.backdrop.context)
            .data(movie?.backdropPath?.original)
            .target(binding.backdrop)
            .listener(
                onSuccess = { request, metadata ->
                    binding.backdropScrim.visibility = View.VISIBLE
                })
            .crossfade(true)
            .crossfade(200)
            .build()
        imageLoader.enqueue(request)
    }

    private fun setupListeners() {
        binding.retryButton.setOnClickListener {
            viewModel.retry()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.movie = state.movie
                    loadBackdrop(state.movie)
                    showTrailerButton(!state.movie?.videos.isNullOrEmpty())
                }
            }
        }
    }

    private fun showTrailerButton(show: Boolean) {
        if (show) {
            binding.trailerButton.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.useClearStatusBar(true)
    }

    override fun onStop() {
        super.onStop()
        activity?.useClearStatusBar(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}