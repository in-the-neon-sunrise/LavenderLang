package com.lavenderlang.backend.dao.language

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId

interface LanguageDao {
    fun changeName(language: LanguageEntity, newName: String, context: Context)
    fun changeDescription(language: LanguageEntity, newDescription: String, context: Context)
    fun copyLanguage(language: LanguageEntity, context: Context)
    fun createLanguage(name: String, description: String, context: Context)
    fun deleteLanguage(id: Int, context: Context)
    fun getLanguagesFromDB(context: AppCompatActivity)
    fun downloadLanguageJSON(language: LanguageEntity, activity: AppCompatActivity)
    fun downloadLanguagePDF(language: LanguageEntity, activity: AppCompatActivity)
    fun getLanguageFromFile(path: String, activity: AppCompatActivity)//кошечку
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    override fun getLanguagesFromDB(context: AppCompatActivity) {
            languageRepository.languages.observe(context
            ) { languageItemList ->
                run {
                    for (e in languageItemList) {
                        languages[e.id] = Serializer.getInstance().deserializeLanguage(e.lang)
                        if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
                    }
                }
            }
            languageRepository.loadAllLanguages(context, context)
        }
    override fun changeName(language : LanguageEntity, newName : String, context: Context) {
        language.name = newName
        Thread {
            languageRepository.updateLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }.start()
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String, context: Context) {
        language.description = newDescription
        Thread {
            languageRepository.updateLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }.start()
    }

    override fun copyLanguage(language: LanguageEntity, context: Context) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")
        languages[nextLanguageId++] = newLang
        Thread {
            languageRepository.insertLanguage(context, newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }.start()
        return
    }
    override fun createLanguage(name: String, description: String, context: Context) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        languages[nextLanguageId] = newLang
        Thread {
            languageRepository.insertLanguage(context, nextLanguageId, Serializer.getInstance().serializeLanguage(newLang))
        }.start()
        ++nextLanguageId
        return
    }
    override fun deleteLanguage(id: Int, context: Context) {
        languages.remove(id)
        Thread {
            languageRepository.deleteLanguage(context, id)
        }.start()
    }

    override fun downloadLanguageJSON(language: LanguageEntity, activity: AppCompatActivity) {
        TODO("Not yet implemented")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, activity: AppCompatActivity) {
        TODO("Not yet implemented")
    }

    override fun getLanguageFromFile(path: String, activity: AppCompatActivity) {
        TODO("Not yet implemented")
    }
}