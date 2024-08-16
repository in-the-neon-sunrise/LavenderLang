package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.exception.IncorrectRegexException
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.databinding.FragmentGrammarRuleBinding
import com.lavenderlang.ui.MyApp

class GrammarRuleFragment : Fragment() {
    private lateinit var binding: FragmentGrammarRuleBinding
    companion object{
        var idLang: Int = 0
        var idRule: Int = 0

        var idPartOfSpeech: Int = 0
        var attrs: MutableMap<Attributes, Int> = mutableMapOf()
        var regex: String = ".*"
        var mutableAttrs: MutableMap<Attributes, Int> = mutableMapOf()
        var numberFront=0
        var numberBack=0
        var addFront=""
        var addBack=""

        val grammarDao = GrammarDaoImpl()
        val grammarRuleDao = GrammarRuleDaoImpl()
        val mascDao = MascDaoImpl()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGrammarRuleBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("block", 8)
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
            findNavController().navigate(R.id.action_grammarRuleFragment_to_translatorFragment)
        }
        //val editMasc: EditText = findViewById(R.id.editMasc)
        //how it was started?
        when (val lang =
            requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_grammarRuleFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }

        var rule = arguments?.getInt("rule", -1) ?: -1
        if (rule == -1 && idRule != 0) {
            rule = LanguageFragment.idLang
        }
        when(rule) {
            -1 -> {
                val newRule = GrammarRuleEntity(idLang)
                grammarDao.addGrammarRule(MyApp.language!!.grammar, newRule)
                idRule = MyApp.language!!.grammar.grammarRules.size-1
                binding.editMasc.setText(newRule.masc.regex)
            }
            else -> {
                idRule = rule
                binding.editMasc.setText(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.regex)
            }
        }


        attrs = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs
        regex = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.regex
        mutableAttrs = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs


        listenSpinners()
        updateSpinners()

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Предлог/частица/..."))

        binding.spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        var partOfSpeech= MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.partOfSpeech
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
        binding.spinnerPartOfSpeech.setSelection(idPartOfSpeech)

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
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.NOUN)
                        binding.spinnerGender.visibility=View.VISIBLE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.VISIBLE
                        binding.spinnerFinishCase.visibility=View.VISIBLE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech){
                            //Toast.makeText(this@GrammarRuleActivity, "задаем в первый раз чр", Toast.LENGTH_LONG).show()
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    1->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.VERB)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.VISIBLE
                        binding.spinnerVoice.visibility=View.VISIBLE

                        binding.spinnerFinishGender.visibility=View.VISIBLE
                        binding.spinnerFinishNumber.visibility=View.VISIBLE
                        binding.spinnerFinishCase.visibility=View.GONE
                        binding.spinnerFinishTime.visibility=View.VISIBLE
                        binding.spinnerFinishPerson.visibility=View.VISIBLE
                        binding.spinnerFinishMood.visibility=View.VISIBLE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    2->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.ADJECTIVE)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.VISIBLE
                        binding.spinnerFinishNumber.visibility=View.VISIBLE
                        binding.spinnerFinishCase.visibility=View.VISIBLE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.VISIBLE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    3->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.ADVERB)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.GONE
                        binding.spinnerFinishCase.visibility=View.GONE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    4->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.PARTICIPLE)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.VISIBLE
                        binding.spinnerVoice.visibility=View.VISIBLE

                        binding.spinnerFinishGender.visibility=View.VISIBLE
                        binding.spinnerFinishNumber.visibility=View.VISIBLE
                        binding.spinnerFinishCase.visibility=View.VISIBLE
                        binding.spinnerFinishTime.visibility=View.VISIBLE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    5->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.VERB_PARTICIPLE)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.VISIBLE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.GONE
                        binding.spinnerFinishCase.visibility=View.GONE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    6->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.PRONOUN)
                        binding.spinnerGender.visibility=View.VISIBLE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.VISIBLE
                        binding.spinnerFinishCase.visibility=View.VISIBLE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    7->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.NUMERAL)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.GONE
                        binding.spinnerFinishCase.visibility=View.GONE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                    else->{
                        mascDao.changePartOfSpeech(
                            MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.FUNC_PART)
                        binding.spinnerGender.visibility=View.GONE
                        binding.spinnerType.visibility=View.GONE
                        binding.spinnerVoice.visibility=View.GONE

                        binding.spinnerFinishGender.visibility=View.GONE
                        binding.spinnerFinishNumber.visibility=View.GONE
                        binding.spinnerFinishCase.visibility=View.GONE
                        binding.spinnerFinishTime.visibility=View.GONE
                        binding.spinnerFinishPerson.visibility=View.GONE
                        binding.spinnerFinishMood.visibility=View.GONE
                        binding.spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner== idPartOfSpeech)return
                        idPartOfSpeech =positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateRule()
                        updateSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mascDao.changePartOfSpeech(
                    MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc, PartOfSpeech.NOUN)
            }
        }

        // edit texts

        numberFront = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].transformation.delFromBeginning
        numberBack = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].transformation.delFromEnd
        addFront = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].transformation.addToBeginning
        addBack = MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].transformation.addToEnd
        (binding.editTextNumberFront as TextView).setText(numberFront.toString())
        (binding.editTextNumberBack as TextView).setText(numberBack.toString())
        (binding.editTextAddFront as TextView).setText(addFront)
        (binding.editTextAddBack as TextView).setText(addBack)


        binding.buttonSaveGrammarRule.setOnClickListener{
            Log.d("save", "save grammar rule")
            if(binding.spinnerGender.isVisible) attrs[Attributes.GENDER] = binding.spinnerGender.selectedItemPosition
            if(binding.spinnerType.isVisible) attrs[Attributes.TYPE] = binding.spinnerType.selectedItemPosition
            if(binding.spinnerVoice.isVisible) attrs[Attributes.VOICE] = binding.spinnerVoice.selectedItemPosition

            regex =binding.editMasc.text.toString()

            if(binding.spinnerFinishGender.isVisible) mutableAttrs[Attributes.GENDER] = binding.spinnerFinishGender.selectedItemPosition
            if(binding.spinnerFinishNumber.isVisible) mutableAttrs[Attributes.NUMBER] = binding.spinnerFinishNumber.selectedItemPosition
            if(binding.spinnerFinishCase.isVisible) mutableAttrs[Attributes.CASE] = binding.spinnerFinishCase.selectedItemPosition
            if(binding.spinnerFinishTime.isVisible) mutableAttrs[Attributes.TIME] = binding.spinnerFinishTime.selectedItemPosition
            if(binding.spinnerFinishPerson.isVisible) mutableAttrs[Attributes.PERSON] = binding.spinnerFinishPerson.selectedItemPosition
            if(binding.spinnerFinishMood.isVisible) mutableAttrs[Attributes.MOOD] = binding.spinnerFinishMood.selectedItemPosition
            if(binding.spinnerFinishDegreeOfComparison.isVisible) mutableAttrs[Attributes.DEGREE_OF_COMPARISON] = binding.spinnerFinishDegreeOfComparison.selectedItemPosition

            addBack =binding.editTextAddBack.text.toString()
            addFront =binding.editTextAddFront.text.toString()
            numberFront = if (binding.editTextNumberFront.text.toString().isNotEmpty())
                binding.editTextNumberFront.text.toString().toInt()
            else 0
            numberBack = if (binding.editTextNumberBack.text.toString().isNotEmpty())
                binding.editTextNumberBack.text.toString().toInt()
            else 0

            updateRule()

        }

        binding.buttonDelete.setOnClickListener{
            grammarDao.deleteGrammarRule(MyApp.language!!.grammar, MyApp.language!!.grammar.grammarRules.toMutableList()[idRule])
            findNavController().popBackStack()
        }
        return binding.root
    }

    fun updateRule(){
        var partOfSpeech=when(idPartOfSpeech){
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
            var newMasc = MascEntity(partOfSpeech, attrs, regex)
            var newTransformation = TransformationEntity(numberFront, numberBack, addFront, addBack)
            grammarRuleDao.updateRule(
                MyApp.language!!.grammar.grammarRules.toMutableList()[idRule],
                newMasc,
                newTransformation,
                mutableAttrs
            )
            Log.d("rule in frag", "rule: ${MyApp.language!!.grammar.grammarRules.toMutableList()[idRule]}")
        }catch (e: IncorrectRegexException){
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
        var genderAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderNames)
        binding.spinnerGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()

        val typeNames = MyApp.language!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeNames)
        binding.spinnerType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = MyApp.language!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, voiceNames)
        binding.spinnerVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()

        val finishGenderAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderNames)
        binding.spinnerFinishGender.adapter = finishGenderAdapter
        finishGenderAdapter.notifyDataSetChanged()

        val numberNames = MyApp.language!!.grammar.varsNumber.values.map { it.name }
        val finishNumberAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, numberNames)
        binding.spinnerFinishNumber.adapter = finishNumberAdapter
        finishNumberAdapter.notifyDataSetChanged()

        val caseNames = MyApp.language!!.grammar.varsCase.values.map { it.name }
        val finishCaseAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, caseNames)
        binding.spinnerFinishCase.adapter = finishCaseAdapter
        finishCaseAdapter.notifyDataSetChanged()

        val timeNames = MyApp.language!!.grammar.varsTime.values.map { it.name }
        val finishTimeAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timeNames)
        binding.spinnerFinishTime.adapter = finishTimeAdapter
        finishTimeAdapter.notifyDataSetChanged()

        val personNames = MyApp.language!!.grammar.varsPerson.values.map { it.name }
        val finishPersonAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, personNames)
        binding.spinnerFinishPerson.adapter = finishPersonAdapter
        finishPersonAdapter.notifyDataSetChanged()

        val moodNames = MyApp.language!!.grammar.varsMood.values.map { it.name }
        val finishMoodAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, moodNames)
        binding.spinnerFinishMood.adapter = finishMoodAdapter
        finishMoodAdapter.notifyDataSetChanged()

        val degreeOfComparisonNames = MyApp.language!!.grammar.varsDegreeOfComparison.values.map { it.name }
        val finishDegreeOfComparisonAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, degreeOfComparisonNames)
        binding.spinnerFinishDegreeOfComparison.adapter = finishDegreeOfComparisonAdapter
        finishDegreeOfComparisonAdapter.notifyDataSetChanged()
    }
    fun updateSpinners(){
        when(idPartOfSpeech) {
            0 -> {
                binding.spinnerGender.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.GENDER] ?: 0)

                binding.spinnerFinishNumber.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.NUMBER] ?:0)
                binding.spinnerFinishCase.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.CASE] ?:0)
            }
            1->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerVoice.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.VOICE] ?: 0)

                binding.spinnerFinishGender.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.GENDER] ?:0)
                binding.spinnerFinishNumber.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.NUMBER] ?:0)
                binding.spinnerFinishTime.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.TIME] ?:0)
                binding.spinnerFinishPerson.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.PERSON] ?:0)
                binding.spinnerFinishMood.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.MOOD] ?:0)
            }
            2-> {
                binding.spinnerFinishGender.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.GENDER] ?:0)
                binding.spinnerFinishNumber.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.NUMBER] ?:0)
                binding.spinnerFinishCase.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.CASE] ?:0)
                binding.spinnerFinishDegreeOfComparison.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.DEGREE_OF_COMPARISON] ?:0)
            }
            3->{}
            4->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerVoice.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.VOICE] ?: 0)

                binding.spinnerFinishGender.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.GENDER] ?:0)
                binding.spinnerFinishNumber.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.NUMBER] ?:0)
                binding.spinnerFinishCase.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.CASE] ?:0)
                binding.spinnerFinishTime.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.TIME] ?:0)
            }
            5->{
                binding.spinnerType.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                binding.spinnerGender.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].masc.immutableAttrs[Attributes.GENDER] ?: 0)

                binding.spinnerFinishNumber.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.NUMBER] ?:0)
                binding.spinnerFinishCase.setSelection(MyApp.language!!.grammar.grammarRules.toMutableList()[idRule].mutableAttrs[Attributes.CASE] ?:0)
            }
            7->{}
            else->{}
        }
    }
}