package com.jlroberts.flixat.ui.nowplaying

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import coil.ImageLoader
import com.google.android.gms.location.LocationServices
import com.google.android.material.transition.MaterialFadeThrough
import com.jlroberts.flixat.R
import com.jlroberts.flixat.databinding.FragmentNowplayingBinding
import com.jlroberts.flixat.ui.common.MovieAdapter
import com.jlroberts.flixat.utils.Permission
import com.jlroberts.flixat.utils.PermissionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class NowPlayingFragment : Fragment() {

    val imageLoader: ImageLoader by inject()

    val viewModel: NowPlayingViewModel by viewModel()

    private var _binding: FragmentNowplayingBinding? = null
    private val binding get() = _binding!!

    private var _nowPlayingAdapter: MovieAdapter? = null
    private val nowPlayingAdapter get() = _nowPlayingAdapter!!

    private val permissionManager = PermissionManager.from(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowplayingBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFeedList()
        setupObservers()
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        binding.toolbar.inflateMenu(R.menu.toolbar_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> {
                    findNavController().navigate(
                        NowPlayingFragmentDirections
                            .actionNowPlayingFragmentToAboutFragment()
                    )
                    return@setOnMenuItemClickListener true
                }
                R.id.preferences -> {
                    findNavController().navigate(
                        NowPlayingFragmentDirections
                            .actionNowPlayingFragmentToPreferenceFragment()
                    )
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
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
                viewModel.state.collectLatest { state ->
                    if (state.country.isEmpty()) {
                        requestLocation()
                    }
                    nowPlayingAdapter.submitData(state.movies)
                    binding.feedRefresh.isRefreshing = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                nowPlayingAdapter.loadStateFlow.collectLatest { loadState ->
                    viewModel.onLoading(
                        loadState.refresh is LoadState.Loading
                                || loadState.append is LoadState.Loading
                    )
                    viewModel.onError(
                        loadState.refresh is LoadState.Error
                                || loadState.append is LoadState.Error
                    )
                }
            }
        }
    }

    private fun requestLocation() {
        permissionManager
            .request(Permission.ForegroundLocation)
            .rationale(getString(R.string.permission_denied_explanation))
            .checkPermission { granted ->
                if (granted) {
                    try {
                        val locationProviderClient =
                            LocationServices.getFusedLocationProviderClient(requireActivity())
                        locationProviderClient.lastLocation
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    task.result?.let { location ->
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            val country =
                                                getCountry(location.latitude, location.longitude)
                                            country?.let {
                                                viewModel.setCountry(country)
                                            }
                                        }
                                    }
                                } else {
                                    logcat { "Current location is null" }
                                }
                            }
                    } catch (e: SecurityException) {
                        logcat { "Exception: $e.asLog() " }
                    }
                }
            }
    }

    private fun getCountry(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        val result: String? = try {
            val location = geocoder.getFromLocation(latitude, longitude, 1)
            location?.let {
                return location[0].countryCode
            }
        } catch (e: IOException) {
            logcat { "Error receiving country code" }
            return null
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _nowPlayingAdapter = null
    }
}