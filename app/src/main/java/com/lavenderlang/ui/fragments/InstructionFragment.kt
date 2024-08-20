package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.databinding.FragmentInstructionBinding
import com.lavenderlang.databinding.FragmentLanguageBinding

class InstructionFragment : Fragment() {
    private lateinit var binding: FragmentInstructionBinding
    companion object{
        var idLang: Int = 0
        var idBlock: Int = 0
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInstructionBinding.inflate(inflater, container, false)

        idLang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)
        requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("block", 0)

        val heads = mutableListOf<TextView>()
        heads.add(binding.textViewHead1)
        heads.add(binding.textViewHead2)
        heads.add(binding.textViewHead3)
        heads.add(binding.textViewHead4)
        heads.add(binding.textViewHead5)
        heads.add(binding.textViewHead6)
        heads.add(binding.textViewHead7)
        heads.add(binding.textViewHead8)
        heads.add(binding.textViewHead9)
        heads.add(binding.textViewHead10)
        heads.add(binding.textViewHead11)

        if(idBlock !=0) binding.scrollView2.post { binding.scrollView2.smoothScrollTo(0, heads[idBlock -1].top) }
        return binding.root
    }
}