package com.lavenderlang.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.FragmentTranslatorBinding
import com.lavenderlang.domain.usecase.language.GetLanguageUseCase
import com.lavenderlang.domain.usecase.language.GetShortLanguagesUseCase
import com.lavenderlang.domain.usecase.translator.TranslateFromConlangUseCase
import com.lavenderlang.domain.usecase.translator.TranslateToConlangUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TranslatorFragment : Fragment() {

    private lateinit var binding: FragmentTranslatorBinding

    companion object{
        var idLang = 0
        var translationOnConlang = false
        var flagIsSpinnerSelected = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTranslatorBinding.inflate(inflater, container, false)

        flagIsSpinnerSelected = false


        idLang = requireContext().getSharedPreferences("pref", MODE_PRIVATE).getInt("lang", 0)
        val languages = runBlocking {
            withContext(Dispatchers.IO) {
                GetShortLanguagesUseCase.execute(LanguageRepositoryImpl())
            }
        }
        val languageNames = languages.map { it.name }
        val adapterLanguages: ArrayAdapter<String> = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, languageNames
        )
        binding.spinnerChooseLanguage.adapter = adapterLanguages
        adapterLanguages.notifyDataSetChanged()
        flagIsSpinnerSelected =true
        val stupidId = languages.indexOfFirst { it.id == idLang }
        if(idLang !=-1) binding.spinnerChooseLanguage.setSelection(stupidId)
        binding.toggleButton.check(R.id.buttonFromConlang)//перевод с конланга

        binding.buttonFromConlang.setOnClickListener {
            translationOnConlang = false
            binding.toggleButton.uncheck(R.id.buttonOnConlang)
        }
        binding.buttonOnConlang.setOnClickListener {
            translationOnConlang = true
            binding.toggleButton.uncheck(R.id.buttonFromConlang)
        }

        binding.spinnerChooseLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) {
                if(flagIsSpinnerSelected) {
                    idLang = languages[position].id
                    val preferences = requireContext().getSharedPreferences("pref", MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putInt("lang", idLang)
                    prefEditor.apply()
                    if (idLang != (MyApp.language?.languageId ?: -2)) {
                        runBlocking {
                            MyApp.language = GetLanguageUseCase.execute(
                                idLang,
                                LanguageRepositoryImpl()
                            )
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                idLang = 0
            }
        }

        binding.buttonTranslate.setOnClickListener {
            translate()
        }

        return binding.root
    }

    private fun translate() {

        val inputText: String = binding.editTextText.editText?.text.toString()

        if (!translationOnConlang) {
            binding.textViewTranslation.text =
                TranslateFromConlangUseCase.execute(MyApp.language!!, inputText)
        } else {
            binding.textViewTranslation.text =
                TranslateToConlangUseCase.execute(MyApp.language!!, inputText)
        }
    }

}