package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.PunctuationDao
import com.lavenderlang.backend.dao.language.PunctuationDaoImpl
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException

class PunctuationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        val punctuationDao: PunctuationDao = PunctuationDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.punctuation_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@PunctuationActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
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
    override fun onResume(){
        super.onResume()

        var editTextConlangSymbol1: EditText = findViewById(R.id.editTextConlangSymbol1)
        var editTextConlangSymbol2: EditText = findViewById(R.id.editTextConlangSymbol2)
        var editTextConlangSymbol3: EditText = findViewById(R.id.editTextConlangSymbol3)
        var editTextConlangSymbol4: EditText = findViewById(R.id.editTextConlangSymbol4)

        // set symbols
        editTextConlangSymbol1.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[0])
        editTextConlangSymbol2.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[1])
        editTextConlangSymbol3.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[2])
        editTextConlangSymbol4.setText(languages[id_lang]!!.puncSymbols.values.toMutableList()[3])

        var buttonSave: Button = findViewById(R.id.buttonSavePunctuation)

        // save symbols
        buttonSave.setOnClickListener {
            try{
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 0, editTextConlangSymbol1.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 1, editTextConlangSymbol2.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 2, editTextConlangSymbol3.text.toString())
                punctuationDao.updatePunctuationSymbol(languages[id_lang]!!, 3, editTextConlangSymbol4.text.toString())
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