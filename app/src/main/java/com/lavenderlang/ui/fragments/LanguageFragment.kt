package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.service.exception.FileWorkException
import com.lavenderlang.databinding.FragmentLanguageBinding
import com.lavenderlang.frontend.MyApp
import com.lavenderlang.frontend.languages


class LanguageFragment: Fragment() {
    private lateinit var createJSONLauncher: ActivityResultLauncher<String>
    private lateinit var createPDFLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentLanguageBinding

    companion object{
        var idLang: Int = 0
        val languageDao: LanguageDaoImpl = LanguageDaoImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLanguageBinding.inflate(inflater, container, false)

        createJSONLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            Log.d("json start write", "result")
            if (uri != null) {
                Log.d("json write", uri.toString())
                LanguageDaoImpl().writeToJSON(uri)
            } else {
                Log.d("json write", "uri is null")
            }
        }

        createPDFLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            Log.d("pdf start write", "result")
            if (uri != null) {
                LanguageDaoImpl().writeToPDF(uri)
            } else {
                Log.d("pdf write", "uri is null")
            }
        }

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 2)
            findNavController().navigate(R.id.action_languageFragment_to_instructionFragment, argsToSend)
        }

        //parts of language
        binding.buttonDictionary.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_dictionaryFragment)
        }
        binding.buttonGrammar.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_grammarFragment)
        }
        binding.buttonPunctuation.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_punctuationFragment)
        }
        binding.buttonWriting.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_writingFragment)
        }
        binding.buttonWordFormation.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_wordFormationFragment)
        }

        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_translatorFragment)
        }

        val preferences =
            requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        //how it was started?
        when(val lang = preferences.getInt("lang", -1)){
            -1 -> {
                idLang = MyApp.nextLanguageId
                languageDao.createLanguage("Язык$idLang", "")
                binding.editLanguageName.setText(languages[idLang]?.name)
            }
            else -> {
                idLang = lang
                binding.editLanguageName.setText(languages[idLang]?.name)
            }
        }
        if(languages[idLang]?.description != "")
            binding.editDescription.setText(languages[idLang]?.description)

        //check changing
        binding.editLanguageName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeName(languages[idLang]!!, binding.editLanguageName.text.toString())
            }
        })
        binding.editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeDescription(languages[idLang]!!, binding.editDescription.text.toString())
            }
        })

        binding.buttonFile.setOnClickListener {
            try {
                LanguageDaoImpl().downloadLanguageJSON(
                    languages[idLang]!!,
                    MyApp.storageHelper!!,
                    createJSONLauncher)
            } catch (e: FileWorkException) {
                Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        binding.buttonPDF.setOnClickListener {
            try {
                LanguageDaoImpl().downloadLanguagePDF(
                    languages[idLang]!!,
                    MyApp.storageHelper!!,
                    createPDFLauncher)
            } catch (e: FileWorkException) {
                Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        binding.buttonCopy.setOnClickListener {
            languageDao.copyLanguage(languages[idLang]!!)
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        binding.buttonDelete.setOnClickListener {
            languageDao.deleteLanguage(idLang)
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        return binding.root
    }
}