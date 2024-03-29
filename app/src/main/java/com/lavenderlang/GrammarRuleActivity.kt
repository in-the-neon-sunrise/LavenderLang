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
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDao
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity

class GrammarRuleActivity: Activity() {
    companion object{
        var id_lang: Int = 0
        var id_rule: Int = 0

        val grammarDao = GrammarDaoImpl()
        val grammarRuleDao = GrammarRuleDaoImpl()
        val mascDao = MascDaoImpl()
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
                var newRule = GrammarRuleEntity(id_lang)
                grammarDao.addGrammarRule(languages[id_lang]!!.grammar, newRule)
                id_rule = languages[id_lang]!!.grammar.grammarRules.size-1
                editMasc.setText(".*")
            }
            else -> {
                id_rule = rule
                editMasc.setText(languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.regex)
            }
        }
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Предлог/частица/..."))

        spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        var partOfSpeech=languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc.partsOfSpeech
        var idPartOfSpeech:Int
        when (partOfSpeech){
            PartOfSpeech.NOUN->idPartOfSpeech=0
            PartOfSpeech.VERB->idPartOfSpeech=1
            PartOfSpeech.ADJECTIVE->idPartOfSpeech=2
            PartOfSpeech.ADVERB->idPartOfSpeech=3
            PartOfSpeech.PARTICIPLE->idPartOfSpeech=4
            PartOfSpeech.VERBPARTICIPLE->idPartOfSpeech=5
            PartOfSpeech.PRONOUN->idPartOfSpeech=6
            PartOfSpeech.NUMERAL->idPartOfSpeech=7
            PartOfSpeech.FUNCPART->idPartOfSpeech=8
        }
        spinnerPartOfSpeech.setSelection(idPartOfSpeech)

        spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                idPartOfSpeech=positionSpinner
                when(idPartOfSpeech){
                    0->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
                        spinnerGender.visibility=View.VISIBLE
                        spinnerNumber.visibility=View.GONE
                    }
                    else->{
                        mascDao.changePartOfSpeech(
                            languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.FUNCPART)
                        spinnerGender.visibility=View.GONE
                        spinnerNumber.visibility=View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mascDao.changePartOfSpeech(
                    languages[id_lang]!!.grammar.grammarRules.toMutableList()[id_rule].masc, PartOfSpeech.NOUN)
            }
        }

    }
}