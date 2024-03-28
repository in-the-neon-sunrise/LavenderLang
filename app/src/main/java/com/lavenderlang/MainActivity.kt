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


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("pm2")
        try {
            val res = module.callAttr("test").toString()
            Toast.makeText(this, res, Toast.LENGTH_LONG).show()
        }
        catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
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
        val listLanguages : ListView = findViewById(R.id.listLanguages)
        val adapter: ArrayAdapter<Language> = ArrayAdapter(this, android.R.layout.simple_list_item_1, Languages.languages)
        listLanguages.adapter = adapter
        adapter.notifyDataSetChanged()
        listLanguages.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this@MainActivity, LanguageActivity::class.java)
                intent.putExtra("lang", position)
                startActivity(intent)
            }
    }

}