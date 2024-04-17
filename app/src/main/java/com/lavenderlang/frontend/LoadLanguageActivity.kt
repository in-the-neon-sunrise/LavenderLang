package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.service.exception.FileWorkException

class LoadLanguageActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_language_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this, InstructionActivity::class.java)
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

        val accessible = arrayListOf<String>()
        var accessiblePathsRaw = DocumentFileCompat.getAccessibleAbsolutePaths(this)
        for (key in  accessiblePathsRaw.keys) {
            for (path in accessiblePathsRaw[key]!!) {
                accessible.add(path)
            }
        }
        //эти 3 строчки обновляют список
        var adapterPath: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, accessible)
        spinnerPath.adapter = adapterPath
        adapterPath.notifyDataSetChanged()


        buttonFirst.setOnClickListener {
            val requestCode = 123123
            MainActivity.getInstance().storageHelper.requestStorageAccess(
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
                languageDao.getLanguageFromFile("${accessible[pathPositionSpinner]}/${path}", this)
            } catch (e: FileWorkException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            catch (e: Exception) {
                Log.d("woof", e.message?:"")
                return@setOnClickListener
            }
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("lang", languages.keys.toMutableList().last())
            startActivity(intent)
        }
    }

}