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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.Serializer
import java.io.File

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
        languages = mutableMapOf(0 to LanguageEntity(0, "a", "aa"))
        nextLanguageId = 1



        /*val languageRepository = LanguageRepository()
        languageRepository.languages.observe(this
        ) { languageItemList ->
            run {
                for (e in languageItemList) {
                    languages[e.id] = serializer.deserialize(e.lang)
                }
            }
        }
        languageRepository.loadAllLanguagesFromDB(this, this)
         */

        /*if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }*/
        //Toast.makeText(this, serializer.f(), Toast.LENGTH_LONG).show()

        /*if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        try {
            val res = module.callAttr("getWrappedAttrs", "кошечки").toString()
            val m = ObjectMapper()
            m.readValue(res, ResultAttrs::class.java)
            Toast.makeText(this, res, Toast.LENGTH_LONG).show()
        }
        catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }*/

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

}