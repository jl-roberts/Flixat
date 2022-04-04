package com.jlroberts.flixat.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.ImageLoader
import com.jlroberts.flixat.databinding.FragmentSearchBinding
import com.jlroberts.flixat.ui.common.MovieAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    val imageLoader: ImageLoader by inject()

    val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var _searchAdapter: MovieAdapter? = null
    private val searchAdapter get() = _searchAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        setupFeedList()
        setupObservers()
        showKeyboard()
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        binding.etSearch.requestFocus()
    }

    private fun setupFeedList() {
        _searchAdapter = MovieAdapter(imageLoader) { movie ->
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToDetailFragment(movie.movieId)
            )
        }
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            hasFixedSize()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collectLatest { searchResults ->
                    searchAdapter.submitData(searchResults)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.etSearch.doOnTextChanged { charSequence: CharSequence?, _, _, _ ->
            viewModel.search(charSequence.toString())
        }
    }

    private fun showKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.etSearch.post {
            inputMethodManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_FORCED)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _searchAdapter = null
    }
}