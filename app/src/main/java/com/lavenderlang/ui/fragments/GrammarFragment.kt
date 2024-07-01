package com.lavenderlang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lavenderlang.databinding.FragmentGrammarBinding
import com.lavenderlang.databinding.FragmentLanguageBinding

class GrammarFragment : Fragment() {
    private lateinit var binding: FragmentGrammarBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrammarBinding.inflate(inflater, container, false)
        // TODO: перенести все из активити... как-нибудь...
        return binding.root
    }
}