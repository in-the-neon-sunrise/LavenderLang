package com.lavenderlang.backend.dao.language

import android.annotation.SuppressLint
import android.net.Uri
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
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.Serializer
import com.lowagie.text.Document
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class LanguageHelperDaoImpl {
    fun downloadJSONOldApi(language: LanguageEntity) {
        TODO()
    }

    fun writeToFile(uri: Uri) {
        val context = MainActivity.getInstance()
        if (LanguageDaoImpl.curLanguage == null) {
            Log.d("woof", "no language")
            Toast.makeText(context, "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
            return
        }
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use { it.write(Serializer.getInstance().serializeLanguage(LanguageDaoImpl.curLanguage!!)) }
        }
    }

    @SuppressLint("NewApi")
    fun downloadJSONNewApi(language: LanguageEntity, createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val context = MainActivity.getInstance()
        val accessiblePath = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance()).values.toList()[0].toList()[0]
        Log.d("woof", "access:$accessiblePath")
        val fullPath = FileFullPath(context, accessiblePath)
        //MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
        //storageHelper.createFile("application/json", language.name, fullPath)
        //sleep(10000)
        createDocumentResultLauncher.launch("${language.name}.json")
        Log.d("woof", fullPath.absolutePath)
        //for (i in 0..10000) {
        val file: DocumentFile? = DocumentFileCompat.fromFile(
            context,
            File(fullPath.absolutePath + "/${language.name}.json")
        )
        if (file == null) {
            Log.d("woof", "no file: ${fullPath.absolutePath}!!!${language.name}.json")
            return//@launch
        }//continue

        val output = file.openOutputStream(context)
        if (output == null) {
            Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_LONG).show()
            Log.d("woof", "no output")
            return//@launch
        }
        val writer = BufferedWriter(OutputStreamWriter(output))
        writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
        Log.d("woof", "written")
        return//@launch
        //}
        //}
    }

    fun downloadPDFOldApi(language: LanguageEntity) {
        TODO()
    }

    @SuppressLint("NewApi")
    fun downloadPDFNewApi(language: LanguageEntity, storageHelper: SimpleStorageHelper) {
        val context = MainActivity.getInstance()
        val accessiblePath = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance()).values.toList()[0].toList()[0]
        val fullPath = FileFullPath(context, StorageType.EXTERNAL, accessiblePath)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            storageHelper.createFile("application/pdf", language.name, fullPath)
            for (i in 0..10000) {
                val file: DocumentFile = DocumentFileCompat.fromFile(
                    context,
                    File(fullPath.absolutePath + "/${language.name}.pdf")
                )
                    ?: continue
                val output = file.openOutputStream(context)
                if (output == null) {
                    Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_LONG).show()
                    Log.d("woof", "no output")
                    return@launch
                }
                try {
                    // Step 1: Initialize the Document object
                    val document = Document()

                    // Step 2: Get an instance of PdfWriter
                    PdfWriter.getInstance(
                        document,
                        FileOutputStream(fullPath.absolutePath + "/${language.name}.pdf")
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
                }
            }
        }
        /*val writer = BufferedWriter(OutputStreamWriter(output))
        writer.use {
            it.write("Название: ${language.name}\n")
            it.write("Описание: ${language.description}\n")
            it.write("Гласные: ${language.vowels}\n")
            it.write("Согласные: ${language.consonants}\n")
            it.write("Знаки препинания: ${language.puncSymbols.values.joinToString(" ")}\n")
            it.write("Части речи с заглавной буквы: ${language.capitalizedPartsOfSpeech.joinToString("")}\n")

            it.write("Грамматика:\n")
            it.write("Варианты характеристик:\n")
            it.write(language.grammar.varsGender.toString() + "\n")
            // continue for other vars

            it.write("Правила:\n")
            for (rule in language.grammar.grammarRules) {
                it.write("${rule.masc}, ${rule.mutableAttrs}, ${rule.transformation}\n")
            }
            for (rule in language.grammar.wordFormationRules) {
                it.write("${rule.masc}, ${rule.description}, ${rule.immutableAttrs}, ${rule.transformation}\n")
            }

            it.write("Словарь:\n")
            for (word in language.dictionary.dict) {
                it.write("${word.word}:${word.translation} - ${word.partOfSpeech}\n")
            }
        }*/
        Log.d("woof", "written")
        return

        /*val file : DocumentFile? = DocumentFileCompat.fromFile(context, File(fullPath.absolutePath+"/${language.name}.json"))
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
        Log.d("woof", "written")*/
    }
}