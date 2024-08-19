package com.lavenderlang.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.exception.FileWorkException
import com.lavenderlang.databinding.FragmentLoadLanguageBinding
import com.lavenderlang.domain.usecase.language.GetLanguageFromFileUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.runBlocking

class LoadLanguageFragment : Fragment() {
    private lateinit var binding: FragmentLoadLanguageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoadLanguageBinding.inflate(inflater, container, false)

        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 11)
            findNavController().navigate(
                R.id.action_loadLanguageFragment_to_instructionFragment,
                argsToSend
            )
        }

        var accessiblePathsRaw = DocumentFileCompat.getAccessibleAbsolutePaths(requireContext())
        val accessible = arrayListOf<String>()
        for (key in  accessiblePathsRaw.keys) {
            for (path in accessiblePathsRaw[key]!!) {
                accessible.add(path)
            }
        }
        Log.d("accessible", accessible.toString())

        //эти 3 строчки обновляют список
        var adapterPath: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accessible)
        binding.spinnerPath.adapter = adapterPath
        adapterPath.notifyDataSetChanged()


        binding.buttonFirst.setOnClickListener {
            val requestCode = 123123
            if (MyApp.storageHelper == null) {
                Log.d("restore", "StorageHelper is null")
                Toast.makeText(requireContext(), "Загрузка невозможна", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            MyApp.storageHelper!!.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            accessible.clear()

            accessiblePathsRaw = DocumentFileCompat.getAccessibleAbsolutePaths(requireContext())
            for (key in  accessiblePathsRaw.keys) {
                for (path in accessiblePathsRaw[key]!!) {
                    accessible.add(path)
                }
            }
            adapterPath = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accessible)
            binding.spinnerPath.adapter = adapterPath
            adapterPath.notifyDataSetChanged()
        }
        binding.buttonOpen.setOnClickListener {
            Log.d("restore", accessible.toString())
            val path = binding.editTextPath.editText?.text.toString()
            val pathPositionSpinner = binding.spinnerPath.selectedItemPosition
            if (pathPositionSpinner == Spinner.INVALID_POSITION) {
                Toast.makeText(requireContext(), "Папка не выбрана, сохранение невозможно", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.d("path", "${accessible[pathPositionSpinner]}/${path}")
            val prefs = requireContext().getSharedPreferences("pref", MODE_PRIVATE)
            val id = prefs.getInt("nextLanguageId", -1)
            Log.d("LoadLanguageFragment", "next id = $id")
            try {
                runBlocking {
                    MyApp.language = GetLanguageFromFileUseCase.execute(
                        "${accessible[pathPositionSpinner]}/${path}", id,
                        LanguageRepositoryImpl(), requireContext())
                    prefs.edit().putInt("lang", id).apply()
                    prefs.edit().putInt("nextLanguageId", id + 1).apply()
                }
            } catch (e: FileWorkException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            catch (e: Exception) {
                Log.d("woof", e.message?:"")
                return@setOnClickListener
            }
            //нет навигации на страницу языка :(
            //findNavController().navigate(R.id.action_loadLanguageFragment_to_mainFragment)
        }
        return binding.root
    }
}