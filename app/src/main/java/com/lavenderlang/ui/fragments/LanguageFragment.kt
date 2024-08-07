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
//import com.lavenderlang.frontend.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class LanguageFragment: Fragment() {
    private lateinit var createJSONLauncher: ActivityResultLauncher<String>
    private lateinit var createPDFLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentLanguageBinding

    companion object{
        var idLang: Int = -1
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
            Log.d("language", "dictionary: ${MyApp.language!!.languageId}")
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
        when(val lang = preferences.getInt("lang", -1)) {
            -1 -> {
                idLang = MyApp.nextLanguageId
                runBlocking {
                    withContext(Dispatchers.IO) {
                        languageDao.createLanguage("Язык$idLang", "")
                    }
                }
                Log.d("language", "new language: ${MyApp.language!!.languageId}")
            }

            else -> {
                idLang = lang
                runBlocking {
                    withContext(Dispatchers.IO) {
                        languageDao.getLanguage(lang)
                        if (MyApp.language == null) {
                            languageDao.createLanguage("Язык$idLang", "")
                        }
                    }
                }
            }
        }
        binding.editLanguageName.setText(MyApp.language!!.name)

        if(MyApp.language?.description != "")
            binding.editDescription.setText(MyApp.language!!.description)

        //check changing
        binding.editLanguageName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                runBlocking {
                    languageDao.changeName(
                        MyApp.language!!,
                        binding.editLanguageName.text.toString()
                    )
                }
            }
        })
        binding.editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                runBlocking {
                    languageDao.changeDescription(
                        MyApp.language!!,
                        binding.editDescription.text.toString()
                    )
                }
            }
        })

        binding.buttonFile.setOnClickListener {
            try {
                LanguageDaoImpl().downloadLanguageJSON(
                    MyApp.language!!,
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
                    MyApp.language!!,
                    MyApp.storageHelper!!,
                    createPDFLauncher)
            } catch (e: FileWorkException) {
                Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        binding.buttonCopy.setOnClickListener {
            runBlocking {
                languageDao.copyLanguage(MyApp.language!!)
            }
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        binding.buttonDelete.setOnClickListener {
            runBlocking {
                Companion.languageDao.deleteLanguage(idLang)
            }
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        return binding.root
    }
}