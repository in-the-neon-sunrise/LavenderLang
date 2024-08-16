package com.lavenderlang.legacy.frontend

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
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.DictionaryDao
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.backend.service.Serializer

class DictionaryActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = 0
        val dictionaryDao: DictionaryDao = DictionaryDaoImpl()
        var sort: Int = 0
        var filter: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dictionary_activity)
        if (getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener {
            val intent = Intent(this@DictionaryActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 7)
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

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        var spinnerSort: Spinner = findViewById(R.id.spinnerSort)
        var spinnerFilter: Spinner = findViewById(R.id.spinnerFilter)

        var flag = mutableListOf("по конлангу", "по переводу")
        var adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, flag)
        spinnerSort.adapter = adapter
        adapter.notifyDataSetChanged()

        flag = mutableListOf(
            "всё",
            "существительные",
            "глаголы",
            "прилагательные",
            "наречия",
            "причастия",
            "деепричастия",
            "местоимения",
            "числительные",
            "служебные части речи"
        )
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, flag)
        spinnerFilter.adapter = adapter
        adapter.notifyDataSetChanged()

        allWords()

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                sort = position
                allWords()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                filter = position
                allWords()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    override fun finish() {
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }

    fun allWords() {
        val dictionary = Serializer.getInstance().deserializeDictionary(
            LanguageRepository().getLanguage(this, id_lang).dictionary)
        var list = dictionary.dict.toList()
        if (sort == 0) {
            when (filter) {
                0 -> list = dictionaryDao.sortDictByWord(dictionary)
                1 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.NOUN)
                2 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.VERB)
                3 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.ADJECTIVE)
                4 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.ADVERB)
                5 -> list =
                    dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.PARTICIPLE)

                6 -> list =
                    dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.VERB_PARTICIPLE)

                7 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.PRONOUN)
                8 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.NUMERAL)
                9 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.FUNC_PART)
            }
        } else {
            when (filter) {
                0 -> list = dictionaryDao.sortDictByTranslation(dictionary)
                1 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.NOUN)

                2 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.VERB)

                3 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.ADJECTIVE)

                4 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.ADVERB)

                5 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.PARTICIPLE)

                6 -> list = dictionaryDao.sortDictByTranslationFiltered(
                    dictionary,
                    PartOfSpeech.VERB_PARTICIPLE
                )

                7 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.PRONOUN)

                8 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.NUMERAL)

                9 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.FUNC_PART)
            }
        }

        val listWords: ListView = findViewById(R.id.listWords)
        val adapter: ArrayAdapter<IWordEntity> = WordAdapter(this, list.toMutableList())
        listWords.adapter = adapter
        adapter.notifyDataSetChanged()
        listWords.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(this@DictionaryActivity, WordActivity::class.java)
                val id: Int = dictionary.dict.indexOfFirst { it.word == list[position].word }
                intent.putExtra("lang", id_lang)
                intent.putExtra("word", id)
                startActivity(intent)
            }
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