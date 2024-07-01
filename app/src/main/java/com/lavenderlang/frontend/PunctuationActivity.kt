package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.PunctuationDao
import com.lavenderlang.backend.dao.language.PunctuationDaoImpl
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException

class PunctuationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        val punctuationDao: PunctuationDao = PunctuationDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.punctuation_activity)
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
            val intent = Intent(this@PunctuationActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 5)
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
                val intent = Intent(this@PunctuationActivity, LanguageActivity::class.java)
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
    override fun onResume(){
        super.onResume()

        var editTextConlangSymbol1: EditText = findViewById(R.id.editTextConlangSymbol1)
        var editTextConlangSymbol2: EditText = findViewById(R.id.editTextConlangSymbol2)
        var editTextConlangSymbol3: EditText = findViewById(R.id.editTextConlangSymbol3)
        var editTextConlangSymbol4: EditText = findViewById(R.id.editTextConlangSymbol4)
        var editTextConlangSymbol5: EditText = findViewById(R.id.editTextConlangSymbol5)
        var editTextConlangSymbol6: EditText = findViewById(R.id.editTextConlangSymbol6)
        var editTextConlangSymbol7: EditText = findViewById(R.id.editTextConlangSymbol7)
        var editTextConlangSymbol8: EditText = findViewById(R.id.editTextConlangSymbol8)
        var editTextConlangSymbol9: EditText = findViewById(R.id.editTextConlangSymbol9)
        var editTextConlangSymbol10: EditText = findViewById(R.id.editTextConlangSymbol10)
        var editTextConlangSymbol11: EditText = findViewById(R.id.editTextConlangSymbol11)
        var editTextConlangSymbol12: EditText = findViewById(R.id.editTextConlangSymbol12)
        var editTextConlangSymbol13: EditText = findViewById(R.id.editTextConlangSymbol13)
        var editTextConlangSymbol14: EditText = findViewById(R.id.editTextConlangSymbol14)
        var editTextConlangSymbol15: EditText = findViewById(R.id.editTextConlangSymbol15)
        var editTextConlangSymbol16: EditText = findViewById(R.id.editTextConlangSymbol16)
        var editTextConlangSymbol17: EditText = findViewById(R.id.editTextConlangSymbol17)
        var editTextConlangSymbol18: EditText = findViewById(R.id.editTextConlangSymbol18)
        var editTextConlangSymbol19: EditText = findViewById(R.id.editTextConlangSymbol19)

        // set symbols
        editTextConlangSymbol1.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[0])
        editTextConlangSymbol2.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[1])
        editTextConlangSymbol3.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[2])
        editTextConlangSymbol4.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[3])
        editTextConlangSymbol5.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[4])
        editTextConlangSymbol6.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[5])
        editTextConlangSymbol7.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[6])
        editTextConlangSymbol8.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[7])
        editTextConlangSymbol9.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[8])
        editTextConlangSymbol10.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[9])
        editTextConlangSymbol11.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[10])
        editTextConlangSymbol12.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[11])
        editTextConlangSymbol13.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[12])
        editTextConlangSymbol14.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[13])
        editTextConlangSymbol15.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[14])
        editTextConlangSymbol16.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[15])
        editTextConlangSymbol17.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[16])
        editTextConlangSymbol18.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[17])
        editTextConlangSymbol19.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[18])

        var buttonSave: Button = findViewById(R.id.buttonSavePunctuation)

        // save symbols
        buttonSave.setOnClickListener {
            try{
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 0, editTextConlangSymbol1.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 1, editTextConlangSymbol2.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 2, editTextConlangSymbol3.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 3, editTextConlangSymbol4.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 4, editTextConlangSymbol5.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 5, editTextConlangSymbol6.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 6, editTextConlangSymbol7.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 7, editTextConlangSymbol8.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 8, editTextConlangSymbol9.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 9, editTextConlangSymbol10.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 10, editTextConlangSymbol11.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 11, editTextConlangSymbol12.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 12, editTextConlangSymbol13.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 13, editTextConlangSymbol14.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 14, editTextConlangSymbol15.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 15, editTextConlangSymbol16.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 16, editTextConlangSymbol17.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 17, editTextConlangSymbol18.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 18, editTextConlangSymbol19.text.toString())
            }catch (e:ForbiddenSymbolsException){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}