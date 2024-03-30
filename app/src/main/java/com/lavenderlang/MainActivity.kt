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
import com.lavenderlang.backend.data.LanguageItem
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.Serializer

var serializer : Serializer = Serializer()
var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        var dir = getExternalFilesDirs(null)[0].absolutePath + "\\data"
        serializer = Serializer(getExternalFilesDirs(null)[0].absolutePath)
        //languages = mutableMapOf(0 to LanguageEntity(0, "a", "aa"))
        //nextLanguageId = 1

        val languageRepository = LanguageRepository()
        languageRepository.languages.observe(this
        ) { languageItemList ->
            run {
                Toast.makeText(this, "starting", Toast.LENGTH_LONG).show()
                for (e in languageItemList) {
                    languages[e.id] = serializer.deserialize(e.lang)
                    nextLanguageId = e.id + 1
                    //Toast.makeText(this, languages[e.id]!!.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        languageRepository.loadAllLanguagesFromDB(this, this)


        Toast.makeText(this, languages.size.toString(), Toast.LENGTH_LONG).show()
        //for (lan in languages.keys) Toast.makeText(this, languages[lan]!!.toString(), Toast.LENGTH_LONG).show()



        /*if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }*/
        //Toast.makeText(this, serializer.f(), Toast.LENGTH_LONG).show()

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

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


        /*var languageRepository = LanguageRepository()
        languageRepository.languages.observe(this
        ) { languageItemList ->
            {
                var languageItem = languageItemList[0]
                // add it to languages
            }
        }
        languageRepository.loadLanguageFromDB(this, this, 0)*/
        val languageRepository = LanguageRepository()
        languageRepository.languages.observe(this
        ) { languageItemList ->
            run {
                //Toast.makeText(this, "starting", Toast.LENGTH_LONG).show()
                for (e in languageItemList) {
                    languages[e.id] = serializer.deserialize(e.lang)
                    nextLanguageId = e.id + 1
                    //Toast.makeText(this, languages[e.id]!!.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        languageRepository.loadAllLanguagesFromDB(this, this)




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