package com.jlroberts.flixat.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jlroberts.flixat.databinding.FragmentFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel by viewModels<FeedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}