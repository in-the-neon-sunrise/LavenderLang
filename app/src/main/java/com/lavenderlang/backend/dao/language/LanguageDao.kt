package com.lavenderlang.backend.dao.language

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId
import com.lowagie.text.Document
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Thread.sleep

interface LanguageDao {
    fun changeName(language: LanguageEntity, newName: String)
    fun changeDescription(language: LanguageEntity, newDescription: String)
    fun copyLanguage(language: LanguageEntity)
    fun createLanguage(name: String, description: String)
    fun deleteLanguage(id: Int)
    fun getLanguagesFromDB()
    fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper)
    fun getLanguageFromFile(path: String, context: AppCompatActivity)
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    companion object {
        var curLanguage: LanguageEntity? = null
    }
    override fun getLanguagesFromDB() {
            languageRepository.languages.observe(MainActivity.getInstance()
            ) { languageItemList ->
                run {
                    for (e in languageItemList) {
                        languages[e.id] = Serializer.getInstance().deserializeLanguage(e.lang)
                        Log.d("woof", "loaded ${languages[e.id]}")
                        if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
                    }
                }
            }
            languageRepository.loadAllLanguages(MainActivity.getInstance(), MainActivity.getInstance())
        }
    override fun changeName(language : LanguageEntity, newName : String) {
        language.name = newName
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(MainActivity.getInstance(), language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(MainActivity.getInstance(), language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }

    override fun copyLanguage(language: LanguageEntity) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")
        languages[nextLanguageId++] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }
        return
    }
    override fun createLanguage(name: String, description: String) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        Log.d("woof", "new $newLang")
        languages[nextLanguageId] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
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
        TODO("Not yet implemented")
    }

    override fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper,
                                      createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            Toast.makeText(MainActivity.getInstance(),
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно :(",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        curLanguage = language
        if (Build.VERSION.SDK_INT <= 29) LanguageHelperDaoImpl().downloadJSONOldApi(language)
        else LanguageHelperDaoImpl().downloadJSONNewApi(language, createDocumentResultLauncher)
        Log.d("woof", "json done i hope")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            Toast.makeText(MainActivity.getInstance(),
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        if (Build.VERSION.SDK_INT <= 29) LanguageHelperDaoImpl().downloadPDFOldApi(language)
        else LanguageHelperDaoImpl().downloadPDFNewApi(language, storageHelper)
        Log.d("woof", "pdf done i hope")
    }

}