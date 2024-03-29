package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class GrammarRuleActivity: Activity() {
    companion object{
        var id_lang: Int = 0
        var id_rule: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_rule_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@GrammarRuleActivity, InformationActivity::class.java)
            intent.putExtra("lang", GrammarRuleActivity.id_lang)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val editMasc: EditText = findViewById(R.id.editMasc)
        when(val lang = intent.getIntExtra("lang", -1)){
            -1 -> {
                val intent = Intent(this@GrammarRuleActivity, GrammarActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }
        when(val rule = intent.getIntExtra("grammarRule", -1)){
            -1 -> {
                id_rule = Languages.rules.size
                Languages.rules.add("-")
                editMasc.setText(Languages.rules[id_rule])
            }
            else -> {
                id_rule = rule
                editMasc.setText(Languages.rules[id_rule])
            }
        }
        /*val spinnerRus: Spinner = newView.findViewById(R.id.spinnerRusAttribute)
        val spinnerAdapter: ArrayAdapter<String>
        when(idAttribute){
            0->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusGender)
            }
        }*/

    }
}