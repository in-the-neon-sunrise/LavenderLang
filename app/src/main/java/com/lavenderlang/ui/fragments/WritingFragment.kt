package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.databinding.FragmentWritingBinding
import com.lavenderlang.domain.usecase.update.UpdateWritingUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WritingFragment : Fragment() {
    private lateinit var binding: FragmentWritingBinding
    companion object {
        var idLang: Int = 0
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWritingBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            argsToSend.putInt("block", 4)
            findNavController().navigate(
                R.id.action_writingFragment_to_instructionFragment,
                argsToSend
            )
        }
        //how it was started?
        when (val lang =
            requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_writingFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }

        //letters
        binding.editTextVowels.editText?.setText(MyApp.language!!.vowels)

        //symbols
        binding.editTextConsonants.editText?.setText(MyApp.language?.consonants)

        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NOUN)) binding.checkBoxNoun.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB)) binding.checkBoxVerb.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADJECTIVE)) binding.checkBoxAdjective.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADVERB)) binding.checkBoxAdverb.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PARTICIPLE)) binding.checkBoxParticiple.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB_PARTICIPLE)) binding.checkBoxVerbParticiple.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PRONOUN)) binding.checkBoxPronoun.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NUMERAL)) binding.checkBoxNumeral.isChecked = true
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.FUNC_PART)) binding.checkBoxFuncPart.isChecked = true

        binding.buttonSave.setOnClickListener {
            val newVowels = binding.editTextVowels.editText?.text.toString()
            val newConsonants = binding.editTextConsonants.editText?.text.toString()
            val language = MyApp.language!!
            for (letter in newVowels.lowercase()) {
                if (letter == ' ') continue
                if (language.consonants.contains(letter)) {
                    throw ForbiddenSymbolsException("Буква $letter уже находится в согласных!")
                }
                for (ps in language.puncSymbols.values) {
                    if (ps.lowercase().contains(letter)) {
                        throw ForbiddenSymbolsException("Буква $letter уже находится в символах пунктуации!")
                    }
                }
            }
            language.vowels = newVowels.lowercase()

            for (letter in newConsonants.lowercase()) {
                if (letter == ' ') continue
                if (language.vowels.contains(letter)) {
                    throw ForbiddenSymbolsException("Буква $letter уже находится в гласных!")
                }
                for (ps in language.puncSymbols.values) {
                    if (ps.lowercase().contains(letter)) {
                        throw ForbiddenSymbolsException("Буква $letter уже находится в символах пунктуации!")
                    }
                }
            }
            language.consonants = newConsonants.lowercase()

            val translation : Map<String, PartOfSpeech> = mapOf(
                "СУЩЕСТВИТЕЛЬНОЕ" to PartOfSpeech.NOUN,
                "ГЛАГОЛ" to PartOfSpeech.VERB,
                "ПРИЛАГАТЕЛЬНОЕ" to PartOfSpeech.ADJECTIVE,
                "НАРЕЧИЕ" to PartOfSpeech.ADVERB,
                "ПРИЧАСТИЕ" to PartOfSpeech.PARTICIPLE,
                "ДЕЕПРИЧАСТИЕ" to PartOfSpeech.VERB_PARTICIPLE,
                "МЕСТОИМЕНИЕ" to PartOfSpeech.PRONOUN,
                "ЧИСЛИТЕЛЬНОЕ" to PartOfSpeech.NUMERAL,
                "СЛУЖЕБНОЕ СЛОВО" to PartOfSpeech.FUNC_PART)

            for (checkBox in listOf(binding.checkBoxNoun, binding.checkBoxVerb, binding.checkBoxAdjective, binding.checkBoxAdverb, binding.checkBoxParticiple, binding.checkBoxVerbParticiple, binding.checkBoxPronoun, binding.checkBoxNumeral, binding.checkBoxFuncPart)) {
                val partOfSpeech = translation[checkBox.text.toString().uppercase()]!!
                if (checkBox.isChecked && !language.capitalizedPartsOfSpeech.contains(partOfSpeech))
                    language.capitalizedPartsOfSpeech.add(partOfSpeech)
                else if (!checkBox.isChecked)
                    language.capitalizedPartsOfSpeech.remove(partOfSpeech)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                UpdateWritingUseCase.execute(
                    newVowels, newConsonants, language.capitalizedPartsOfSpeech,
                    language.languageId, LanguageRepositoryImpl()
                )
            }
        }

        return binding.root
    }
}