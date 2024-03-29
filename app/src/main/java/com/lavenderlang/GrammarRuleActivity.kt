package com.lavenderlang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerNumber: Spinner=findViewById(R.id.spinnerNumber)







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
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)
        val spinnerAdapter: ArrayAdapter<String>
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf(
            "NOUN",
            "VERB",
            "ADJECTIVE",
            "ADVERB",
            "PARTICIPLE",
            "VERBPARTICIPLE",
            "PRONOUN",
            "NUMERAL",
            "FUNCPART"))

        spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        if(true/*если нет части речи (новое правило)*/) spinnerPartOfSpeech.setSelection(0)
        else spinnerPartOfSpeech.setSelection(0/*поставить id части речи*/)

        //переменная для id части речи
        var idPart=0

        spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                //поменять id части речиLanguages.attributesGender[positionAttribute].rusId = positionSpinner;
                when(idPart){
                    0->{
                        spinnerGender.visibility=View.VISIBLE
                        spinnerNumber.visibility=View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //что-нибудь поставить
            }
        }

    }
}