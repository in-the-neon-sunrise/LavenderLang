package com.lavenderlang.backend.data

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData


class LanguageRepository {
    var languages: MutableLiveData<ArrayList<LanguageItem>> =
        MutableLiveData<ArrayList<LanguageItem>>()

    fun loadAllLanguages(context: Context, lifecycleOwner: LifecycleOwner) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()
        languageDao.selectAll().observe(lifecycleOwner) { languageItems ->
            languages.setValue(
                languageItems as ArrayList<LanguageItem>
            )
        }
    }

    fun insertLanguage(context: Context, id: Int, language: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        val languageItem = LanguageItem(id, language)
        languageDao.insert(languageItem)
    }

    fun deleteLanguage(context: Context, id: Int) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        languageDao.deleteById(id)
    }

    fun updateLanguage(context: Context, id: Int, language: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao()

        val languageItem = LanguageItem(id, language)
        languageDao.update(languageItem)
    }
}