package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {
    private lateinit var binding: FragmentInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInformationBinding.inflate(inflater, container, false)

        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }

        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_mainFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_translatorFragment)
        }

        binding.buttonGuide.setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_instructionFragment)
        }

        val switchTheme: SwitchCompat = binding.switch1
        switchTheme.isChecked = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getBoolean("Theme", false)
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("Theme", "dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                requireActivity().recreate()
            }
            else {
                Log.d("Theme", "light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                requireActivity().recreate()
            }
            val sp = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean("Theme", isChecked)
            editor.apply()
        }
        return binding.root
    }
}