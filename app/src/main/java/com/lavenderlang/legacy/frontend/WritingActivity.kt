package com.lavenderlang.legacy.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.WritingDao
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.ui.MyApp

class WritingActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
        val writingDao: WritingDao = WritingDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.writing_activity)
        if(getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WritingActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 4)
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

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()

        //letters
        val editTextVowels: EditText = findViewById(R.id.editTextVowels)
        editTextVowels.setText(MyApp.language!!.vowels)

        //symbols
        val editTextConsonants: EditText = findViewById(R.id.editTextConsonants)
        editTextConsonants.setText(MyApp.language?.consonants)

        val checkBoxNoun: CheckBox = findViewById(R.id.checkBoxNoun)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NOUN)) checkBoxNoun.isChecked = true
        val checkBoxVerb: CheckBox = findViewById(R.id.checkBoxVerb)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB)) checkBoxVerb.isChecked = true
        val checkBoxAdjective: CheckBox = findViewById(R.id.checkBoxAdjective)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADJECTIVE)) checkBoxAdjective.isChecked = true
        val checkBoxAdverb: CheckBox = findViewById(R.id.checkBoxAdverb)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.ADVERB)) checkBoxAdverb.isChecked = true
        val checkBoxParticiple: CheckBox = findViewById(R.id.checkBoxParticiple)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PARTICIPLE)) checkBoxParticiple.isChecked = true
        val checkBoxVerbParticiple: CheckBox = findViewById(R.id.checkBoxVerbParticiple)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.VERB_PARTICIPLE)) checkBoxVerbParticiple.isChecked = true
        val checkBoxPronoun: CheckBox = findViewById(R.id.checkBoxPronoun)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.PRONOUN)) checkBoxPronoun.isChecked = true
        val checkBoxNumeral: CheckBox = findViewById(R.id.checkBoxNumeral)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.NUMERAL)) checkBoxNumeral.isChecked = true
        val checkBoxFuncPart: CheckBox = findViewById(R.id.checkBoxFuncPart)
        if(MyApp.language!!.capitalizedPartsOfSpeech.contains(PartOfSpeech.FUNC_PART)) checkBoxFuncPart.isChecked = true

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            try{
            writingDao.changeVowels(MyApp.language!!, editTextVowels.text.toString())
            writingDao.changeConsonants(MyApp.language!!, editTextConsonants.text.toString())
            }catch (e: ForbiddenSymbolsException){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

            if(checkBoxNoun.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NOUN)

            if(checkBoxVerb.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB)

            if(checkBoxAdjective.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADJECTIVE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADJECTIVE)

            if(checkBoxAdverb.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADVERB)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.ADVERB)

            if(checkBoxParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PARTICIPLE)

            if(checkBoxVerbParticiple.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB_PARTICIPLE)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.VERB_PARTICIPLE)

            if(checkBoxPronoun.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PRONOUN)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.PRONOUN)

            if(checkBoxNumeral.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NUMERAL)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.NUMERAL)

            if(checkBoxFuncPart.isChecked) writingDao.addCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.FUNC_PART)
            else writingDao.deleteCapitalizedPartOfSpeech(MyApp.language!!, PartOfSpeech.FUNC_PART)
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}