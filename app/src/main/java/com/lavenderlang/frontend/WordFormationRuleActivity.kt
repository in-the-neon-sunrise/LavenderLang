package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

class WordFormationRuleActivity: AppCompatActivity()  {
    companion object {
        var id_lang: Int = 0
        var id_rule: Int = 0

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_formation_rule_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonLanguage: Button = findViewById(R.id.buttonLanguage)
        buttonLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val editMasc: EditText = findViewById(R.id.editMasc)

        when(val lang = intent.getIntExtra("lang", -1)){
            -1 -> {
                val intent = Intent(this, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }
        var rule = intent.getIntExtra("wordFormationRule", -1)
        when(rule){
            -1 -> {
                var newRule = WordFormationRuleEntity(id_lang)
                grammarDao.addWordFormationRule(languages[id_lang]!!.grammar, newRule)
                Toast.makeText(this, languages[id_lang]!!.grammar.wordFormationRules.size.toString(), Toast.LENGTH_SHORT).show()
                id_rule = languages[id_lang]!!.grammar.wordFormationRules.size-1
                editMasc.setText(newRule.masc.regex)
            }
            else -> {
                id_rule = rule
                editMasc.setText(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.regex)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        startFlagIsFirst =true
        finishFlagIsFirst =true

        var partOfSpeech= languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.partOfSpeech
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

        attrs = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs
        regex = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.regex

        partOfSpeech= languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].partOfSpeech
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
        finishAttrs = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs

        numberFront = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].transformation.delFromBeginning
        numberBack = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].transformation.delFromEnd
        addFront = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].transformation.addToBeginning
        addBack = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].transformation.addToEnd

        description = languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].description

        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner = findViewById(R.id.spinnerGender)
        val spinnerType: Spinner = findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner = findViewById(R.id.spinnerVoice)

        val editMasc: EditText = findViewById(R.id.editMasc)

        val spinnerFinishPartOfSpeech: Spinner = findViewById(R.id.spinnerFinishPartOfSpeech)

        val spinnerFinishGender: Spinner = findViewById(R.id.spinnerFinishGender)
        val spinnerFinishType: Spinner = findViewById(R.id.spinnerFinishType)
        val spinnerFinishVoice: Spinner = findViewById(R.id.spinnerFinishVoice)

        val editTextNumberFront: EditText = findViewById(R.id.editTextNumberFront)
        val editTextNumberBack: EditText = findViewById(R.id.editTextNumberBack)
        val editTextAddFront: EditText = findViewById(R.id.editTextAddFront)
        val editTextAddBack: EditText = findViewById(R.id.editTextAddBack)

        val editTextDescriptionRule: EditText = findViewById(R.id.editTextDescriptionRule)

        listenSpinners()
        updateStartSpinners()
        updateFinishSpinners()

        editTextDescriptionRule.setText(description)

        (editTextNumberFront as TextView).text = numberFront.toString()
        (editTextNumberBack as TextView).text = numberBack.toString()
        (editTextAddFront as TextView).text = addFront
        (editTextAddBack as TextView).text = addBack

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Служебное слово"))

        spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerFinishPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        spinnerPartOfSpeech.setSelection(idPartOfSpeech)
        spinnerFinishPartOfSpeech.setSelection(finishIdPartOfSpeech)

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.VERB)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.ADJECTIVE)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.ADVERB)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.PARTICIPLE)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.VERB_PARTICIPLE)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.GONE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.PRONOUN)
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.NUMERAL)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE


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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.FUNC_PART)
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE


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
                    languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
            }
        }
        spinnerFinishPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        wordFormationRuleDao.updatePartOfSpeech(
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.NOUN)
                        spinnerFinishGender.visibility= View.VISIBLE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.VERB)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.VISIBLE
                        spinnerFinishVoice.visibility= View.VISIBLE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.ADJECTIVE)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.ADVERB)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.PARTICIPLE)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.VISIBLE
                        spinnerFinishVoice.visibility= View.VISIBLE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.VERB_PARTICIPLE)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.VISIBLE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.PRONOUN)
                        spinnerFinishGender.visibility= View.VISIBLE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.NUMERAL)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                            languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule], PartOfSpeech.FUNC_PART)
                        spinnerFinishGender.visibility= View.GONE
                        spinnerFinishType.visibility= View.GONE
                        spinnerFinishVoice.visibility= View.GONE

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
                    languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
            }
        }

        val buttonSave: Button=findViewById(R.id.buttonSaveWordFormationRule)
        buttonSave.setOnClickListener{
            if(spinnerGender.isVisible) attrs[Attributes.GENDER] = spinnerGender.selectedItemPosition
            if(spinnerType.isVisible) attrs[Attributes.TYPE] = spinnerType.selectedItemPosition
            if(spinnerVoice.isVisible) attrs[Attributes.VOICE] = spinnerVoice.selectedItemPosition

            regex = editMasc.text.toString()
            description = editTextDescriptionRule.text.toString()

            if(spinnerFinishGender.isVisible) finishAttrs[Attributes.GENDER] = spinnerFinishGender.selectedItemPosition
            if(spinnerFinishType.isVisible) finishAttrs[Attributes.TYPE] = spinnerFinishType.selectedItemPosition
            if(spinnerFinishVoice.isVisible) finishAttrs[Attributes.VOICE] = spinnerFinishVoice.selectedItemPosition

            numberFront = if (editTextNumberFront.text.toString().isNotEmpty())
                editTextNumberFront.text.toString().toInt()
            else 0
            numberBack = if (editTextNumberBack.text.toString().isNotEmpty())
                editTextNumberBack.text.toString().toInt()
            else 0
            addFront = editTextAddFront.text.toString()
            addBack = editTextAddBack.text.toString()

            updateRule()
        }
        val buttonDelete: Button = findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener{
            grammarDao.deleteWordFormationRule(languages[id_lang]!!.grammar, languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule])
            finish()
        }
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
                languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule],
                newMasc,
                newTransformation,
                description,
                finishAttrs,
                partOfSpeech
            )
        }
        catch(e: IncorrectRegexException){
            Toast.makeText(this@WordFormationRuleActivity, e.message, Toast.LENGTH_LONG).show()
        }
        catch (e: ForbiddenSymbolsException){
            Toast.makeText(this@WordFormationRuleActivity, e.message, Toast.LENGTH_LONG).show()
        }
        catch (e:Exception){
            Toast.makeText(this@WordFormationRuleActivity, "какая-то беда", Toast.LENGTH_LONG).show()
        }
    }
    fun listenSpinners(){
        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        val spinnerFinishGender: Spinner=findViewById(R.id.spinnerFinishGender)
        val spinnerFinishType: Spinner=findViewById(R.id.spinnerFinishType)
        val spinnerFinishVoice: Spinner=findViewById(R.id.spinnerFinishVoice)

        val genderNames = languages[id_lang]!!.grammar.varsGender.values.map { it.name }
        val genderAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderNames)
        spinnerGender.adapter = genderAdapter
        spinnerFinishGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()


        val typeNames = languages[id_lang]!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, typeNames)
        spinnerType.adapter = typeAdapter
        spinnerFinishType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = languages[id_lang]!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, voiceNames)
        spinnerVoice.adapter = voiceAdapter
        spinnerFinishVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()
    }
    fun updateStartSpinners(){
        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        when(idPartOfSpeech) {
            0 -> {
                spinnerGender.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerVoice.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2->{}
            3->{}
            4->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerVoice.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                spinnerType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                spinnerGender.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].masc.immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
    fun updateFinishSpinners(){
        val spinnerFinishGender: Spinner=findViewById(R.id.spinnerFinishGender)
        val spinnerFinishType: Spinner=findViewById(R.id.spinnerFinishType)
        val spinnerFinishVoice: Spinner=findViewById(R.id.spinnerFinishVoice)

        when(idPartOfSpeech) {
            0 -> {
                spinnerFinishGender.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                spinnerFinishType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerFinishVoice.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2->{}
            3->{}
            4->{
                spinnerFinishType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerFinishVoice.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                spinnerFinishType.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                spinnerFinishGender.setSelection(languages[id_lang]!!.grammar.wordFormationRules.toMutableList()[id_rule].immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        data.putExtra("wordFormationRule", id_rule)
        setResult(RESULT_OK, data)
        super.finish()
    }
}