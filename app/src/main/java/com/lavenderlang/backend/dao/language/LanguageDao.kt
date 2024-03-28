package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId

interface LanguageDao {
    fun changeName(language : LanguageEntity, newName : String);
    fun changeDescription(language : LanguageEntity, newDescription: String);
    fun copyLanguage(language : LanguageEntity) : LanguageEntity;
    fun createLanguage(name : String, description: String) : LanguageEntity;
}
class LanguageDaoImpl(private val serializer : Serializer = Serializer(),
                      val dictHandler : DictionaryDaoImpl = DictionaryDaoImpl(),
                      val grammarHandler : GrammarDaoImpl = GrammarDaoImpl()
) : LanguageDao {
    override fun changeName(language : LanguageEntity, newName : String) {
        language.name = newName
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
    }
    override fun copyLanguage(language : LanguageEntity) : LanguageEntity {
        serializer.updateMaxLanguageId()
        return language.copy(languageId = serializer.getMaxLanguageId() - 1, name = language.name + " копия")
    }
    override fun createLanguage(name: String, description: String): LanguageEntity {
        languages[nextLanguageId] = LanguageEntity(nextLanguageId, name, description)
        return languages[nextLanguageId++]!!
    }
}