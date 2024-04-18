package com.lavenderlang.frontend

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
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity

class WordFormationActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_formation_activity)
        if(isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordFormationActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this@WordFormationActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonLanguage: Button = findViewById(R.id.buttonLanguage)
        buttonLanguage.setOnClickListener {
            val intent = Intent(this@WordFormationActivity, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", LanguageActivity.id_lang)
            startActivity(intent)
        }
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
    override fun onStart() {
        super.onStart()
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
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}

private class WordFormationRuleAdapter(context: Context, listOfRules: MutableList<WordFormationRuleEntity>) :
    ArrayAdapter<WordFormationRuleEntity>(context,
        R.layout.word_formation_rule_line_activity, listOfRules) {
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