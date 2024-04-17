package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R

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