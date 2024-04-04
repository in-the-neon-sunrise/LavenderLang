package com.lavenderlang.backend.dao.language

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.FileFullPath
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

interface LanguageDao {
    fun changeName(language: LanguageEntity, newName: String, context: AppCompatActivity)
    fun changeDescription(language: LanguageEntity, newDescription: String, context: AppCompatActivity)
    fun copyLanguage(language: LanguageEntity, context: AppCompatActivity)
    fun createLanguage(name: String, description: String, context: AppCompatActivity)
    fun deleteLanguage(id: Int, context: AppCompatActivity)
    fun getLanguagesFromDB(context: AppCompatActivity)
    fun downloadLanguageJSON(language: LanguageEntity, context: AppCompatActivity)
    fun downloadLanguagePDF(language: LanguageEntity, context: AppCompatActivity)
    fun getLanguageFromFile(path: String, context: AppCompatActivity)
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    override fun getLanguagesFromDB(context: AppCompatActivity) {
            languageRepository.languages.observe(context
            ) { languageItemList ->
                run {
                    for (e in languageItemList) {
                        languages[e.id] = Serializer.getInstance().deserializeLanguage(e.lang)
                        Log.d("woof", "loaded ${languages[e.id]}")
                        if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
                    }
                }
            }
            languageRepository.loadAllLanguages(context, context)
        }
    override fun changeName(language : LanguageEntity, newName : String, context: AppCompatActivity) {
        language.name = newName
        context.lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String, context: AppCompatActivity) {
        language.description = newDescription
        context.lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }

    override fun copyLanguage(language: LanguageEntity, context: AppCompatActivity) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")
        languages[nextLanguageId++] = newLang
        context.lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(context, newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }
        return
    }
    override fun createLanguage(name: String, description: String, context: AppCompatActivity) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        Log.d("woof", "new $newLang")
        languages[nextLanguageId] = newLang
        context.lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(context, newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }
        ++nextLanguageId
        return
    }
    override fun deleteLanguage(id: Int, context: AppCompatActivity) {
        languages.remove(id)
        context.lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.deleteLanguage(context, id)
        }
    }

    override fun downloadLanguageJSON(language: LanguageEntity, context: AppCompatActivity) {
        val REQUEST_CODE = 12123
        val storageHelper = SimpleStorageHelper(context)
        var accessible = DocumentFileCompat.getAccessibleAbsolutePaths(context)
        if (accessible.isEmpty()) {
            storageHelper.requestStorageAccess(REQUEST_CODE, null, StorageType.EXTERNAL)
            accessible = DocumentFileCompat.getAccessibleAbsolutePaths(context)
        }
        if (accessible.isEmpty()) {
            Toast.makeText(context,
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно :(",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        if (Build.VERSION.SDK_INT <= 29) downloadJSONOldApi(language, context)
        else downloadJSONNewApi(language, context)
        Log.d("woof", DocumentFileCompat.getAccessibleAbsolutePaths(context).values.toString())
    }

    override fun downloadLanguagePDF(language: LanguageEntity, context: AppCompatActivity) {
        TODO("Not yet implemented")
    }

    override fun getLanguageFromFile(path: String, context: AppCompatActivity) {
        TODO("Not yet implemented")
    }

    private fun downloadJSONOldApi(language: LanguageEntity, context: AppCompatActivity) {
        TODO()
    }

    @SuppressLint("NewApi")
    private fun downloadJSONNewApi(language: LanguageEntity, context: AppCompatActivity) {
        val storageHelper = SimpleStorageHelper(context)
        val fullPath = FileFullPath(context, StorageType.EXTERNAL, "Download/Cat")
        storageHelper.createFile("application/json", language.name, fullPath)
        for (i in 0..10000) {
            val file : DocumentFile = DocumentFileCompat.fromFile(context, File(fullPath.absolutePath+"/${language.name}.json"))
                ?: continue
            val output = file.openOutputStream(context)
            if (output == null) {
                Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_LONG).show()
                Log.d("woof", "no output")
                return
            }
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
            Log.d("woof", "written")
            return
        }
        val file : DocumentFile? = DocumentFileCompat.fromFile(context, File(fullPath.absolutePath+"/${language.name}.json"))
        if (file == null) {
            Toast.makeText(context, "Не удалось создать файл", Toast.LENGTH_LONG).show()
            Log.d("woof", "no file")
            return
        }
        Log.d("woof", file.canWrite().toString())
        val output = file.openOutputStream(context)
        if (output == null) {
            Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_LONG).show()
            Log.d("woof", "no output")
            return
        }
        val writer = BufferedWriter(OutputStreamWriter(output))
        writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
        Log.d("woof", "written")
    }
}