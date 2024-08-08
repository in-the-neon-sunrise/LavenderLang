package com.lavenderlang.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDao
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.help.TransformationEntity
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.exception.IncorrectRegexException
import com.lavenderlang.databinding.FragmentLanguageBinding
import com.lavenderlang.databinding.FragmentWordFormationRuleBinding
import com.lavenderlang.frontend.MyApp

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

        val mascDao = MascDaoImpl()
        val grammarDao: GrammarDao = GrammarDaoImpl()
        val wordFormationRuleDao: WordFormationRuleDao = WordFormationRuleDaoImpl()

        var startFlagIsFirst = false
        var finishFlagIsFirst = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWordFormationRuleBinding.inflate(inflater, container, false)
        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 9)
            findNavController().navigate(
                R.id.action_grammarRuleFragment_to_instructionFragment,
                argsToSend
            )
        }
        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_grammarRuleFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_grammarRuleFragment_to_languageFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            findNavController().navigate(R.id.action_wordFormationRuleFragment_to_translatorFragment)
        }

        when(val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)){
            -1 -> {
                findNavController().navigate(R.id.action_grammarRuleFragment_to_languageFragment)
            }
            else -> {
                idLang = lang
            }
        }
        var rule = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("wordFormationRule", -1)
        when(rule){
            -1 -> {
                var newRule = WordFormationRuleEntity(idLang)
                grammarDao.addWordFormationRule(MyApp.language!!.grammar, newRule)
                idRule = MyApp.language!!.grammar.wordFormationRules.size-1
                binding.editMasc.setText(newRule.masc.regex)
            }
            else -> {
                idRule = rule
                binding.editMasc.setText(MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc.regex)
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
        when (partOfSpeech){
            PartOfSpeech.NOUN-> finishIdPartOfSpeech =0
            PartOfSpeech.VERB-> finishIdPartOfSpeech =1
            PartOfSpeech.ADJECTIVE-> finishIdPartOfSpeech =2
            PartOfSpeech.ADVERB-> finishIdPartOfSpeech =3
            PartOfSpeech.PARTICIPLE-> finishIdPartOfSpeech =4
            PartOfSpeech.VERB_PARTICIPLE-> finishIdPartOfSpeech =5
            PartOfSpeech.PRONOUN-> finishIdPartOfSpeech =6
            PartOfSpeech.NUMERAL-> finishIdPartOfSpeech =7
            PartOfSpeech.FUNC_PART-> finishIdPartOfSpeech =8
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

        binding.editTextDescriptionRule.setText(description)

        (binding.editTextNumberFront as TextView).text = numberFront.toString()
        (binding.editTextNumberBack as TextView).text = numberBack.toString()
        (binding.editTextAddFront as TextView).text = addFront
        (binding.editTextAddBack as TextView).text = addBack

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
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.NOUN)
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    1->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.VERB)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    2->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.ADJECTIVE)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    3->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.ADVERB)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    4->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.PARTICIPLE)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    5->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.VERB_PARTICIPLE)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    6->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.PRONOUN)
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    7->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.NUMERAL)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                    else->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.FUNC_PART)
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE


                        if(positionSpinner == idPartOfSpeech || startFlagIsFirst){
                            startFlagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        updateRule()
                        updateStartSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mascDao.changePartOfSpeech(
                    MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.NOUN)
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
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.NOUN)
                        binding.spinnerFinishGender.visibility= View.VISIBLE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    1->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.VERB)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    2->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.ADJECTIVE)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    3->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.ADVERB)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    4->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.PARTICIPLE)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    5->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.VERB_PARTICIPLE)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.VISIBLE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    6->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.PRONOUN)
                        binding.spinnerFinishGender.visibility= View.VISIBLE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    7->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.NUMERAL)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                    else->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule], PartOfSpeech.FUNC_PART)
                        binding.spinnerFinishGender.visibility= View.GONE
                        binding.spinnerFinishType.visibility= View.GONE
                        binding.spinnerFinishVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || finishFlagIsFirst){
                            finishFlagIsFirst =false
                            return
                        }
                        finishIdPartOfSpeech = positionSpinner
                        finishAttrs = mutableMapOf()
                        updateRule()
                        updateFinishSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mascDao.changePartOfSpeech(
                    MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule].masc, PartOfSpeech.NOUN)
            }
        }

        binding.buttonSaveWordFormationRule.setOnClickListener{
            if(binding.spinnerGender.isVisible) attrs[Attributes.GENDER] = binding.spinnerGender.selectedItemPosition
            if(binding.spinnerType.isVisible) attrs[Attributes.TYPE] = binding.spinnerType.selectedItemPosition
            if(binding.spinnerVoice.isVisible) attrs[Attributes.VOICE] = binding.spinnerVoice.selectedItemPosition

            regex = binding.editMasc.text.toString()
            description = binding.editTextDescriptionRule.text.toString()

            if(binding.spinnerFinishGender.isVisible) finishAttrs[Attributes.GENDER] = binding.spinnerFinishGender.selectedItemPosition
            if(binding.spinnerFinishType.isVisible) finishAttrs[Attributes.TYPE] = binding.spinnerFinishType.selectedItemPosition
            if(binding.spinnerFinishVoice.isVisible) finishAttrs[Attributes.VOICE] = binding.spinnerFinishVoice.selectedItemPosition

            numberFront = if (binding.editTextNumberFront.text.toString().isNotEmpty())
                binding.editTextNumberFront.text.toString().toInt()
            else 0
            numberBack = if (binding.editTextNumberBack.text.toString().isNotEmpty())
                binding.editTextNumberBack.text.toString().toInt()
            else 0
            addFront = binding.editTextAddFront.text.toString()
            addBack = binding.editTextAddBack.text.toString()

            updateRule()
        }
        binding.buttonDelete.setOnClickListener{
            grammarDao.deleteWordFormationRule(MyApp.language!!.grammar, MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule])
            findNavController().popBackStack()
        }

        return binding.root
    }
    fun updateRule(){
        var partOfSpeech=when(idPartOfSpeech){
            0->PartOfSpeech.NOUN
            1->PartOfSpeech.VERB
            2->PartOfSpeech.ADJECTIVE
            3->PartOfSpeech.ADVERB
            4->PartOfSpeech.PARTICIPLE
            5->PartOfSpeech.VERB_PARTICIPLE
            6->PartOfSpeech.PRONOUN
            7->PartOfSpeech.NUMERAL
            else->PartOfSpeech.FUNC_PART
        }
        try {
            var newMasc = MascEntity(partOfSpeech, attrs, regex)
            partOfSpeech = when (finishIdPartOfSpeech) {
                0 -> PartOfSpeech.NOUN
                1 -> PartOfSpeech.VERB
                2 -> PartOfSpeech.ADJECTIVE
                3 -> PartOfSpeech.ADVERB
                4 -> PartOfSpeech.PARTICIPLE
                5 -> PartOfSpeech.VERB_PARTICIPLE
                6 -> PartOfSpeech.PRONOUN
                7 -> PartOfSpeech.NUMERAL
                else -> PartOfSpeech.FUNC_PART
            }
            var newTransformation = TransformationEntity(numberFront, numberBack, addFront, addBack)

            wordFormationRuleDao.updateRule(
                MyApp.language!!.grammar.wordFormationRules.toMutableList()[idRule],
                newMasc,
                newTransformation,
                description,
                finishAttrs,
                partOfSpeech
            )
        }
        catch(e: IncorrectRegexException){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
        catch (e: ForbiddenSymbolsException){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
        catch (e:Exception){
            Toast.makeText(requireContext(), "какая-то беда", Toast.LENGTH_LONG).show()
        }
    }
    fun listenSpinners(){

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