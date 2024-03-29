package com.lavenderlang.backend.data

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lavenderlang.Language


class LanguageRepository {
    var languages: MutableLiveData<ArrayList<LanguageItem>> =
        MutableLiveData<ArrayList<LanguageItem>>()

    fun loadLanguageFromDB(context: Context, lifecycleOwner: LifecycleOwner, id: Int) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao
        languageDao.selectById(id).observe(lifecycleOwner) { languageItems ->
            languages.setValue(
                languageItems as ArrayList<LanguageItem>
            )
        }
    }

    fun loadAllLanguagesFromDB(context: Context, lifecycleOwner: LifecycleOwner) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao
        languageDao.selectAll().observe(lifecycleOwner) { languageItems ->
            languages.setValue(
                languageItems as ArrayList<LanguageItem>
            )
        }
    }

    fun insertLanguage(context: Context, id: Int, language: String) {
        val languageDB: LanguageDB = LanguageDB.getInstance(context)
        val languageDao: LanguageDao = languageDB.languageDao
        val languageItem: LanguageItem = LanguageItem(id, language)
        languageDao.insert(languageItem)
    }
}