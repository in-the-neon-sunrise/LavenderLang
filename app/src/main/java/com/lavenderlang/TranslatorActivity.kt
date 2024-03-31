package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import com.lavenderlang.backend.dao.language.TranslatorDao
import com.lavenderlang.backend.dao.language.TranslatorDaoImpl
import com.lavenderlang.backend.entity.help.Characteristic

class TranslatorActivity : Activity() {
    companion object{
        var id_lang = 0
        var isOnConlang = true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translator_activity)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@TranslatorActivity, MainActivity::class.java)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@TranslatorActivity, InformationActivity::class.java)
            intent.putExtra("lang", LanguageActivity.id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val spinner: Spinner = findViewById(R.id.spinnerChooseLanguage)
        val edittext: EditText = findViewById(R.id.editTextText)
        val radiogroup: RadioGroup = findViewById(R.id.radioGroupTranslate)
        val radiobutton: RadioButton = findViewById(R.id.radioButtonFromConlang)

        val languageNames = languages.values.map { it.name }
        val adapterLanguages: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        spinner.adapter = adapterLanguages
        adapterLanguages.notifyDataSetChanged()

        radiobutton.isChecked = true
        radiogroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==0) isOnConlang=false
            else isOnConlang=true
            translate()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) {
                id_lang = position
                translate()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                id_lang = 0
                translate()
            }
        }

        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                translate()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }
    fun translate(){
        val edittext: EditText = findViewById(R.id.editTextText)
        val textview: TextView = findViewById(R.id.textViewTranslation)

        var clever_index_of_language: Int = languages.keys.toMutableList()[id_lang]
        var input_text: String = edittext.text.toString()

        val translatorDao = TranslatorDaoImpl()
        if (isOnConlang) {
            textview.setText(
                translatorDao.translateTextFromConlang(languages[clever_index_of_language]!!, input_text))
        } else {
            textview.setText(
                translatorDao.translateTextToConlang(languages[clever_index_of_language]!!, input_text))
        }
    }
}