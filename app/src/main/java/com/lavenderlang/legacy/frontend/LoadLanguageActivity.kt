package com.lavenderlang.legacy.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.service.exception.FileWorkException
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.runBlocking

class LoadLanguageActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_language_activity)
        if(getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this, InstructionActivity::class.java)
            intent.putExtra("block", 11)
            startActivity(intent)
        }
        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val buttonFirst: Button = findViewById(R.id.buttonFirst)
        val spinnerPath: Spinner = findViewById(R.id.spinnerPath)
        val editTextPath: EditText = findViewById(R.id.editTextPath)
        val buttonOpen: Button = findViewById(R.id.buttonOpen)
        val languageDao: LanguageDao = LanguageDaoImpl()

        var accessiblePathsRaw = DocumentFileCompat.getAccessibleAbsolutePaths(this)
        val accessible = arrayListOf<String>()
        for (key in  accessiblePathsRaw.keys) {
            for (path in accessiblePathsRaw[key]!!) {
                accessible.add(path)
            }
        }
        Log.d("accessible", accessible.toString())

        //эти 3 строчки обновляют список
        var adapterPath: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, accessible)
        spinnerPath.adapter = adapterPath
        adapterPath.notifyDataSetChanged()


        buttonFirst.setOnClickListener {
            val requestCode = 123123
            if (MyApp.storageHelper == null) {
                Log.d("restore", "StorageHelper is null")
                Toast.makeText(this, "Загрузка невозможна", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            MyApp.storageHelper!!.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            accessible.clear()

            accessiblePathsRaw = DocumentFileCompat.getAccessibleAbsolutePaths(this)
            for (key in  accessiblePathsRaw.keys) {
                for (path in accessiblePathsRaw[key]!!) {
                    accessible.add(path)
                }
            }
            adapterPath = ArrayAdapter(this, android.R.layout.simple_spinner_item, accessible)
            spinnerPath.adapter = adapterPath
            adapterPath.notifyDataSetChanged()
        }
        buttonOpen.setOnClickListener {
            Log.d("restore", accessible.toString())
            val path = editTextPath.text.toString()
            val pathPositionSpinner = spinnerPath.selectedItemPosition
            if (pathPositionSpinner == Spinner.INVALID_POSITION) {
                Toast.makeText(this, "Папка не выбрана, сохранение невозможно", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.d("path", "${accessible[pathPositionSpinner]}/${path}")
            try {
                runBlocking {
                languageDao.getLanguageFromFile("${accessible[pathPositionSpinner]}/${path}", this@LoadLanguageActivity)
            }
            } catch (e: FileWorkException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            catch (e: Exception) {
                Log.d("woof", e.message?:"")
                return@setOnClickListener
            }
            val intent = Intent(this, LanguageActivity::class.java)
            // oh well, we need to pass the language id
            // intent.putExtra("lang", languages.keys.toMutableList().last())
            startActivity(intent)
        }
    }

}