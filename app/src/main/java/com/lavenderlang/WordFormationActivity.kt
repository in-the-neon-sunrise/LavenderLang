package com.lavenderlang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.GrammarDao
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity

class WordFormationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_formation_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordFormationActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WordFormationActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //button new grammar rule listener
        val buttonNewRule: Button = findViewById(R.id.buttonNewWordFormationRule)
        buttonNewRule.setOnClickListener {
            val intent = Intent(this, WordFormationRuleActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("wordFormationRule", -1)
            startActivity(intent)
        }

        //list of rules
        val listWordFormationRules : ListView = findViewById(R.id.listViewWordFormationRules)
        val adapterWordFormationRules: ArrayAdapter<WordFormationRuleEntity> = WordFormationRuleAdapter(this, languages[id_lang]!!.grammar.wordFormationRules.toMutableList())
        listWordFormationRules.adapter = adapterWordFormationRules
        adapterWordFormationRules.notifyDataSetChanged()

        //click listener
        listWordFormationRules.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this, WordFormationRuleActivity::class.java)
                intent.putExtra("lang", id_lang)
                intent.putExtra("wordFormationRule", position)

                startActivity(intent)
            }
    }
}

private class WordFormationRuleAdapter(context: Context, listOfRules: MutableList<WordFormationRuleEntity>) :
    ArrayAdapter<WordFormationRuleEntity>(context, R.layout.word_formation_rule_line_activity, listOfRules) {
        companion object{
            val wordFormationRuleDao: WordFormationRuleDao = WordFormationRuleDaoImpl()
        }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val wordFormationRule: WordFormationRuleEntity? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.word_formation_rule_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView!!.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = wordFormationRuleDao.getOrigInfo(wordFormationRule!!)
        changeableAttributes.text = wordFormationRuleDao.getResultInfo(wordFormationRule!!)

        return newView
    }
}