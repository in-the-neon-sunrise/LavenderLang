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
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.word.IWordEntity

class DictionaryActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dictionary_activity)

        LanguageDaoImpl.getLanguagesFromDB(this)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@DictionaryActivity, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@DictionaryActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()

        LanguageDaoImpl.getLanguagesFromDB(this)

        //how it was started?ааа кошечка
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@DictionaryActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }

        val listWords : ListView = findViewById(R.id.listWords)
        val adapter: ArrayAdapter<IWordEntity> = WordAdapter(this, languages[id_lang]!!.dictionary.dict)
        listWords.adapter = adapter
        adapter.notifyDataSetChanged()
        listWords.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this@DictionaryActivity, WordActivity::class.java)
                intent.putExtra("lang", id_lang)
                intent.putExtra("word", position)
                startActivity(intent)
            }
    }
}
private class WordAdapter(context: Context, listOfWords: MutableList<IWordEntity>) :
    ArrayAdapter<IWordEntity>(context, R.layout.word_line_activity, listOfWords) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newView = convertView
        val word: IWordEntity? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.grammar_rule_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView!!.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = word?.word
        changeableAttributes.text = word?.translation

        return newView
    }
}