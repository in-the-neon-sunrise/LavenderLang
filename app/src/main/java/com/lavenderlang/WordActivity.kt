package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.LanguageDaoImpl

class WordActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var id_word: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_activity)

        LanguageDaoImpl.getLanguagesFromDB(this)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@WordActivity, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WordActivity, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }
        when (val word = intent.getIntExtra("word", -1)) {
            -1 -> {
                val intent = Intent(this@WordActivity, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_word = word
            }
        }
    }
    override fun onResume() {
        super.onResume()

        var editConlangWord: EditText = findViewById(R.id.editConlangWord)
        var editRussianWord: EditText = findViewById(R.id.editRussianWord)

        editConlangWord.setText(languages[id_lang]!!.dictionary.dict[id_word].word)
        editRussianWord.setText(languages[id_lang]!!.dictionary.dict[id_word].translation)


    }
}