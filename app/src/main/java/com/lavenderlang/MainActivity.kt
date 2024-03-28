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
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.Serializer
import java.io.File

data class ResultAttrs(
    var partOfSpeech : String = "",
    var mutableAttrs : ArrayList<Int> = arrayListOf(),
    var immutableAttrs : ArrayList<Int> = arrayListOf()
)

var serializer : Serializer = Serializer()
var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        var dir = getExternalFilesDirs(null)[0].absolutePath + "\\data"
        serializer = Serializer(getExternalFilesDirs(null)[0].absolutePath)
        languages = serializer.readAllLanguages()
        nextLanguageId = serializer.getMaxLanguageId()
        Toast.makeText(this, serializer.createDir(), Toast.LENGTH_LONG).show()
        var f = File("${getExternalFilesDirs(null)[0].absolutePath}\\data\\a.txt")
        //f.writeText("aaa")
        Toast.makeText(this, f.readText(), Toast.LENGTH_LONG).show()
        f = File("$dir\\language0.json")
        val jsonFile = File("$dir\\language0.json")
        try {
            Serializer.mapper.writeValue(jsonFile, languages[0])
        }
        catch (e : Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
        /*languages[0] = LanguageEntity(0, "AAA")
        ++nextLanguageId
        serializer.saveAllLanguages()
        val folder = File(getExternalFilesDirs(null)[0].absolutePath+"\\data")
        if (folder.exists()) Toast.makeText(this, folder.absolutePath, Toast.LENGTH_LONG).show()
        if (!folder.exists()) {
            Toast.makeText(this, "well", Toast.LENGTH_LONG).show()
            if (!folder.mkdirs()) Toast.makeText(this, "no way", Toast.LENGTH_LONG).show()
        }
        Toast.makeText(this, languages.toString(), Toast.LENGTH_LONG).show()*/

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