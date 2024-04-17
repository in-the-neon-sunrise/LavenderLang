package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.service.exception.FileWorkException


class LanguageActivity: AppCompatActivity() {
    lateinit var createJSONLauncher: ActivityResultLauncher<String>
    lateinit var createPDFLauncher : ActivityResultLauncher<String>

    companion object{
        var id_lang: Int = 0
        val languageDao: LanguageDaoImpl = LanguageDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_activity)

        createJSONLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            Log.d("json start write", "result")
            if (uri != null) {
                Log.d("json write", uri.toString())
                LanguageDaoImpl().writeToJSON(uri)
            } else {
                Log.d("json write", "uri is null")
            }
        }

        createPDFLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            Log.d("pdf start write", "result")
            if (uri != null) {
                LanguageDaoImpl().writeToPDF(uri)
            } else {
                Log.d("pdf write", "uri is null")
            }
        }

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@LanguageActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //parts of language
        val buttonDictionary: Button = findViewById(R.id.buttonDictionary)
        buttonDictionary.setOnClickListener {
            val intent = Intent(this@LanguageActivity, DictionaryActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonGrammar: Button = findViewById(R.id.buttonGrammar)
        buttonGrammar.setOnClickListener {
            val intent = Intent(this@LanguageActivity, GrammarActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonPunctuation : Button = findViewById(R.id.buttonPunctuation)
        buttonPunctuation.setOnClickListener {
            val intent = Intent(this@LanguageActivity, PunctuationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonWriting : Button = findViewById(R.id.buttonWriting)
        buttonWriting.setOnClickListener {
            val intent = Intent(this@LanguageActivity, WritingActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonWordFormation : Button = findViewById(R.id.buttonWordFormation)
        buttonWordFormation.setOnClickListener {
            val intent = Intent(this@LanguageActivity, WordFormationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //how it was started?
        val editLanguageName: EditText = findViewById(R.id.editLanguageName)
        val editDescription: EditText = findViewById(R.id.editDescription)

        when(val lang = intent.getIntExtra("lang", -1)){
            -1 -> {
                id_lang = nextLanguageId
                languageDao.createLanguage("Язык$id_lang", "")
                editLanguageName.setText(languages[id_lang]?.name)
            }
            else -> {
                id_lang = lang
                editLanguageName.setText(languages[id_lang]?.name)
            }
        }
        if(languages[id_lang]?.description != "") editDescription.setText(languages[id_lang]?.description)
    }
    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()

        val editLanguageName: EditText = findViewById(R.id.editLanguageName)
        val editDescription: EditText = findViewById(R.id.editDescription)
        //check changing
        editLanguageName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeName(languages[id_lang]!!, editLanguageName.text.toString())
            }
        })
        editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                languageDao.changeDescription(languages[id_lang]!!, editDescription.text.toString())
            }
        })

        val buttonFile: Button = findViewById(R.id.buttonFile)
        buttonFile.setOnClickListener {
            try {
            LanguageDaoImpl().downloadLanguageJSON(
                languages[id_lang]!!,
                MainActivity.getInstance().storageHelper,
                createJSONLauncher)
            } catch (e: FileWorkException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        val buttonPDF: Button = findViewById(R.id.buttonPDF)
        buttonPDF.setOnClickListener {
            try {
            LanguageDaoImpl().downloadLanguagePDF(
                languages[id_lang]!!,
                MainActivity.getInstance().storageHelper,
                createPDFLauncher)
            } catch (e: FileWorkException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            catch (_: Exception) {
            }
        }
        val buttonCopy: Button = findViewById(R.id.buttonCopy)
        buttonCopy.setOnClickListener {
            languageDao.copyLanguage(languages[id_lang]!!)
            val intent = Intent(this@LanguageActivity, MainActivity::class.java)
            startActivity(intent)
        }
        val buttonDelete: Button = findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            languageDao.deleteLanguage(id_lang)
            val intent = Intent(this@LanguageActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            id_lang = data?.getIntExtra("lang", -1) ?: -1
        }
    }
}