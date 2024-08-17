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
import com.lavenderlang.domain.PdfWriterDaoImpl
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.exception.FileWorkException
import com.lavenderlang.databinding.FragmentLanguageBinding
import com.lavenderlang.domain.usecase.language.ChangeDescriptionUseCase
import com.lavenderlang.domain.usecase.language.ChangeNameUseCase
import com.lavenderlang.domain.usecase.language.CopyLanguageUseCase
import com.lavenderlang.domain.usecase.language.CreateLanguageUseCase
import com.lavenderlang.domain.usecase.language.DeleteLanguageUseCase
import com.lavenderlang.domain.usecase.language.WriteToPdfUseCase
import com.lavenderlang.domain.usecase.language.GetLanguageUseCase
import com.lavenderlang.domain.usecase.language.WriteToJonUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class LanguageFragment: Fragment() {
    private lateinit var createJSONLauncher: ActivityResultLauncher<String>
    private lateinit var createPDFLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentLanguageBinding

    companion object{
        var idLang: Int = -1
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
                WriteToJonUseCase.execute(uri, MyApp.language!!, requireContext())
            } else {
                Log.d("json write", "uri is null")
            }
        }

        createPDFLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            Log.d("pdf start write", "result")
            if (uri != null) {
                WriteToPdfUseCase.execute(uri, MyApp.language!!, requireContext())
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
                // get nextLanguageId from shared preferences
                idLang = preferences.getInt("nextLanguageId", 0)
                runBlocking {
                    withContext(Dispatchers.IO) {
                        MyApp.language = CreateLanguageUseCase.execute(
                            "Язык$idLang",
                            "",
                            LanguageRepositoryImpl(),
                            idLang
                        )
                    }
                }
                preferences.edit().putInt("lang", idLang).apply()
                preferences.edit().putInt("nextLanguageId", idLang + 1).apply()
            }

            else -> {
                idLang = lang
                runBlocking(Dispatchers.IO) {
                    MyApp.language = GetLanguageUseCase.execute(lang, LanguageRepositoryImpl())
                    if (MyApp.language == null) {
                        idLang = preferences.getInt("nextLanguageId", 0)
                        MyApp.language = CreateLanguageUseCase.execute(
                            "Язык$idLang",
                            "",
                            LanguageRepositoryImpl(),
                            idLang
                        )
                        preferences.edit().putInt("nextLanguageId", idLang + 1).apply()
                    }
                    preferences.edit().putInt("lang", idLang).apply()
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
                    ChangeNameUseCase.execute(MyApp.language!!, binding.editLanguageName.text.toString(), LanguageRepositoryImpl())
                }
            }
        })
        binding.editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                runBlocking {
                    ChangeDescriptionUseCase.execute(MyApp.language!!, binding.editDescription.text.toString(), LanguageRepositoryImpl())
                }
            }
        })

        binding.buttonFile.setOnClickListener {
            try {
                createJSONLauncher.launch("${PdfWriterDaoImpl().translitName(
                    MyApp.language!!.name)}.json")
            } catch (e: FileWorkException) {
                Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        binding.buttonPDF.setOnClickListener {
            try {
                createPDFLauncher.launch(
                    "${PdfWriterDaoImpl().translitName(
                        MyApp.language!!.name)}.pdf")
            } catch (e: FileWorkException) {
                Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        binding.buttonCopy.setOnClickListener {
            runBlocking {
                val nextLanguageId = preferences.getInt("nextLanguageId", 0)
                MyApp.language = CopyLanguageUseCase.execute(MyApp.language!!, nextLanguageId, LanguageRepositoryImpl())
                preferences.edit().putInt("nextLanguageId", nextLanguageId + 1).apply()
                preferences.edit().putInt("lang", nextLanguageId).apply()
            }
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        binding.buttonDelete.setOnClickListener {
            runBlocking {
                DeleteLanguageUseCase.execute(MyApp.language!!.languageId, LanguageRepositoryImpl())
                MyApp.language = null
                preferences.edit().putInt("lang", -1).apply()
            }
            findNavController().navigate(R.id.action_languageFragment_to_mainFragment)
        }
        return binding.root
    }
}