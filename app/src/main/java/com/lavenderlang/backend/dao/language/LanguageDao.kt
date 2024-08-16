package com.lavenderlang.backend.dao.language

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.openInputStream
import com.google.firebase.auth.FirebaseAuth
import com.lavenderlang.domain.db.LanguageItem
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.service.*
import com.lavenderlang.domain.exception.FileWorkException
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lavenderlang.domain.model.language.LanguageEntity

interface LanguageDao {
    suspend fun changeName(language: LanguageEntity, newName: String)
    suspend fun changeDescription(language: LanguageEntity, newDescription: String)
    suspend fun copyLanguage(language: LanguageEntity)
    suspend fun createLanguage(name: String, description: String)
    suspend fun getLanguage(id: Int): LanguageEntity?
    suspend fun deleteLanguage(id: Int)
//    suspend fun getLanguagesFromDB(): MutableMap<Int, LanguageEntity>
    suspend fun getShortLanguagesFromDB(): ArrayList<Pair<Int, String>>
    fun downloadLanguageJSON(
        language: LanguageEntity,
        storageHelper: SimpleStorageHelper,
        createDocumentResultLauncher: ActivityResultLauncher<String>
    )

    fun downloadLanguagePDF(
        language: LanguageEntity,
        storageHelper: SimpleStorageHelper,
        createDocumentResultLauncher: ActivityResultLauncher<String>
    )

    suspend fun getLanguageFromFile(path: String, context: Context)
}

class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) :
    LanguageDao {
    companion object {
        var curLanguage: LanguageEntity? = null
    }

//    override suspend fun getLanguagesFromDB(): MutableMap<Int, LanguageEntity> {
//        val languages = mutableMapOf<Int, LanguageEntity>()
//        var languageItemList: List<LanguageItem>
//        withContext(Dispatchers.IO) {
//            languageItemList = languageRepository.loadAllLanguages(
//                MyApp.getInstance().applicationContext
//            )
//        }
//        MyApp.nextLanguageId = 0
//        for (e in languageItemList) {
//            languages[e.id] = LanguageEntity(
//                e.id,
//                e.name,
//                e.description,
//                Serializer.getInstance().deserializeDictionary(e.dictionary),
//                Serializer.getInstance().deserializeGrammar(e.grammar),
//                e.vowels,
//                e.consonants,
//                Serializer.getInstance().deserializePuncSymbols(e.puncSymbols),
//                Serializer.getInstance()
//                    .deserializeCapitalizedPartsOfSpeech(e.capitalizedPartsOfSpeech)
//            )
//            if (MyApp.nextLanguageId <= e.id) MyApp.nextLanguageId = e.id + 1
//        }
//        return languages
//    }

    override suspend fun getShortLanguagesFromDB(): ArrayList<Pair<Int, String>> {
        var languages: ArrayList<Pair<Int, String>>
        withContext(Dispatchers.IO) {
            val items = languageRepository.getShortLanguageItems(
                MyApp.getInstance().applicationContext
            )
            languages = ArrayList(items.map { Pair(it.id, it.name) })
        }
        return languages
    }


    override suspend fun changeName(language: LanguageEntity, newName: String) {
        language.name = newName
        withContext(Dispatchers.IO) {
            if (languageRepository.exists(
                    MyApp.getInstance().applicationContext,
                    language.languageId
                )
            ) return@withContext
        }
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepository.updateName(
                MyApp.getInstance().applicationContext, language.languageId, newName
            )
        }
    }

    override suspend fun changeDescription(language: LanguageEntity, newDescription: String) {
        language.description = newDescription
        withContext(Dispatchers.IO) {
            if (languageRepository.exists(
                    MyApp.getInstance().applicationContext,
                    language.languageId
                )
            ) return@withContext
        }
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepository.updateDescription(
                MyApp.getInstance().applicationContext, language.languageId, newDescription
            )
        }
    }

    override suspend fun copyLanguage(language: LanguageEntity) {
        val newLang =
            // fixme: shared prefs
            // language.copy(languageId = MyApp.nextLanguageId, name = language.name + " копия")
            language.copy(name = language.name + " копия")

//        newLang.grammar.languageId = MyApp.nextLanguageId
//        for (rule in newLang.grammar.grammarRules) {
//            rule.languageId = MyApp.nextLanguageId
//        }
//        for (rule in newLang.grammar.wordFormationRules) {
//            rule.languageId = MyApp.nextLanguageId
//        }
//        for (word in newLang.dictionary.dict) {
//            word.languageId = MyApp.nextLanguageId
//        }
//        for (key in newLang.dictionary.fullDict.keys) {
//            for (word in newLang.dictionary.fullDict[key]!!) {
//                word.languageId = MyApp.nextLanguageId
//            }
//        }
//        newLang.dictionary.languageId = MyApp.nextLanguageId

        withContext(Dispatchers.IO) {
            languageRepository.insertLanguage(
                MyApp.getInstance().applicationContext, newLang.languageId, newLang
            )
        }
        // ++MyApp.nextLanguageId
        // get shared preferences
        val sharedPref =
            MyApp.getInstance().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("lang", newLang.languageId)
        editor.apply()
        // set MyApp.language
        MyApp.language = newLang
    }

    override suspend fun createLanguage(name: String, description: String) {
        // fixme: shared prefs
        // val newLang = LanguageEntity(MyApp.nextLanguageId, name, description)
        val newLang = LanguageEntity(0, name, description)
        LanguageRepository().insertLanguage(
            MyApp.getInstance().applicationContext, newLang.languageId, newLang
        )
        // ++MyApp.nextLanguageId
        // get shared preferences
        val sharedPref =
            MyApp.getInstance().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("lang", newLang.languageId)
        editor.apply()
        // set MyApp.language
        MyApp.language = newLang
        return
    }

    private fun getLanguageFromFirebase(id: Int): LanguageEntity? {
        var language: LanguageEntity? = null
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val languageItem = LanguageItem(
                        (document.data?.get("id") as Long).toInt(),
                        document.data?.get("name") as String,
                        document.data?.get("description") as String,
                        document.data?.get("dictionary") as String,
                        document.data?.get("grammar") as String,
                        document.data?.get("vowels") as String,
                        document.data?.get("consonants") as String,
                        document.data?.get("puncSymbols") as String,
                        document.data?.get("capitalizedPartsOfSpeech") as String
                    )
                    language = Serializer.getInstance().getLanguageEntityFromLanguageItem(languageItem)
                    // get shared preferences
                    val sharedPref =
                        MyApp.getInstance().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putInt("lang", language!!.languageId)
                    editor.apply()
                    // set MyApp.language
                    MyApp.language = language
                    Log.d("firebase", "success getting")
                } else {
                    Log.d("firebase", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("firebase", "get failed with ", exception)
            }
        return language
    }

    override suspend fun getLanguage(id: Int): LanguageEntity? {
        val language: LanguageEntity?
        var check = false
        withContext(Dispatchers.IO) {
            if (languageRepository.exists(MyApp.getInstance().applicationContext, id))
                check = true
        }
        if (!check) return null
        val languageItem: LanguageItem
        withContext(Dispatchers.IO) {
            languageItem = languageRepository.getLanguage(
                MyApp.getInstance().applicationContext, id
            )
        }
        language = Serializer.getInstance().getLanguageEntityFromLanguageItem(languageItem)
        // get shared preferences
        val sharedPref =
            MyApp.getInstance().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("lang", language.languageId)
        editor.apply()
        // set MyApp.language
        MyApp.language = language

        Log.d("firebase", "getting language from firebase")
        val languageFromFirebase = getLanguageFromFirebase(id)
        return languageFromFirebase
    }

    override suspend fun deleteLanguage(id: Int) {
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepository.deleteLanguage(
                MyApp.getInstance().applicationContext, id
            )
        }
    }

    override suspend fun getLanguageFromFile(path: String, context: Context) {
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
//        language.languageId = MyApp.nextLanguageId
//
//        language.grammar.languageId = MyApp.nextLanguageId
//        for (rule in language.grammar.grammarRules) {
//            rule.languageId = MyApp.nextLanguageId
//        }
//        for (rule in language.grammar.wordFormationRules) {
//            rule.languageId = MyApp.nextLanguageId
//        }
//        for (word in language.dictionary.dict) {
//            word.languageId = MyApp.nextLanguageId
//        }
//        for (key in language.dictionary.fullDict.keys) {
//            for (word in language.dictionary.fullDict[key]!!) {
//                word.languageId = MyApp.nextLanguageId
//            }
//        }
//
//        language.dictionary.languageId = MyApp.nextLanguageId
//
//        ++MyApp.nextLanguageId
        languageRepository.insertLanguage(
            MyApp.getInstance().applicationContext, language.languageId, language
        )
        // get shared preferences
        val sharedPref =
            MyApp.getInstance().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("lang", language.languageId)
        editor.apply()
        // set MyApp.language
        MyApp.language = language
        Log.d("file", "loaded ${language.name}")
    }

    override fun downloadLanguageJSON(
        language: LanguageEntity, storageHelper: SimpleStorageHelper,
        createDocumentResultLauncher: ActivityResultLauncher<String>
    ) {
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDaoImpl().translitName(language.name)}.json")
        Log.d("file", "json done")
    }

    override fun downloadLanguagePDF(
        language: LanguageEntity,
        storageHelper: SimpleStorageHelper,
        createDocumentResultLauncher: ActivityResultLauncher<String>
    ) {
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

    fun writeToPDF(uri: Uri): Boolean {
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