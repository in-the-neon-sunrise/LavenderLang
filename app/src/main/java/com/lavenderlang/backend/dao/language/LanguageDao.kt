package com.lavenderlang.backend.dao.language

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.openInputStream
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.*
import com.lavenderlang.backend.service.exception.FileWorkException
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

interface LanguageDao {
    fun changeName(language: LanguageEntity, newName: String)
    fun changeDescription(language: LanguageEntity, newDescription: String)
    fun copyLanguage(language: LanguageEntity)
    fun createLanguage(name: String, description: String)
    fun deleteLanguage(id: Int)
    fun getLanguagesFromDB(context: AppCompatActivity)
    fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun getLanguageFromFile(path: String, context: AppCompatActivity)
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    companion object {
        var curLanguage: LanguageEntity? = null
    }

    override fun getLanguagesFromDB(context: AppCompatActivity) {
        context.lifecycleScope.launch(Dispatchers.IO) {
            val languageItemList = languageRepository.loadAllLanguages(context)
            languages = mutableMapOf()
            nextLanguageId = 0
            for (e in languageItemList) {
                Log.d("woof", "load ${e.name}: ${e.capitalizedPartsOfSpeech}")
                languages[e.id] = LanguageEntity(
                    e.id,
                    e.name,
                    e.description,
                    Serializer.getInstance().deserializeDictionary(e.dictionary),
                    Serializer.getInstance().deserializeGrammar(e.grammar),
                    e.vowels,
                    e.consonants,
                    Serializer.getInstance().deserializePuncSymbols(e.puncSymbols),
                    Serializer.getInstance()
                        .deserializeCapitalizedPartsOfSpeech(e.capitalizedPartsOfSpeech)
                )
                if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
            }
        }
    }


    override fun changeName(language : LanguageEntity, newName : String) {
        language.name = newName
        if (language.languageId !in languages) return
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateName(MainActivity.getInstance(), language.languageId, newName)
        }
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
        if (language.languageId !in languages) return
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateDescription(MainActivity.getInstance(), language.languageId, newDescription)
        }
    }

    override fun copyLanguage(language: LanguageEntity) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")
        languages[nextLanguageId++] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, newLang)
        }
        return
    }
    override fun createLanguage(name: String, description: String) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        Log.d("woof", "new $newLang")
        languages[nextLanguageId] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, newLang)
        }
        ++nextLanguageId
        return
    }
    override fun deleteLanguage(id: Int) {
        languages.remove(id)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.deleteLanguage(MainActivity.getInstance(), id)
        }
    }
    override fun getLanguageFromFile(path: String, context: AppCompatActivity) {
        val origFile = File(path)
        val file = DocumentFileCompat.fromFile(context, origFile)
        if (file == null) {
            Toast.makeText(context, "Не удалось загрузить язык", Toast.LENGTH_LONG).show()
            Log.d("woof", "no file")
            return
        }
        val inputStream = file.openInputStream(context)
        if (inputStream == null) {
            Toast.makeText(context, "Не удалось загрузить язык", Toast.LENGTH_LONG).show()
            Log.d("woof", "no input stream")
            return
        }
        val inputString = inputStream.bufferedReader().use { it.readText() }
        val language = Serializer.getInstance().deserializeLanguage(inputString)
        language.languageId = nextLanguageId

        language.grammar.languageId = nextLanguageId
        for (rule in language.grammar.grammarRules) {
            rule.languageId = nextLanguageId
        }
        for (rule in language.grammar.wordFormationRules) {
                rule.languageId = nextLanguageId
        }
        for (word in language.dictionary.dict) {
            word.languageId = nextLanguageId
        }
        for (key in language.dictionary.fullDict.keys) {
            for (word in language.dictionary.fullDict[key]!!) {
                word.languageId = nextLanguageId
            }
        }

        language.dictionary.languageId = nextLanguageId

        languages[nextLanguageId] = language
        ++nextLanguageId
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(context, language.languageId, language)
        }
        Toast.makeText(context, "Язык успешно загружен", Toast.LENGTH_LONG).show()
        Log.d("woof", "loaded ${language.name}")
    }

    override fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper,
                                      createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            val requestCode = 123123
            MainActivity.getInstance().storageHelper.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            if (accessible.isEmpty())
                throw FileWorkException("Вы не дали приложению доступ к памяти телефона, сохранение невозможно")
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDaoImpl().translitName(language.name)}.json")
        Log.d("woof", "json done i hope")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            val requestCode = 123123
            MainActivity.getInstance().storageHelper.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            if (accessible.isEmpty())
                throw FileWorkException("Вы не дали приложению доступ к памяти телефона, сохранение невозможно")
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDaoImpl().translitName(language.name)}.pdf")
        Log.d("woof", "pdf done i hope")
    }

    fun writeToJSON(uri: Uri) {
        Log.d("woof", "writing json")
        val context = MainActivity.getInstance()
        if (curLanguage == null) {
            Log.d("woof", "no language")
            throw FileWorkException("Не удалось сохранить файл")
        }
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use {
                it.write(
                    Serializer.getInstance().serializeLanguage(curLanguage!!)
                )
            }
        }
    }

    fun writeToPDF(uri: Uri) : Boolean {
        val context = MainActivity.getInstance()
        if (curLanguage == null) {
            Log.d("woof", "no language")
            throw FileWorkException("Не удалось сохранить файл")
        }
        val language = curLanguage!!
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
        }
        PdfWriterDaoImpl().fullWriteToPdf(context, language, uri)
        return true
    }
}