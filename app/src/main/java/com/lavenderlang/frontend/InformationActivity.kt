package com.lavenderlang.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.lavenderlang.R


class InformationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.information_activity)


        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }

        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val switchTheme: SwitchCompat = findViewById(R.id.switch1)
        isDark = getSavedTheme()
        switchTheme.isChecked = isDark
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            isDark = isChecked
            if(isChecked) dark()
            else light()
            saveTheme(isChecked)
        }
        if(isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    fun light(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    fun dark(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
    fun getSavedTheme(): Boolean{
        val sp = getSharedPreferences("Theme", Context.MODE_PRIVATE)
        return sp.getBoolean("isDark", false)
    }
    fun saveTheme(isDark: Boolean){
        val sp = getSharedPreferences("Theme", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("isDark", isDark)
        editor.apply()
    }
    override fun onResume() {
        super.onResume()
        id_lang = intent.getIntExtra("lang", -1)
        val buttonInstruction: Button = findViewById(R.id.buttonGuide)
        buttonInstruction.setOnClickListener{
            val intent = Intent(this@InformationActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun finish() {
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}