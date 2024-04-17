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
import com.lavenderlang.R
import com.lavenderlang.backend.entity.word.IWordEntity

class DictionaryActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dictionary_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@DictionaryActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //button new lang listener
        val buttonNewWord: Button = findViewById(R.id.buttonNewWord)
        buttonNewWord.setOnClickListener {
            val intent = Intent(this@DictionaryActivity, WordActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("word", -1)
            startActivity(intent)
        }
        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonLanguage: Button = findViewById(R.id.buttonLanguage)
        buttonLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@DictionaryActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }
    }
    override fun onResume() {
        super.onResume()

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
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}
class WordAdapter(context: Context, listOfWords: MutableList<IWordEntity>) :
    ArrayAdapter<IWordEntity>(context, R.layout.word_line_activity, listOfWords) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newView = convertView
        val word: IWordEntity? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.word_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewDescription)
        val changeableAttributes: TextView = newView.findViewById(R.id.textViewConlangWord)
        unchangeableAttributes.text = word?.word
        changeableAttributes.text = word?.translation

        return newView
    }
}