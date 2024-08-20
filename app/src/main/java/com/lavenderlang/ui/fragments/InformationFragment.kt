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
import com.anggrayudi.storage.extension.toInt
import com.google.firebase.auth.FirebaseAuth
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

        binding.buttonGuide.setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_instructionFragment)
        }

        binding.buttonDeleteAccount.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.delete()
            FirebaseAuth.getInstance().signOut()
            val sp = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt("Fragment", R.id.startingFragment)
            editor.apply()
            requireActivity().finish()
            requireActivity().startActivity(requireActivity().intent)
        }

        binding.buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val sp = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt("Fragment", R.id.startingFragment)
            editor.apply()
            requireActivity().finish()
            requireActivity().startActivity(requireActivity().intent)
        }

        if(requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getBoolean("Theme", false)){
            binding.toggleButton.check(R.id.buttonDark)
        }
        else{
            binding.toggleButton.check(R.id.buttonLight)
        }

        binding.buttonDark.setOnClickListener {
            val sp = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt("Fragment", R.id.informationFragment)
            editor.apply()
            Log.d("Theme", "dark")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            requireActivity().recreate()
            editor.putBoolean("Theme", true)
            editor.apply()
            binding.toggleButton.uncheck(R.id.buttonLight)
        }
        binding.buttonLight.setOnClickListener {
            val sp = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt("Fragment", R.id.informationFragment)
            editor.apply()
            Log.d("Theme", "light")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            requireActivity().recreate()
            editor.putBoolean("Theme", false)
            editor.apply()
            binding.toggleButton.uncheck(R.id.buttonDark)
        }

        return binding.root
    }
}