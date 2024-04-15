package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.LanguageActivity.Companion.languageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.language.WritingDao
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.service.Serializer

class WritingActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
        val writingDao: WritingDao = WritingDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.writing_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WritingActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //how it was started?

        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WritingActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }
    }
    override fun onResume() {
        super.onResume()

        //letters
        val editTextVowels: EditText = findViewById(R.id.editTextVowels)
        editTextVowels.setText(languages[id_lang]!!.vowels)

        //symbols
        val editTextConsonants: EditText = findViewById(R.id.editTextConsonants)
        editTextConsonants.setText(languages[id_lang]?.consonants)

        val checkBoxNoun: CheckBox = findViewById(R.id.checkBoxNoun)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NOUN)) checkBoxNoun.isChecked = true
        val checkBoxVerb: CheckBox = findViewById(R.id.checkBoxVerb)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB)) checkBoxVerb.isChecked = true
        val checkBoxAdjective: CheckBox = findViewById(R.id.checkBoxAdjective)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADJECTIVE)) checkBoxAdjective.isChecked = true
        val checkBoxAdverb: CheckBox = findViewById(R.id.checkBoxAdverb)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADVERB)) checkBoxAdverb.isChecked = true
        val checkBoxParticiple: CheckBox = findViewById(R.id.checkBoxParticiple)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PARTICIPLE)) checkBoxParticiple.isChecked = true
        val checkBoxVerbParticiple: CheckBox = findViewById(R.id.checkBoxVerbParticiple)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB_PARTICIPLE)) checkBoxVerbParticiple.isChecked = true
        val checkBoxPronoun: CheckBox = findViewById(R.id.checkBoxPronoun)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PRONOUN)) checkBoxPronoun.isChecked = true
        val checkBoxNumeral: CheckBox = findViewById(R.id.checkBoxNumeral)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NUMERAL)) checkBoxNumeral.isChecked = true
        val checkBoxFuncPart: CheckBox = findViewById(R.id.checkBoxFuncPart)
        if(languages[id_lang]!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.FUNC_PART)) checkBoxFuncPart.isChecked = true

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            writingDao.changeVowels(languages[id_lang]!!, editTextVowels.text.toString())
            writingDao.changeConsonants(languages[id_lang]!!, editTextConsonants.text.toString())

            if(checkBoxNoun.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.NOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.NOUN)

            if(checkBoxVerb.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.VERB)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.VERB)

            if(checkBoxAdjective.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.ADJECTIVE)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.ADJECTIVE)

            if(checkBoxAdverb.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.ADVERB)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.ADVERB)

            if(checkBoxParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.PARTICIPLE)

            if(checkBoxVerbParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.VERB_PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.VERB_PARTICIPLE)

            if(checkBoxPronoun.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.PRONOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.PRONOUN)

            if(checkBoxNumeral.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.NUMERAL)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.NUMERAL)

            if(checkBoxFuncPart.isChecked) writingDao.addCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.FUNC_PART)
            else writingDao.deleteCapitalizedPartOfSpeech(languages[id_lang]!!, PartOfSpeech.FUNC_PART)
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", GrammarRuleActivity.id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}