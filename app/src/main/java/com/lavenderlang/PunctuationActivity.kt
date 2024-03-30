package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class PunctuationActivity : Activity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.punctuation_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@PunctuationActivity, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@PunctuationActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@PunctuationActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                PunctuationActivity.id_lang = lang
            }
        }
    }
}