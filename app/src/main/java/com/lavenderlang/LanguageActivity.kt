package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.lavenderlang.backend.dao.language.LanguageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.language.TranslatorDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.service.Serializer


class LanguageActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        val languageDao: LanguageDaoImpl = LanguageDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@LanguageActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //parts of language
        val buttonDictionary: Button = findViewById(R.id.buttonDictionary)
        buttonDictionary.setOnClickListener {
            val intent = Intent(this@LanguageActivity, DictionaryActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonGrammar: Button = findViewById(R.id.buttonGrammar)
        buttonGrammar.setOnClickListener {
            val intent = Intent(this@LanguageActivity, GrammarActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonPunctuation : Button = findViewById(R.id.buttonPunctuation)
        buttonPunctuation.setOnClickListener {
            val intent = Intent(this@LanguageActivity, PunctuationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonWriting : Button = findViewById(R.id.buttonWriting)
        buttonWriting.setOnClickListener {
            val intent = Intent(this@LanguageActivity, WritingActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonWordFormation : Button = findViewById(R.id.buttonWordFormation)
        buttonWordFormation.setOnClickListener {
            val intent = Intent(this@LanguageActivity, WordFormationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //bottom navigation menu
        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this@LanguageActivity, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        //how it was started?
        val editLanguageName: EditText = findViewById(R.id.editLanguageName)
        val editDescription: EditText = findViewById(R.id.editDescription)

        var lang = intent.getIntExtra("lang", -1)
        if (lang == -1 && id_lang != 0){
            lang = id_lang
        }
        when(lang){
            -1 -> {
                id_lang = nextLanguageId
                languageDao.createLanguage(id_lang.toString(), "")
                editLanguageName.setText(languages[id_lang]?.name)
            }
            else -> {
                id_lang = lang
                editLanguageName.setText(languages[id_lang]?.name)
            }
        }
        if(languages[id_lang]?.description != "") editDescription.setText(languages[id_lang]?.description)
    }
    override fun onResume() {
        super.onResume()
        val editLanguageName: EditText = findViewById(R.id.editLanguageName)
        val editDescription: EditText = findViewById(R.id.editDescription)

        //check changing
        editLanguageName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeName(languages[id_lang]!!, editLanguageName.text.toString())
            }
        })
        editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeDescription(languages[id_lang]!!, editDescription.text.toString())
            }
        })

        val buttonFile: Button = findViewById(R.id.buttonFile)
        buttonFile.setOnClickListener {
            LanguageDaoImpl().downloadLanguageJSON(languages[id_lang]!!,
                MainActivity.getInstance().storageHelper,
                MainActivity.getInstance().createJSONLauncher)
        }
        val buttonPDF: Button = findViewById(R.id.buttonPDF)
        buttonPDF.setOnClickListener {
            LanguageDaoImpl().downloadLanguagePDF(languages[id_lang]!!,
                MainActivity.getInstance().storageHelper,
                MainActivity.getInstance().createPDFLauncher)
        }

    }
}