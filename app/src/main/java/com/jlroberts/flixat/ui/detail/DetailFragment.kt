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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.FragmentDetailBinding
import com.jlroberts.flixat.ui.MainActivity
import com.jlroberts.flixat.utils.useClearStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var binding: FragmentDetailBinding
    private val viewModel by viewModels<DetailViewModel>()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val castAdapter = CastAdapter(imageLoader)
        binding.castRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.castRecycler.adapter = castAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movie.collectLatest {
                    it?.credits?.let { cast ->
                        castAdapter.submitList(cast)
                    }
                    it?.videos?.let {
                        binding.trailerButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        val watchProviderAdapter = WatchProviderAdapter(imageLoader)
        binding.providerRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.providerRecycler.adapter = watchProviderAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.watchProviders.collectLatest {
                    it?.let {
                        watchProviderAdapter.submitList(it)
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.trailerButton.setOnClickListener {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + viewModel.movie.value?.videos?.first()?.key))
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + viewModel.movie.value?.videos?.first()?.key))
            try {
                startActivity(appIntent)
            } catch(e: ActivityNotFoundException) {
                startActivity(webIntent)
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        activity?.useClearStatusBar(true)
    }

    override fun onStop() {
        super.onStop()
        activity?.useClearStatusBar(false)
    }
}