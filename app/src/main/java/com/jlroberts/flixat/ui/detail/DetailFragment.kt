package com.jlroberts.flixat.ui.detail

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
import com.jlroberts.flixat.databinding.FragmentDetailBinding
import com.jlroberts.flixat.domain.model.DetailMovie
import com.jlroberts.flixat.ui.common.MovieAdapter
import com.jlroberts.flixat.ui.nowplaying.NowPlayingFragmentDirections
import com.jlroberts.flixat.utils.useClearStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailViewModel>()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is DetailEvent.TrailerClicked -> launchTrailer()
                        is DetailEvent.BackClicked -> navigateUp()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    when(state) {
                        is DetailState.Success -> {
                            binding.movie = state.movie
                            loadBackdrop(state.movie)
                            showTrailerButton(!state.movie.videos.isNullOrEmpty())
                            hideLoading()
                        }
                        is DetailState.Loading -> {
                            showLoading()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loading.visibility = View.GONE
    }

    private fun loadBackdrop(movie: DetailMovie) {
        val request = ImageRequest.Builder(binding.backdrop.context)
            .data(movie.backdropPath?.original)
            .target(binding.backdrop)
            .listener(
                onStart = {
                    binding.backdropScrim.visibility = View.VISIBLE
                }
            )
            .crossfade(true)
            .crossfade(200)
            .build()
        imageLoader.enqueue(request)
    }

    private fun showTrailerButton(show: Boolean) {
        if (show) {
            binding.trailerButton.visibility = View.VISIBLE
        }
    }

    private fun launchTrailer() {
        val appIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube:" + viewModel.trailerKey)
        )
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=" + viewModel.trailerKey)
        )
        try {
            startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    private fun navigateUp() {
        findNavController().navigateUp()
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