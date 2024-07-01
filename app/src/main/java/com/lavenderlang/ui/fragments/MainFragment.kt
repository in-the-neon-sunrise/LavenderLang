package com.lavenderlang.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anggrayudi.storage.SimpleStorageHelper
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.R
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.databinding.FragmentMainBinding
import com.lavenderlang.frontend.MyApp
import com.lavenderlang.frontend.languages

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (MyApp.nextLanguageId == -1) {
            // go to splash screen
        }
        binding = FragmentMainBinding.inflate(inflater, container, false)

        MyApp.storageHelper = SimpleStorageHelper(this)
        if (!Python.isStarted()) Python.start(AndroidPlatform(MyApp.getInstance().applicationContext))

        //button new lang listener
        binding.buttonNewLang.setOnClickListener {
            val preferences = requireContext().getSharedPreferences("pref", MODE_PRIVATE)
            val prefEditor = preferences.edit()
            prefEditor.putInt("lang", -1)
            prefEditor.apply()
            findNavController().navigate(R.id.action_mainFragment_to_languageFragment)
        }

        //go to load language
        binding.buttonFromFile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_loadLanguageFragment)
        }

        //go to information
        binding.buttonInf.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_informationFragment)
        }
        //bottom navigation menu
        val bottomNavigationView = binding.bottomNavigation

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    // show that the user is already on the home page
                    true
                }

                R.id.action_language -> {
                    findNavController().navigate(R.id.action_mainFragment_to_languageFragment)
                    true
                }

                R.id.action_translator -> {
                    findNavController().navigate(R.id.action_mainFragment_to_translatorFragment)
                    true
                }

                else -> false
            }
        }

        val adapter: ArrayAdapter<LanguageEntity> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                languages.values.toList()
            )
        binding.listLanguages.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.listLanguages.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val preferences = requireContext().getSharedPreferences("pref", MODE_PRIVATE)
                val prefEditor = preferences.edit()
                prefEditor.putInt("lang", languages.values.toList()[position].languageId)
                prefEditor.apply()
                findNavController().navigate(R.id.action_mainFragment_to_languageFragment)
            }

        return binding.root
    }

}