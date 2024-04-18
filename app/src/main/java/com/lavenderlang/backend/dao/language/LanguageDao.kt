package com.lavenderlang.backend.dao.language

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.openInputStream
import com.lavenderlang.frontend.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.*
import com.lavenderlang.backend.service.exception.FileWorkException
import com.lavenderlang.frontend.MyApp
import com.lavenderlang.frontend.languages
import com.lavenderlang.frontend.nextLanguageId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
    fun getLanguagesFromDB()
    fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun getLanguageFromFile(path: String, context: AppCompatActivity)
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    companion object {
        var curLanguage: LanguageEntity? = null
    }

    override fun getLanguagesFromDB() {
        GlobalScope.launch(Dispatchers.IO) {
            val languageItemList = languageRepository.loadAllLanguages(
                MyApp.getInstance().applicationContext)
            languages = mutableMapOf()
            nextLanguageId = 0
            for (e in languageItemList) {
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
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateName(
                MyApp.getInstance().applicationContext, language.languageId, newName)
        }
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
        if (language.languageId !in languages) return
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateDescription(
                MyApp.getInstance().applicationContext, language.languageId, newDescription)
        }
    }

    override fun copyLanguage(language: LanguageEntity) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")

        newLang.grammar.languageId = nextLanguageId
        for (rule in newLang.grammar.grammarRules) {
            rule.languageId = nextLanguageId
        }
        for (rule in newLang.grammar.wordFormationRules) {
            rule.languageId = nextLanguageId
        }
        for (word in newLang.dictionary.dict) {
            word.languageId = nextLanguageId
        }
        for (key in newLang.dictionary.fullDict.keys) {
            for (word in newLang.dictionary.fullDict[key]!!) {
                word.languageId = nextLanguageId
            }
        }

        newLang.dictionary.languageId = nextLanguageId

        languages[nextLanguageId++] = newLang
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(
                MyApp.getInstance().applicationContext, newLang.languageId, newLang)
        }
        return
    }
    override fun createLanguage(name: String, description: String) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        languages[nextLanguageId] = newLang
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(
                MyApp.getInstance().applicationContext, newLang.languageId, newLang)
        }
        ++nextLanguageId
        return
    }
    override fun deleteLanguage(id: Int) {
        languages.remove(id)
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.deleteLanguage(
                MyApp.getInstance().applicationContext, id)
        }
    }
    override fun getLanguageFromFile(path: String, context: AppCompatActivity) {
        val origFile = File(path)
        // fixme: do i need context here? or just myApp?
        val file = DocumentFileCompat.fromFile(MyApp.getInstance().applicationContext, origFile)
        if (file == null) {
            Log.d("file", "no file")
            throw FileWorkException("Не удалось загрузить язык")
        }
        val inputStream = file.openInputStream(MyApp.getInstance().applicationContext)
        if (inputStream == null) {
            Log.d("file", "no input stream")
            throw FileWorkException("Не удалось загрузить язык")
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
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(
                MyApp.getInstance().applicationContext, language.languageId, language)
        }
        Log.d("file", "loaded ${language.name}")
    }

    override fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper,
                                      createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MyApp.getInstance().applicationContext)
        if (accessible.isEmpty()) {
            val requestCode = 123123
            storageHelper.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            if (accessible.isEmpty())
                throw FileWorkException("Вы не дали приложению доступ к памяти телефона, сохранение невозможно")
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDaoImpl().translitName(language.name)}.json")
        Log.d("file", "json done")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MyApp.getInstance().applicationContext)
        if (accessible.isEmpty()) {
            val requestCode = 123123
            storageHelper.requestStorageAccess(
                requestCode,
                null,
                StorageType.EXTERNAL
            )
            if (accessible.isEmpty())
                throw FileWorkException("Вы не дали приложению доступ к памяти телефона, сохранение невозможно")
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDaoImpl().translitName(language.name)}.pdf")
        Log.d("file", "pdf done")
    }

    fun writeToJSON(uri: Uri) {
        Log.d("woof", "writing json")
        val context = MyApp.getInstance().applicationContext
        if (curLanguage == null) {
            Log.d("file", "no language")
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
        val context = MyApp.getInstance().applicationContext
        if (curLanguage == null) {
            Log.d("file", "no language")
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