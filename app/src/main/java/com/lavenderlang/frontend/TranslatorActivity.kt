package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.TranslatorDaoImpl

class TranslatorActivity : AppCompatActivity() {
    companion object{
        var id_lang = 0
        var translationOnConlang = false
        var flagIsSpinnerSelected = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translator_activity)
        if(isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@TranslatorActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 10)
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
    }

    override fun onStart() {
        super.onStart()

        flagIsSpinnerSelected = false

        val spinner: Spinner = findViewById(R.id.spinnerChooseLanguage)
        val edittext: EditText = findViewById(R.id.editTextText)
        val radiogroup: RadioGroup = findViewById(R.id.radioGroupTranslate)
        val radiobutton: RadioButton = findViewById(R.id.radioButtonFromConlang)

        id_lang = intent.getIntExtra("lang", -1)
        val languageNames = languages.values.map { it.name }
        val adapterLanguages: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        spinner.adapter = adapterLanguages
        adapterLanguages.notifyDataSetChanged()
        flagIsSpinnerSelected =true;
        var stupid_id = languages.keys.toList().indexOfFirst { it == id_lang }
        if(id_lang !=-1)spinner.setSelection(stupid_id)
        radiobutton.isChecked = true//перевод с конланга
        radiogroup.setOnCheckedChangeListener { group, checkedId ->
            translationOnConlang = checkedId != radiobutton.id
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) {
                if(flagIsSpinnerSelected) id_lang = languages.keys.toList()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                id_lang = 0
            }
        }

        /*edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                translate()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })*/
        val buttonTranslate: Button = findViewById(R.id.buttonTranslate)
        buttonTranslate.setOnClickListener {
            translate()
        }
    }
    private fun translate(){

        val edittext: EditText = findViewById(R.id.editTextText)
        val textview: TextView = findViewById(R.id.textViewTranslation)

        val clever_index_of_language = id_lang//: Int = languages.keys.toMutableList()[id_lang]
        val input_text: String = edittext.text.toString()

        val translatorDao = TranslatorDaoImpl()
        if (!translationOnConlang) {
            textview.setText(
                translatorDao.translateTextFromConlang(languages[clever_index_of_language]!!, input_text))
        } else {
            textview.setText(
                translatorDao.translateTextToConlang(languages[clever_index_of_language]!!, input_text))
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}