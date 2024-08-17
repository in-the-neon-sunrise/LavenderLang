package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.WritingDao
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.databinding.FragmentWritingBinding
import com.lavenderlang.ui.MyApp

class WritingFragment : Fragment() {
    private lateinit var binding: FragmentWritingBinding
    companion object {
        var idLang: Int = 0
        val writingDao: WritingDao = WritingDaoImpl()
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
            try{
                writingDao.changeVowels(MyApp.language!!, binding.editTextVowels.editText?.text.toString())
                writingDao.changeConsonants(MyApp.language!!, binding.editTextConsonants.editText?.text.toString())
            }catch (e: ForbiddenSymbolsException){
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

            if(binding.checkBoxNoun.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NOUN)

            if(binding.checkBoxVerb.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB)

            if(binding.checkBoxAdjective.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADJECTIVE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADJECTIVE)

            if(binding.checkBoxAdverb.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADVERB)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADVERB)

            if(binding.checkBoxParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PARTICIPLE)

            if(binding.checkBoxVerbParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB_PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB_PARTICIPLE)

            if(binding.checkBoxPronoun.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PRONOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PRONOUN)

            if(binding.checkBoxNumeral.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NUMERAL)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NUMERAL)

            if(binding.checkBoxFuncPart.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.FUNC_PART)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.FUNC_PART)
        }

        return binding.root
    }
}