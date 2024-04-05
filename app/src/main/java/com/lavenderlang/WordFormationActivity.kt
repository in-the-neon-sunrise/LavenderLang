package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.LanguageDaoImpl

class WordFormationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_formation_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordFormationActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WordFormationActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }
    }

}