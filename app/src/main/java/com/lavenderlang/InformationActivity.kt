package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InformationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.information_activity)
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        id_lang = intent.getIntExtra("lang", -1)
    }
}