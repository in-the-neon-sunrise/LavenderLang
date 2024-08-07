package com.lavenderlang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lavenderlang.databinding.FragmentLanguageBinding

class WordFormationFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }
}