package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.help.TransformationEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity

class GrammarRuleActivity: AppCompatActivity(){
    companion object{
        var id_lang: Int = 0
        var id_rule: Int = 0

        var isEverythingCreated: Boolean = false
        var idPartOfSpeech: Int = 0
        var attrs: MutableMap<Attributes, ArrayList<Int>> = mutableMapOf()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_rule_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@GrammarRuleActivity, GrammarActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@GrammarRuleActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val editMasc: EditText = findViewById(R.id.editMasc)

        when(val lang = intent.getIntExtra("lang", -1)){
            -1 -> {
                val intent = Intent(this@GrammarRuleActivity, GrammarActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }

        when(val rule = intent.getIntExtra("grammarRule", -1)){
            -1 -> {
                var newRule = GrammarRuleEntity(id_lang)
                grammarDao.addGrammarRule(languages[id_lang]!!.grammar, newRule)
                id_rule = languages[id_lang]!!.grammar.grammarRules.size-1
                editMasc.setText("это новое правило привет")
            }
            else -> {
                id_rule = rule
                editMasc.setText(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.regex)
            }
        }
        attrs = languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs
        regex = languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.regex
        mutableAttrs = languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs
        updateSpinners()

        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishNumber: Spinner = findViewById(R.id.spinnerFinishNumber)
        val spinnerFinishCase: Spinner = findViewById(R.id.spinnerFinishCase)
        val spinnerFinishTime: Spinner = findViewById(R.id.spinnerFinishTime)
        val spinnerFinishPerson: Spinner = findViewById(R.id.spinnerFinishPerson)
        val spinnerFinishMood: Spinner = findViewById(R.id.spinnerFinishMood)
        val spinnerFinishDegreeOfComparison: Spinner = findViewById(R.id.spinnerFinishDegreeOfComparison)

        listenSpinners()

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Предлог/частица/..."))

        spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        var partOfSpeech=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.partsOfSpeech
        when (partOfSpeech){
            PartOfSpeech.NOUN->idPartOfSpeech=0
            PartOfSpeech.VERB->idPartOfSpeech=1
            PartOfSpeech.ADJECTIVE->idPartOfSpeech=2
            PartOfSpeech.ADVERB->idPartOfSpeech=3
            PartOfSpeech.PARTICIPLE->idPartOfSpeech=4
            PartOfSpeech.VERB_PARTICIPLE->idPartOfSpeech=5
            PartOfSpeech.PRONOUN->idPartOfSpeech=6
            PartOfSpeech.NUMERAL->idPartOfSpeech=7
            PartOfSpeech.FUNC_PART->idPartOfSpeech=8
        }
        spinnerPartOfSpeech.setSelection(idPartOfSpeech)

        // работа с неизменяемыми характеристиками

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                Toast.makeText(this@GrammarRuleActivity, "задаем гендер", Toast.LENGTH_LONG).show()
                attrs[Attributes.GENDER] = arrayListOf(positionSpinner)
                updateMasc()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                attrs[Attributes.TYPE] = arrayListOf(positionSpinner)
                updateMasc()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerVoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                attrs[Attributes.VOICE] = arrayListOf(positionSpinner)
                updateMasc()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // работа с изменяемыми характеристиками

        spinnerFinishGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.GENDER] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishNumber.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.NUMBER] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishCase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.CASE] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.TIME] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishPerson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.PERSON] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishMood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.MOOD] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFinishDegreeOfComparison.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                mutableAttrs[Attributes.DEGREE_OF_COMPARISON] = positionSpinner
                updateMutableAttrs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
                        spinnerGender.visibility=View.VISIBLE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.VISIBLE
                        spinnerFinishCase.visibility=View.VISIBLE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        if(positionSpinner==idPartOfSpeech){
                            Toast.makeText(this@GrammarRuleActivity, "задаем в первый раз чр", Toast.LENGTH_LONG).show()
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    1->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.VERB)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.VISIBLE
                        spinnerVoice.visibility=View.VISIBLE

                        spinnerFinishGender.visibility=View.VISIBLE
                        spinnerFinishNumber.visibility=View.VISIBLE
                        spinnerFinishCase.visibility=View.GONE
                        spinnerFinishTime.visibility=View.VISIBLE
                        spinnerFinishPerson.visibility=View.VISIBLE
                        spinnerFinishMood.visibility=View.VISIBLE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    2->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.ADJECTIVE)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.VISIBLE
                        spinnerFinishNumber.visibility=View.VISIBLE
                        spinnerFinishCase.visibility=View.VISIBLE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.VISIBLE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    3->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.ADVERB)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.GONE
                        spinnerFinishCase.visibility=View.GONE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    4->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.PARTICIPLE)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.VISIBLE
                        spinnerVoice.visibility=View.VISIBLE

                        spinnerFinishGender.visibility=View.VISIBLE
                        spinnerFinishNumber.visibility=View.VISIBLE
                        spinnerFinishCase.visibility=View.VISIBLE
                        spinnerFinishTime.visibility=View.VISIBLE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    5->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.VERB_PARTICIPLE)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.VISIBLE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.GONE
                        spinnerFinishCase.visibility=View.GONE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    6->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.PRONOUN)
                        spinnerGender.visibility=View.VISIBLE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.VISIBLE
                        spinnerFinishCase.visibility=View.VISIBLE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    7->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.NUMERAL)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.GONE
                        spinnerFinishCase.visibility=View.GONE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                    else->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.FUNC_PART)
                        spinnerGender.visibility=View.GONE
                        spinnerType.visibility=View.GONE
                        spinnerVoice.visibility=View.GONE

                        spinnerFinishGender.visibility=View.GONE
                        spinnerFinishNumber.visibility=View.GONE
                        spinnerFinishCase.visibility=View.GONE
                        spinnerFinishTime.visibility=View.GONE
                        spinnerFinishPerson.visibility=View.GONE
                        spinnerFinishMood.visibility=View.GONE
                        spinnerFinishDegreeOfComparison.visibility=View.GONE

                        idPartOfSpeech=positionSpinner
                        attrs = mutableMapOf()
                        mutableAttrs= mutableMapOf()
                        updateMasc()
                        updateMutableAttrs()
                        updateSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mascDao.changePartOfSpeech(
                    languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
            }
        }

        // edit texts

        val editTextNumberFront: EditText = findViewById(R.id.editTextNumberFront)
        val editTextNumberBack: EditText = findViewById(R.id.editTextNumberBack)
        val editTextAddFront: EditText = findViewById(R.id.editTextAddFront)
        val editTextAddBack: EditText = findViewById(R.id.editTextAddBack)

        numberFront=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].transformation.delFromBeginning
        numberBack=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].transformation.delFromEnd
        addFront=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].transformation.addToBeginning
        addBack=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].transformation.addToEnd
        (editTextNumberFront as TextView).setText(numberFront.toString())
        (editTextNumberBack as TextView).setText(numberBack.toString())
        (editTextAddFront as TextView).setText(addFront)
        (editTextAddBack as TextView).setText(addBack)

        editTextNumberFront.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                numberFront = s.toString().toInt()
                updateTransformation()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        editTextNumberBack.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                numberBack = s.toString().toInt()
                updateTransformation()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        editTextAddFront.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                addFront = s.toString()
                updateTransformation()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        editTextAddBack.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                addBack = s.toString()
                updateTransformation()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }
    fun updateMasc(){
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
        var newMasc = MascEntity(
            partOfSpeech, attrs, regex
        )
        grammarRuleDao.updateMasc(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule], newMasc)
        Toast.makeText(this, newMasc.toString(), Toast.LENGTH_LONG).show()
    }
    fun updateMutableAttrs(){
        grammarRuleDao.updateMutableAttrs(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule], mutableAttrs)
    }
    fun updateTransformation(){
        var newTransformation = TransformationEntity(
            numberFront, numberBack, addFront, addBack
        )
        grammarRuleDao.updateTransformation(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule], newTransformation)
    }
    fun listenSpinners(){

        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishNumber: Spinner = findViewById(R.id.spinnerFinishNumber)
        val spinnerFinishCase: Spinner = findViewById(R.id.spinnerFinishCase)
        val spinnerFinishTime: Spinner = findViewById(R.id.spinnerFinishTime)
        val spinnerFinishPerson: Spinner = findViewById(R.id.spinnerFinishPerson)
        val spinnerFinishMood: Spinner = findViewById(R.id.spinnerFinishMood)
        val spinnerFinishDegreeOfComparison: Spinner = findViewById(R.id.spinnerFinishDegreeOfComparison)


        val genderNames = languages[id_lang]!!.grammar.varsGender.values.map { it.name }
        var genderAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderNames)
        spinnerGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()

        val typeNames = languages[id_lang]!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, typeNames)
        spinnerType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = languages[id_lang]!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, voiceNames)
        spinnerVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()

        val finishGenderAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderNames)
        spinnerFinishGender.adapter = finishGenderAdapter
        finishGenderAdapter.notifyDataSetChanged()

        val numberNames = languages[id_lang]!!.grammar.varsNumber.values.map { it.name }
        val finishNumberAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, numberNames)
        spinnerFinishNumber.adapter = finishNumberAdapter
        finishNumberAdapter.notifyDataSetChanged()

        val caseNames = languages[id_lang]!!.grammar.varsCase.values.map { it.name }
        val finishCaseAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, caseNames)
        spinnerFinishCase.adapter = finishCaseAdapter
        finishCaseAdapter.notifyDataSetChanged()

        val timeNames = languages[id_lang]!!.grammar.varsTime.values.map { it.name }
        val finishTimeAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, timeNames)
        spinnerFinishTime.adapter = finishTimeAdapter
        finishTimeAdapter.notifyDataSetChanged()

        val personNames = languages[id_lang]!!.grammar.varsPerson.values.map { it.name }
        val finishPersonAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, personNames)
        spinnerFinishPerson.adapter = finishPersonAdapter
        finishPersonAdapter.notifyDataSetChanged()

        val moodNames = languages[id_lang]!!.grammar.varsMood.values.map { it.name }
        val finishMoodAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, moodNames)
        spinnerFinishMood.adapter = finishMoodAdapter
        finishMoodAdapter.notifyDataSetChanged()

        val degreeOfComparisonNames = languages[id_lang]!!.grammar.varsDegreeOfComparison.values.map { it.name }
        val finishDegreeOfComparisonAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, degreeOfComparisonNames)
        spinnerFinishDegreeOfComparison.adapter = finishDegreeOfComparisonAdapter
        finishDegreeOfComparisonAdapter.notifyDataSetChanged()
    }
    fun updateSpinners(){
        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishNumber: Spinner = findViewById(R.id.spinnerFinishNumber)
        val spinnerFinishCase: Spinner = findViewById(R.id.spinnerFinishCase)
        val spinnerFinishTime: Spinner = findViewById(R.id.spinnerFinishTime)
        val spinnerFinishPerson: Spinner = findViewById(R.id.spinnerFinishPerson)
        val spinnerFinishMood: Spinner = findViewById(R.id.spinnerFinishMood)
        val spinnerFinishDegreeOfComparison: Spinner = findViewById(R.id.spinnerFinishDegreeOfComparison)
        Toast.makeText(this@GrammarRuleActivity, "обновляем гендер", Toast.LENGTH_LONG).show()
        when(idPartOfSpeech) {
            0 -> {
                spinnerGender.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.GENDER]?.get(0) ?: 0)

                spinnerFinishNumber.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.CASE] ?:0)
            }
            1->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.TYPE]?.get(0) ?: 0)
                spinnerVoice.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.VOICE]?.get(0) ?: 0)

                spinnerFinishGender.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishTime.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.TIME] ?:0)
                spinnerFinishPerson.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.PERSON] ?:0)
                spinnerFinishMood.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.MOOD] ?:0)
            }
            2-> {
                spinnerFinishGender.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.CASE] ?:0)
                spinnerFinishDegreeOfComparison.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.DEGREE_OF_COMPARISON] ?:0)
            }
            3->{}
            4->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.TYPE]?.get(0) ?: 0)
                spinnerVoice.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.VOICE]?.get(0) ?: 0)

                spinnerFinishGender.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.CASE] ?:0)
                spinnerFinishTime.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.TIME] ?:0)
            }
            5->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.TYPE]?.get(0) ?: 0)
            }
            6->{
                spinnerGender.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.attrs[Attributes.GENDER]?.get(0) ?: 0)

                spinnerFinishNumber.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].mutableAttrs[Attributes.CASE] ?:0)
            }
            7->{}
            else->{}
        }
    }
}