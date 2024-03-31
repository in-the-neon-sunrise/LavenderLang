package com.lavenderlang.backend.dao.language

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId
import com.lavenderlang.serializer

interface LanguageDao {
    fun changeName(language : LanguageEntity, newName : String);
    fun changeDescription(language : LanguageEntity, newDescription: String);
    fun changeLetters(language : LanguageEntity, newLetters : String);
    fun changePunctuationSymbols(language : LanguageEntity, newSymbols : String);
    fun copyLanguage(language : LanguageEntity) : LanguageEntity;
    fun createLanguage(name : String, description: String) : LanguageEntity;
    fun deleteLanguage(language : LanguageEntity);
    fun updateLanguage(language : LanguageEntity, context: Context);
}
class LanguageDaoImpl(private val serializer : Serializer = Serializer()
) : LanguageDao {
    companion object {
        fun getLanguagesFromDB(context: AppCompatActivity) {
            val languageRepository = LanguageRepository()
            languageRepository.languages.observe(context
            ) { languageItemList ->
                run {
                    for (e in languageItemList) {
                        languages[e.id] = serializer.deserialize(e.lang)
                        if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
                    }
                }
            }
            languageRepository.loadAllLanguagesFromDB(context, context)
        }
    }
    override fun changeName(language : LanguageEntity, newName : String) {
        language.name = newName
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
    }

    override fun changeLetters(language: LanguageEntity, newLetters: String) {
        language.letters = newLetters
    }

    override fun changePunctuationSymbols(language: LanguageEntity, newSymbols: String) {
        language.puncSymbols = newSymbols
    }

    override fun copyLanguage(language: LanguageEntity): LanguageEntity {
        serializer.updateMaxLanguageId()
        return language.copy(languageId = serializer.getMaxLanguageId() - 1, name = language.name + " копия")
    }
    override fun createLanguage(name: String, description: String): LanguageEntity {
        languages[nextLanguageId] = LanguageEntity(nextLanguageId, name, description)
        return languages[nextLanguageId++]!!
    }
    override fun deleteLanguage(language: LanguageEntity) {
        languages.remove(language.languageId)
        //delete from db!!
    }

    override fun updateLanguage(language: LanguageEntity, context: Context) {
        val languageRepository = LanguageRepository()
        Thread {
            languageRepository.insertLanguage(context, language.languageId, serializer.serializeLanguage(language))
        }.start()
    }
}