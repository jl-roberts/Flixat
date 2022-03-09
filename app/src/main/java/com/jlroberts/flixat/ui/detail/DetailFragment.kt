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
import com.jlroberts.flixat.databinding.FragmentDetailBinding
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
                viewModel.movie.collectLatest { movie ->
                    val request = ImageRequest.Builder(binding.backdrop.context)
                        .data(movie?.backdropPath?.original)
                        .target(binding.backdrop)
                        .listener(
                            onSuccess = {
                                _, _ ->
                                binding.backdropScrim.visibility = View.VISIBLE
                            }
                        )
                        .crossfade(true)
                        .build()
                    imageLoader.enqueue(request)
                }
            }
        }

        return binding.root
    }

    private fun launchTrailer() {
        val appIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube:" + viewModel.movie.value?.videos?.first()?.key)
        )
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=" + viewModel.movie.value?.videos?.first()?.key)
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