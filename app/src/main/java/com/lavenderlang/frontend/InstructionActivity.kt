package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R

class InstructionActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var id_block: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instruction_activity)
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }
    }
    override fun onResume() {
        super.onResume()
        id_lang = intent.getIntExtra("lang", -1)
        id_block = intent.getIntExtra("block", -1)
        val list: ScrollView = findViewById(R.id.scrollView2)

    }

    override fun finish() {
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}