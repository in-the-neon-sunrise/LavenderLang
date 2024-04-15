package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.LanguageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl

class LoadLanguageActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_language_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val editTextPath: EditText = findViewById(R.id.editTextPath)
        val buttonOpen: Button = findViewById(R.id.buttonOpen)
        val languageDao: LanguageDao = LanguageDaoImpl()

        buttonOpen.setOnClickListener {
            val path = editTextPath.text.toString()
            languageDao.getLanguageFromFile(path, this)
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("lang", languages.keys.toMutableList().last())
            startActivity(intent)
        }
    }

}