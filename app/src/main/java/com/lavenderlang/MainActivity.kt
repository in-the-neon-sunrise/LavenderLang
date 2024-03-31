package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.data.LanguageItem
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.word.NounEntity
import com.lavenderlang.backend.entity.word.VerbEntity
import com.lavenderlang.backend.service.Serializer

var serializer : Serializer = Serializer()
var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        LanguageDaoImpl.getLanguagesFromDB(this)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        languages[0] = LanguageEntity(0, "lol", "Ne rусский язык")

        val dict = DictionaryDaoImpl()
        val word1 = NounEntity(
            0,
            "aaa",
            "кошечка",
            immutableAttrs = mutableMapOf(Attributes.GENDER to 1),
            partOfSpeech = PartOfSpeech.NOUN
        )
        val word2 = VerbEntity(
            0,
            "bbb",
            "заплакать",
            partOfSpeech = PartOfSpeech.VERB
        )
        dict.addWord(languages[0]!!.dictionary, word1)
        dict.addWord(languages[0]!!.dictionary, word2)







        //button new lang listener
        val buttonNewLang: Button = findViewById(R.id.buttonNewLang)
        buttonNewLang.setOnClickListener {
            val intent = Intent(this@MainActivity, LanguageActivity::class.java)
            intent.putExtra("lang", -1)
            startActivity(intent)
        }

        //go to information
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@MainActivity, InformationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        LanguageDaoImpl.getLanguagesFromDB(this)

        //val languageRepository = LanguageRepository()
        //for (e in languages.keys) languageRepository.deleteLanguage(this, e)


        val listLanguages : ListView = findViewById(R.id.listLanguages)
        val adapter: ArrayAdapter<LanguageEntity> = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages.values.toList())
        listLanguages.adapter = adapter
        adapter.notifyDataSetChanged()
        listLanguages.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this@MainActivity, LanguageActivity::class.java)
                intent.putExtra("lang", languages[position]?.languageId)
                startActivity(intent)
            }
    }
    override fun onPause() {
        super.onPause()
        val languageRepository = LanguageRepository()
        for (lang in languages.keys) {
            Thread {
                languageRepository.insertLanguage(this, lang, serializer.serializeLanguage(languages[lang]!!))
            }.start()
        }
    }
}