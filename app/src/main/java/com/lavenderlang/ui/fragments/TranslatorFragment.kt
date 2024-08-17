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
import com.lavenderlang.backend.dao.language.TranslatorDaoImpl
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.FragmentTranslatorBinding
import com.lavenderlang.domain.usecase.language.GetLanguageUseCase
import com.lavenderlang.domain.usecase.language.GetShortLanguagesUseCase
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

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 10)
            findNavController().navigate(R.id.action_translatorFragment_to_instructionFragment, argsToSend)
        }

        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_translatorFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_translatorFragment_to_languageFragment)
        }

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
        binding.radioButtonFromConlang.isChecked = true//перевод с конланга
        binding.radioGroupTranslate.setOnCheckedChangeListener { _, checkedId ->
            translationOnConlang = checkedId != binding.radioButtonFromConlang.id
        }

        binding.spinnerChooseLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) {
                if(flagIsSpinnerSelected) {
                    idLang = languages[position].id
                    val preferences = requireContext().getSharedPreferences("pref", MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putInt("lang", idLang)
                    prefEditor.apply()
                    runBlocking {
                        MyApp.language = GetLanguageUseCase.execute(
                            idLang,
                            LanguageRepositoryImpl()
                        )
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

        val inputText: String = binding.editTextText.text.toString()

        val translatorDao = TranslatorDaoImpl()
        if (!translationOnConlang) {
            binding.textViewTranslation.text =
                translatorDao.translateTextFromConlang(MyApp.language!!, inputText)
        } else {
            binding.textViewTranslation.text =
                translatorDao.translateTextToConlang(MyApp.language!!, inputText)
        }
    }

}