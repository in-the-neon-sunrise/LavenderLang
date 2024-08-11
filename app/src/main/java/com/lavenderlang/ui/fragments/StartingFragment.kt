package com.lavenderlang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lavenderlang.R
import com.lavenderlang.databinding.FragmentStartingBinding
import com.lavenderlang.ui.MyApp

class StartingFragment : Fragment() {
    private lateinit var binding: FragmentStartingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartingBinding.inflate(inflater, container, false)
        return binding.root
    }
}