package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.databinding.FragmentWordFormationRuleBinding
import com.lavenderlang.domain.usecase.grammar.AddWordFormationRuleUseCase
import com.lavenderlang.domain.usecase.grammar.DeleteWordFormationRuleUseCase
import com.lavenderlang.domain.usecase.grammar.UpdateWordFormationRuleUseCase
import com.lavenderlang.domain.usecase.update.UpdateGrammarUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordFormationRuleFragment : Fragment() {
    private lateinit var binding: FragmentWordFormationRuleBinding
    companion object {
        var idLang: Int = 0
        var idRule: Int = 0

        var idPartOfSpeech: Int = 0
        var attrs: MutableMap<Attributes, Int> = mutableMapOf()
        var regex: String = ".*"

        var finishIdPartOfSpeech: Int = 0
        var finishAttrs: MutableMap<Attributes, Int> = mutableMapOf()

        var numberFront=0
        var numberBack=0
        var addFront=""
        var addBack=""

        var description: String = ""

        var startFlagIsFirst = false
        var finishFlagIsFirst = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWordFormationRuleBinding.inflate(inflater, container, false)

        when(val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)){
            -1 -> {
                findNavController().navigate(R.id.action_grammarRuleFragment_to_languageFragment)
            }
            else -> {
                idLang = lang
            }
        }
        var rule = arguments?.getInt("rule", -1) ?: -1
        when(rule){
            -1 -> {
                var newRule = WordFormationRuleEntity(idLang)
                AddWordFormationRuleUseCase.execute(newRule, MyApp.language!!.grammar)
                lifecycleScope.launch(Dispatchers.IO) {
                    UpdateGrammarUseCase.execute(MyApp.language!!.grammar, LanguageRepositoryImpl())
                }
                idRule = MyApp.language!!.grammar.wordFormationRules.size-1
                binding.editMasc.editText?.setText(newRule.masc.regex)
            }
            else -> {
                idRule = rule
                binding.editMasc.editText?.setText(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.regex)
            }
        }
        startFlagIsFirst =true
        finishFlagIsFirst =true

        var partOfSpeech= MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.partOfSpeech
        when (partOfSpeech){
            PartOfSpeech.NOUN-> idPartOfSpeech =0
            PartOfSpeech.VERB-> idPartOfSpeech =1
            PartOfSpeech.ADJECTIVE-> idPartOfSpeech =2
            PartOfSpeech.ADVERB-> idPartOfSpeech =3
            PartOfSpeech.PARTICIPLE-> idPartOfSpeech =4
            PartOfSpeech.VERB_PARTICIPLE-> idPartOfSpeech =5
            PartOfSpeech.PRONOUN-> idPartOfSpeech =6
            PartOfSpeech.NUMERAL-> idPartOfSpeech =7
            PartOfSpeech.FUNC_PART-> idPartOfSpeech =8
        }

        attrs = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs
        regex = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.regex

        partOfSpeech= MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech
        finishIdPartOfSpeech = when (partOfSpeech) {
            PartOfSpeech.NOUN-> 0
            PartOfSpeech.VERB-> 1
            PartOfSpeech.ADJECTIVE-> 2
            PartOfSpeech.ADVERB-> 3
            PartOfSpeech.PARTICIPLE-> 4
            PartOfSpeech.VERB_PARTICIPLE-> 5
            PartOfSpeech.PRONOUN-> 6
            PartOfSpeech.NUMERAL-> 7
            PartOfSpeech.FUNC_PART-> 8
        }
        finishAttrs = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs

        numberFront = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].transformation.delFromBeginning
        numberBack = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].transformation.delFromEnd
        addFront = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].transformation.addToBeginning
        addBack = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].transformation.addToEnd

        description = MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].description

        listenSpinners()
        updateStartSpinners()
        updateFinishSpinners()

        binding.editTextDescriptionRule.editText?.setText(description)

        binding.editTextNumberFront.editText?.setText(numberFront.toString())
        binding.editTextNumberBack.editText?.setText(numberBack.toString())
        binding.editTextAddFront.editText?.setText(addFront)
        binding.editTextAddBack.editText?.setText(addBack)

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Служебное слово"))

        binding.spinnerPartOfSpeech.adapter = spinnerAdapter
        binding.spinnerFinishPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        binding.spinnerPartOfSpeech.setSelection(idPartOfSpeech)
        binding.spinnerFinishPartOfSpeech.setSelection(finishIdPartOfSpeech)

        binding.spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        partOfSpeech = PartOfSpeech.NOUN
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    1->{
                        partOfSpeech = PartOfSpeech.VERB
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    2->{
                        partOfSpeech = PartOfSpeech.ADJECTIVE
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    3->{
                        partOfSpeech = PartOfSpeech.ADVERB
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    4->{
                        partOfSpeech = PartOfSpeech.PARTICIPLE
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    5->{
                        partOfSpeech = PartOfSpeech.VERB_PARTICIPLE
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    6->{
                        partOfSpeech = PartOfSpeech.PRONOUN
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    7->{
                        partOfSpeech = PartOfSpeech.NUMERAL
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                    else->{
                        partOfSpeech = PartOfSpeech.FUNC_PART
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateStartSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                partOfSpeech = PartOfSpeech.NOUN
            }
        }
        binding.spinnerFinishPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.NOUN
                        binding.spinnerFinishGender.visibility= View.VISIBLE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    1->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.VERB
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.VISIBLE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    2->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.ADJECTIVE
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    3->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.ADVERB
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    4->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.PARTICIPLE
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.VISIBLE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    5->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.VERB_PARTICIPLE
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    6->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.PRONOUN
                        binding.spinnerFinishGender.visibility= View.VISIBLE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    7->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.NUMERAL
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                    else->{
                        MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].partOfSpeech = PartOfSpeech.FUNC_PART
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == finishIdPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateFinishSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.partOfSpeech = PartOfSpeech.NOUN
            }
        }

        binding.buttonSaveWordFormationRule.setOnClickListener{
            attrs = mutableMapOf()
            if(binding.spinnerGender.isVisible) attrs[Attributes.GENDER] = binding.spinnerGender.selectedItemPosition
            if(binding.spinnerType.isVisible) attrs[Attributes.TYPE] = binding.spinnerType.selectedItemPosition
            if(binding.spinnerVoice.isVisible) attrs[Attributes.VOICE] = binding.spinnerVoice.selectedItemPosition

            regex = binding.editMasc.editText?.text.toString()
            description = binding.editTextDescriptionRule.editText?.text.toString()

            finishAttrs = mutableMapOf()
            if(binding.spinnerFinishGender.isVisible) finishAttrs[Attributes.GENDER] = binding.spinnerFinishGender.selectedItemPosition
            if(binding.spinnerFinishType.isVisible) finishAttrs[Attributes.TYPE] = binding.spinnerFinishType.selectedItemPosition
            if(binding.spinnerFinishVoice.isVisible) finishAttrs[Attributes.VOICE] = binding.spinnerFinishVoice.selectedItemPosition

            numberFront = if (binding.editTextNumberFront.editText?.text.toString().isNotEmpty())
                binding.editTextNumberFront.editText?.text.toString().toInt()
            else 0
            numberBack = if (binding.editTextNumberBack.editText?.text.toString().isNotEmpty())
                binding.editTextNumberBack.editText?.text.toString().toInt()
            else 0
            addFront = binding.editTextAddFront.editText?.text.toString()
            addBack = binding.editTextAddBack.editText?.text.toString()

            if (!updateRule()) return@setOnClickListener

            partOfSpeech = when(idPartOfSpeech){
                0-> PartOfSpeech.NOUN
                1-> PartOfSpeech.VERB
                2-> PartOfSpeech.ADJECTIVE
                3-> PartOfSpeech.ADVERB
                4-> PartOfSpeech.PARTICIPLE
                5-> PartOfSpeech.VERB_PARTICIPLE
                6-> PartOfSpeech.PRONOUN
                7-> PartOfSpeech.NUMERAL
                else-> PartOfSpeech.FUNC_PART
            }
            val newMasc = MascEntity(partOfSpeech, attrs, regex)
            val newTransformation = TransformationEntity(numberFront, numberBack, addFront, addBack)
            val finishPartOfSpeech = when(finishIdPartOfSpeech){
                0-> PartOfSpeech.NOUN
                1-> PartOfSpeech.VERB
                2-> PartOfSpeech.ADJECTIVE
                3-> PartOfSpeech.ADVERB
                4-> PartOfSpeech.PARTICIPLE
                5-> PartOfSpeech.VERB_PARTICIPLE
                6-> PartOfSpeech.PRONOUN
                7-> PartOfSpeech.NUMERAL
                else-> PartOfSpeech.FUNC_PART
            }
            UpdateWordFormationRuleUseCase.execute(
                MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule],
                newMasc,
                newTransformation,
                description,
                finishAttrs,
                finishPartOfSpeech
            )
            lifecycleScope.launch(Dispatchers.IO) {
                UpdateGrammarUseCase.execute(MyApp.language!!.grammar, LanguageRepositoryImpl())
            }
        }
        binding.buttonDelete.setOnClickListener{
            DeleteWordFormationRuleUseCase.execute(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], MyApp.language!!.grammar)
            lifecycleScope.launch(Dispatchers.IO) {
                UpdateGrammarUseCase.execute(MyApp.language!!.grammar, LanguageRepositoryImpl())
            }
            findNavController().popBackStack()
        }

        return binding.root
    }
    private fun updateRule() : Boolean {
        val partOfSpeech=when(idPartOfSpeech){
            0-> PartOfSpeech.NOUN
            1-> PartOfSpeech.VERB
            2-> PartOfSpeech.ADJECTIVE
            3-> PartOfSpeech.ADVERB
            4-> PartOfSpeech.PARTICIPLE
            5-> PartOfSpeech.VERB_PARTICIPLE
            6-> PartOfSpeech.PRONOUN
            7-> PartOfSpeech.NUMERAL
            else-> PartOfSpeech.FUNC_PART
        }
        try {
            val newMasc = MascEntity(partOfSpeech, attrs, regex)
            val newTransformation = TransformationEntity(numberFront, numberBack, addFront, addBack)

            try {
                newMasc.regex.toRegex()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Неверное регулярное выражение!",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }

            for (letter in newTransformation.addToBeginning) {
                if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                    !MyApp.language!!.consonants.contains(letter.lowercase())
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Буква $letter не находится в алфавите языка!",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
            for (letter in newTransformation.addToEnd) {
                if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                    !MyApp.language!!.consonants.contains(letter.lowercase())
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Буква $letter не находится в алфавите языка!",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
        }
        catch (e:Exception){
            Toast.makeText(requireContext(), "какая-то беда", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    private fun listenSpinners(){

        val genderNames = MyApp.language!!.grammar.varsGender.values.map { it.name }
        val genderAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderNames)
        binding.spinnerGender.adapter = genderAdapter
        binding.spinnerFinishGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()


        val typeNames = MyApp.language!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeNames)
        binding.spinnerType.adapter = typeAdapter
        binding.spinnerFinishType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = MyApp.language!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, voiceNames)
        binding.spinnerVoice.adapter = voiceAdapter
        binding.spinnerFinishVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()
    }
    fun updateStartSpinners(){

        when(idPartOfSpeech) {
            0 -> {
                binding.spinnerGender.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerVoice.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2->{}
            3->{}
            4->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerVoice.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                binding.spinnerGender.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
    fun updateFinishSpinners(){
        when(idPartOfSpeech) {
            0 -> {
                binding.spinnerFinishGender.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                binding.spinnerFinishType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerFinishVoice.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2->{}
            3->{}
            4->{
                binding.spinnerFinishType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerFinishVoice.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                binding.spinnerFinishType.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                binding.spinnerFinishGender.setSelection(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
}