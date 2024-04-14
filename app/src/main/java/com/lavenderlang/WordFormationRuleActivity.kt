package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.GrammarDao
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity

class WordFormationRuleActivity: AppCompatActivity()  {
    companion object {
        var id_lang: Int = 0
        var id_rule: Int = 0

        val grammarDao: GrammarDao = GrammarDaoImpl()
        val wordFormationRuleDao: WordFormationRuleDao = WordFormationRuleDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_formation_rule_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val editMasc: EditText = findViewById(R.id.editMasc)

        when(val lang = intent.getIntExtra("lang", -1)){
            -1 -> {
                val intent = Intent(this, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }
        var rule = intent.getIntExtra("wordFormationRule", -1)
        if (rule == -1 && id_rule != 0){
            rule = id_lang
        }
        when(rule){
            -1 -> {
                var newRule = WordFormationRuleEntity(id_lang)
                grammarDao.addWordFormationRule(languages[id_lang]!!.grammar, newRule)
                id_rule = languages[GrammarRuleActivity.id_lang]!!.grammar.grammarRules.size-1
                editMasc.setText("это новое правило привет")
            }
            else -> {
                id_rule = rule
                editMasc.setText(languages[id_lang]!!.grammar.grammarRules.toMutableList()[GrammarRuleActivity.id_rule].masc.regex)
            }
        }
    }
}