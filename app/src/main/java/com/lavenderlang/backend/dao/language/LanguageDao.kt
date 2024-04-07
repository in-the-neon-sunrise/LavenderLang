package com.lavenderlang.backend.dao.language

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageItem
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
            languageRepository.languages.observe(MainActivity.getInstance()
            ) { languageItemList: List<LanguageItem> ->
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
        // fixme: characteristics, words in dict and fullDict

        language.dictionary.languageId = nextLanguageId

        languages[nextLanguageId] = language
        ++nextLanguageId
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
        Toast.makeText(context, "Язык успешно загружен", Toast.LENGTH_LONG).show()
        Log.d("woof", "loaded ${language.name}")
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
        createDocumentResultLauncher.launch("${language.name}.json")
        Log.d("woof", "json done i hope")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            Toast.makeText(MainActivity.getInstance(),
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${language.name}.pdf")
        Log.d("woof", "pdf done i hope")
    }

    fun writeToJSON(uri: Uri) {
        val context = MainActivity.getInstance()
        if (LanguageDaoImpl.curLanguage == null) {
            Log.d("woof", "no language")
            Toast.makeText(context, "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
            return
        }
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use {
                it.write(
                    Serializer.getInstance().serializeLanguage(LanguageDaoImpl.curLanguage!!)
                )
            }
        }
    }

    fun writeToPDF(uri: Uri) {
        val context = MainActivity.getInstance()
        if (LanguageDaoImpl.curLanguage == null) {
            Log.d("woof", "no language")
            Toast.makeText(context, "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
            return
        }
        val language = LanguageDaoImpl.curLanguage!!
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
        }
        val file: DocumentFile = DocumentFileCompat.fromUri(context, uri)!!
        val output = file.openOutputStream(context)
        val document = PdfDocument()
        // fixme: count pages
        val pageInfo = PdfDocument.PageInfo.Builder(1080, 1920, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        // fixme: it's just a test
        paint.setColor(Color.RED)
        paint.textSize = 42F
        val text = "Hello, World"
        val x = 500F
        val y = 900F
        canvas.drawText(text, x, y, paint)
        document.finishPage(page)
        document.writeTo(output)
    }
    /*if (output == null) {
            Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_LONG).show()
            Log.d("woof", "no output")
            return
        }
        try {
            // Step 1: Initialize the Document object
            val document = Document()

            // Step 2: Get an instance of PdfWriter
            PdfWriter.getInstance(
                document,
                output
            )

            // Step 3: Open the document
            document.open()

            // Step 4: Add content to the document
            document.add(Paragraph("Название: ${language.name}"))
            document.add(Paragraph("Описание: ${language.description}"))
            document.add(Paragraph("Гласные: ${language.vowels}"))
            document.add(Paragraph("Согласные: ${language.consonants}"))


            document.add(
                Paragraph(
                    "Знаки препинания: ${
                        language.puncSymbols.values.joinToString(
                            " "
                        )
                    }"
                )
            )
            document.add(
                Paragraph(
                    "Части речи с заглавной буквы: ${
                        language.capitalizedPartsOfSpeech.joinToString(
                            ""
                        )
                    }"
                )
            )

            document.add(Paragraph("Грамматика:"))
            document.add(Paragraph("Варианты характеристик:"))
            document.add(Paragraph(language.grammar.varsGender.toString()))
            // continue for other vars

            document.add(Paragraph("Правила:"))
            for (rule in language.grammar.grammarRules) {
                document.add(Paragraph("${rule.masc}, ${rule.mutableAttrs}, ${rule.transformation}"))
            }
            for (rule in language.grammar.wordFormationRules) {
                document.add(Paragraph("${rule.masc}, ${rule.description}, ${rule.immutableAttrs}, ${rule.transformation}"))
            }

            document.add(Paragraph("Словарь:"))
            for (word in language.dictionary.dict) {
                document.add(Paragraph("${word.word}:${word.translation} - ${word.partOfSpeech}"))
            }

            // Step 5: Close the document
            document.close()
        } catch (e: Exception) {
            Log.d("woof", e.message.toString())
        }*/

}