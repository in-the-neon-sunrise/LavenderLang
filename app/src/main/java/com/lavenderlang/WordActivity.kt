package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.PartOfSpeech

class WordActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var id_word: Int = 0

        var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf()
        var mutableAttrs: MutableMap<Attributes, Int> = mutableMapOf()
        var idPartOfSpeech: Int = 0
        var partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_activity)

        LanguageDaoImpl.getLanguagesFromDB(this)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@WordActivity, DictionaryActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WordActivity, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }
        when (val word = intent.getIntExtra("word", -1)) {
            -1 -> {
                val intent = Intent(this@WordActivity, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_word = word
            }
        }
    }
    override fun onResume() {
        super.onResume()

        var editConlangWord: EditText = findViewById(R.id.editConlangWord)
        var editRussianWord: EditText = findViewById(R.id.editRussianWord)

        editConlangWord.setText(languages[id_lang]!!.dictionary.dict[id_word].word)
        editRussianWord.setText(languages[id_lang]!!.dictionary.dict[id_word].translation)

        partOfSpeech = languages[id_lang]!!.dictionary.dict[id_word].partOfSpeech
        when (partOfSpeech){
            PartOfSpeech.NOUN-> GrammarRuleActivity.idPartOfSpeech =0
            PartOfSpeech.VERB-> GrammarRuleActivity.idPartOfSpeech =1
            PartOfSpeech.ADJECTIVE-> GrammarRuleActivity.idPartOfSpeech =2
            PartOfSpeech.ADVERB-> GrammarRuleActivity.idPartOfSpeech =3
            PartOfSpeech.PARTICIPLE-> GrammarRuleActivity.idPartOfSpeech =4
            PartOfSpeech.VERBPARTICIPLE-> GrammarRuleActivity.idPartOfSpeech =5
            PartOfSpeech.PRONOUN-> GrammarRuleActivity.idPartOfSpeech =6
            PartOfSpeech.NUMERAL-> GrammarRuleActivity.idPartOfSpeech =7
            PartOfSpeech.FUNCPART-> GrammarRuleActivity.idPartOfSpeech =8
        }
        immutableAttrs = languages[id_lang]!!.dictionary.dict[id_word].immutableAttrs
        mutableAttrs = languages[id_lang]!!.dictionary.dict[id_word].mutableAttrs

        setPartOfSpeechListener()

        setSpinners()
        updateSpinners()

        val buttonUpdate: Button = findViewById(R.id.buttonUpdate)
    }
    fun setSpinners(){
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
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner =findViewById(R.id.spinnerGender)
        val spinnerType: Spinner =findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner =findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishNumber: Spinner = findViewById(R.id.spinnerFinishNumber)
        val spinnerFinishCase: Spinner = findViewById(R.id.spinnerFinishCase)
        val spinnerFinishTime: Spinner = findViewById(R.id.spinnerFinishTime)
        val spinnerFinishPerson: Spinner = findViewById(R.id.spinnerFinishPerson)
        val spinnerFinishMood: Spinner = findViewById(R.id.spinnerFinishMood)
        val spinnerFinishDegreeOfComparison: Spinner = findViewById(R.id.spinnerFinishDegreeOfComparison)

        spinnerPartOfSpeech.setSelection(idPartOfSpeech)

        when(idPartOfSpeech) {
            0 -> {
                spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)

                spinnerFinishNumber.setSelection(mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(mutableAttrs[Attributes.CASE] ?:0)
            }
            1->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)

                spinnerFinishGender.setSelection(mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishTime.setSelection(mutableAttrs[Attributes.TIME] ?:0)
                spinnerFinishPerson.setSelection(mutableAttrs[Attributes.PERSON] ?:0)
                spinnerFinishMood.setSelection(mutableAttrs[Attributes.MOOD] ?:0)
            }
            2-> {
                spinnerFinishGender.setSelection(mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(mutableAttrs[Attributes.CASE] ?:0)
                spinnerFinishDegreeOfComparison.setSelection(mutableAttrs[Attributes.DEGREEOFCOMPARISON] ?:0)
            }
            3->{}
            4->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE]?: 0)
                spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)

                spinnerFinishGender.setSelection(mutableAttrs[Attributes.GENDER] ?:0)
                spinnerFinishNumber.setSelection(mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(mutableAttrs[Attributes.CASE] ?:0)
                spinnerFinishTime.setSelection(mutableAttrs[Attributes.TIME] ?:0)
            }
            5->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)

                spinnerFinishNumber.setSelection(mutableAttrs[Attributes.NUMBER] ?:0)
                spinnerFinishCase.setSelection(mutableAttrs[Attributes.CASE] ?:0)
            }
            7->{}
            else->{}
        }
    }
    fun setPartOfSpeechListener(){
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner =findViewById(R.id.spinnerGender)
        val spinnerType: Spinner =findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner =findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishNumber: Spinner = findViewById(R.id.spinnerFinishNumber)
        val spinnerFinishCase: Spinner = findViewById(R.id.spinnerFinishCase)
        val spinnerFinishTime: Spinner = findViewById(R.id.spinnerFinishTime)
        val spinnerFinishPerson: Spinner = findViewById(R.id.spinnerFinishPerson)
        val spinnerFinishMood: Spinner = findViewById(R.id.spinnerFinishMood)
        val spinnerFinishDegreeOfComparison: Spinner = findViewById(R.id.spinnerFinishDegreeOfComparison)

        spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.VISIBLE
                        spinnerFinishCase.visibility= View.VISIBLE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    1->{
                        GrammarRuleActivity.mascDao.changePartOfSpeech(
                            languages[GrammarRuleActivity.id_lang]!!.grammar.grammarRules.toMutableList()[GrammarRuleActivity.id_rule].masc, PartOfSpeech.VERB)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE

                        spinnerFinishGender.visibility= View.VISIBLE
                        spinnerFinishNumber.visibility= View.VISIBLE
                        spinnerFinishCase.visibility= View.GONE
                        spinnerFinishTime.visibility= View.VISIBLE
                        spinnerFinishPerson.visibility= View.VISIBLE
                        spinnerFinishMood.visibility= View.VISIBLE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    2->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.VISIBLE
                        spinnerFinishNumber.visibility= View.VISIBLE
                        spinnerFinishCase.visibility= View.VISIBLE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    3->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.GONE
                        spinnerFinishCase.visibility= View.GONE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    4->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE

                        spinnerFinishGender.visibility= View.VISIBLE
                        spinnerFinishNumber.visibility= View.VISIBLE
                        spinnerFinishCase.visibility= View.VISIBLE
                        spinnerFinishTime.visibility= View.VISIBLE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    5->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.GONE
                        spinnerFinishCase.visibility= View.GONE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    6->{
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.VISIBLE
                        spinnerFinishCase.visibility= View.VISIBLE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    7->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.GONE
                        spinnerFinishCase.visibility= View.GONE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    else->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishNumber.visibility= View.GONE
                        spinnerFinishCase.visibility= View.GONE
                        spinnerFinishTime.visibility= View.GONE
                        spinnerFinishPerson.visibility= View.GONE
                        spinnerFinishMood.visibility= View.GONE
                        spinnerFinishDegreeOfComparison.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        mutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }
}