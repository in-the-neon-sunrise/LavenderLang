package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class WritingActivity : Activity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.writing_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WritingActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
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

        //letters
        val editTextLetters: EditText = findViewById(R.id.editLetters)
        editTextLetters.setText(Languages.languages[id_lang].stringLetters())

        //check changing
        editTextLetters.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                Languages.languages[LanguageActivity.id_lang].setLetters(editTextLetters.text.toString())
            }
        })

        //symbols
        val editTextSymbols: EditText = findViewById(R.id.editSymbols)
        editTextLetters.setText("hello")

        //check changing
        editTextSymbols.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                //Languages.languages[LanguageActivity.id_lang].setLetters(editTextLetters.text.toString())
            }
        })
    }
}