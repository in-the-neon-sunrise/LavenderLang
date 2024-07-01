package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R

class InstructionActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var id_block: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instruction_activity)
        if(getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }
    }
    override fun onResume() {
        super.onResume()
        id_lang = intent.getIntExtra("lang", -1)
        id_block = intent.getIntExtra("block", 0)
        val scroll: ScrollView = findViewById(R.id.scrollView2)
        var heads = mutableListOf<TextView>()
        heads.add(findViewById(R.id.textViewHead1))
        heads.add(findViewById(R.id.textViewHead2))
        heads.add(findViewById(R.id.textViewHead3))
        heads.add(findViewById(R.id.textViewHead4))
        heads.add(findViewById(R.id.textViewHead5))
        heads.add(findViewById(R.id.textViewHead6))
        heads.add(findViewById(R.id.textViewHead7))
        heads.add(findViewById(R.id.textViewHead8))
        heads.add(findViewById(R.id.textViewHead9))
        heads.add(findViewById(R.id.textViewHead10))
        heads.add(findViewById(R.id.textViewHead11))

        if(id_block!=0)scroll.post { scroll.smoothScrollTo(0, heads[id_block-1].top) }
    }

    override fun finish() {
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}