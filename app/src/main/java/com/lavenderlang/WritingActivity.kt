package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.LanguageActivity.Companion.languageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.service.Serializer

class WritingActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
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
                GrammarActivity.id_lang = lang
            }
        }
    }
    override fun onResume() {
        super.onResume()

        //letters
        val editTextLetters: EditText = findViewById(R.id.editLetters)
        //editTextLetters.setText(languages[id_lang]?.letters)

        //check changing
        editTextLetters.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                //!!!
                //languageDao.changeLetters(languages[id_lang]!!, (editTextLetters.text.toString()))
            }
        })

        //symbols
        val editTextSymbols: EditText = findViewById(R.id.editSymbols)
        //editTextSymbols.setText(languages[id_lang]?.puncSymbols)

        //check changing
        editTextSymbols.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                //languageDao.changePunctuationSymbols(languages[id_lang]!!, (editTextSymbols.text.toString()))
            }
        })
    }
}