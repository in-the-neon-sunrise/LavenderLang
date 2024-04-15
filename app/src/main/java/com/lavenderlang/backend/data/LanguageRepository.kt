package com.lavenderlang.backend.data

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.Serializer


class LanguageRepository {
    var languages: MutableLiveData<ArrayList<LanguageItem>> =
        MutableLiveData<ArrayList<LanguageItem>>()

    fun loadAllLanguages(context: Context) : ArrayList<LanguageItem> {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()
        return languageDao.selectAll() as ArrayList<LanguageItem>
    }

    fun insertLanguage(context: Context, id: Int, language: LanguageEntity) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        val languageItem = LanguageItem(id,
            language.name,
            language.description,
            Serializer.getInstance().serializeDictionary(language.dictionary),
            Serializer.getInstance().serializeGrammar(language.grammar),
            language.vowels,
            language.consonants,
            Serializer.getInstance().serializePuncSymbols(language.puncSymbols),
            Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
        )
        languageDao.insert(languageItem)
    }

    fun deleteLanguage(context: Context, id: Int) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.deleteById(id)
    }

    /*fun updateLanguage(context: Context, id: Int, language: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        val languageItem = LanguageItem(id, language)
        languageDao.update(languageItem)
    }*/

    fun updateName(context: Context, id: Int, name: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateName(id, name)
    }

    fun updateDescription(context: Context, id: Int, description: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateDescription(id, description)
    }

    fun updateDictionary(context: Context, id: Int, dictionary: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateDictionary(id, dictionary)
    }

    fun updateGrammar(context: Context, id: Int, grammar: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateGrammar(id, grammar)
    }

    fun updateVowels(context: Context, id: Int, vowels: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateVowels(id, vowels)
    }

    fun updateConsonants(context: Context, id: Int, consonants: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateConsonants(id, consonants)
    }

    fun updatePuncSymbols(context: Context, id: Int, puncSymbols: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updatePuncSymbols(id, puncSymbols)
    }

    fun updateCapitalizedPartsOfSpeech(context: Context, id: Int, capitalizedPartsOfSpeech: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.updateCapitalizedPartsOfSpeech(id, capitalizedPartsOfSpeech)
    }
}