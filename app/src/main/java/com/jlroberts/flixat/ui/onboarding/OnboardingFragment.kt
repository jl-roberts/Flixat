package com.jlroberts.flixat.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jlroberts.flixat.databinding.FragmentOnboardingBinding
import com.jlroberts.flixat.domain.repository.PreferencesManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    val preferencesManager: PreferencesManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.startButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                preferencesManager.setOnboardingComplete()
                findNavController().navigate(
                    OnboardingFragmentDirections.actionOnboardingFragmentToPopularFragment()
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}