package com.lavenderlang.ui.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anggrayudi.storage.SimpleStorageHelper
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.auth.FirebaseAuth
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.FragmentMainBinding
import com.lavenderlang.domain.usecase.language.GetLanguageUseCase
import com.lavenderlang.domain.usecase.language.GetShortLanguagesUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        MyApp.storageHelper = SimpleStorageHelper(this)
        if (!Python.isStarted()) Python.start(AndroidPlatform(MyApp.getInstance().applicationContext))

        // cur user
        val userId = FirebaseAuth.getInstance().currentUser
        if (userId == null) {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        if( requireContext().getSharedPreferences
                ("pref", Context.MODE_PRIVATE).getInt("nextLanguageId", 0)==0){
            runBlocking(Dispatchers.IO) {
                requireContext().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE).edit()
                    .putInt("nextLanguageId",
                        try {
                            LanguageRepositoryImpl().getMaxId() + 1}
                        catch (e : Exception) {0}
                    ).apply()
            }
        }

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
        Log.d("meow", "we're here")

        binding.blockingView.setOnClickListener(null)

        binding.blockingView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE

        var languages = emptyList<Pair<Int, String>>()
        runBlocking(Dispatchers.IO) {
            val items = GetShortLanguagesUseCase.execute(LanguageRepositoryImpl())
            Log.d("main:items", items.toString())
            languages = items.map { it.id to it.name }
        }

        Log.d("main:langs", languages.toString())
        binding.blockingView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                languages.map { it.second }
            )
        binding.listLanguages.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.listLanguages.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val preferences =
                    requireContext().getSharedPreferences("pref", MODE_PRIVATE)
                val prefEditor = preferences.edit()
                prefEditor.putInt("lang", languages.toList()[position].first)
                prefEditor.apply()

                binding.blockingView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
                MyApp.language = runBlocking {
                    GetLanguageUseCase.execute(
                        languages.toList()[position].first,
                        LanguageRepositoryImpl()
                    )
                }
                Log.d("main:lang", MyApp.language.toString())

                binding.progressBar.setVisibilityAfterHide(View.GONE)
                binding.progressBar.hide()

                val anim = ObjectAnimator.ofFloat(
                    binding.blockingView,
                    "alpha",
                    0.5f,
                    0f
                )
                anim.duration = 500
                anim.start()
                runBlocking(Dispatchers.IO) {
                    Thread.sleep(500)
                    Log.d("main", "sleeping")
                }
                binding.blockingView.visibility = View.GONE

                findNavController().navigate(R.id.action_mainFragment_to_languageFragment)
            }
        Log.d("meow", "we're here 2")
        return binding.root
    }

}