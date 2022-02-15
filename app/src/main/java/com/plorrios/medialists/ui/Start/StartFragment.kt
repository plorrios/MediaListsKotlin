package com.plorrios.medialists.ui.Start

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.StartFragmentBinding

class StartFragment : Fragment() {

    private lateinit var startViewModel: StartViewModel
    private var _binding: StartFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        startViewModel =
            ViewModelProvider(this).get(StartViewModel::class.java)

        _binding = StartFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.imageViewTV.setOnClickListener{ v ->
            activity?.findNavController(R.id.nav_host_fragment_activity_main)?.navigate(R.id.action_startFragment_to_navigation_tv)
        }
        binding.imageViewVideogames.setOnClickListener{ v ->
            activity?.findNavController(R.id.nav_host_fragment_activity_main)?.navigate(R.id.action_startFragment_to_navigation_games)
        }
        binding.imageViewMusic.setOnClickListener{ v ->
            activity?.findNavController(R.id.nav_host_fragment_activity_main)?.navigate(R.id.action_startFragment_to_navigation_music)
        }
        binding.imageViewBooks.setOnClickListener{ v ->
            activity?.findNavController(R.id.nav_host_fragment_activity_main)?.navigate(R.id.action_startFragment_to_navigation_books)
        }

    }

}