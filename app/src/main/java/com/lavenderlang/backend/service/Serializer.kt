package com.lavenderlang.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.service.exception.LanguageNotFoundException
import com.lavenderlang.backend.service.exception.WordNotFoundException

class Serializer private constructor() {
    companion object {
        val mapper = ObjectMapper()
        private var instance : Serializer? = null
        fun getInstance() : Serializer {
            if (instance == null) instance = Serializer()
            return instance!!
        }
    }

    fun deserializeLanguage(languageString: String) : LanguageEntity {
        try {
            return mapper.readValue(languageString, LanguageEntity::class.java)
        } catch (e : Exception) {
            throw LanguageNotFoundException(e.message?:"")
        }
    }

    fun deserializeWord(wordString: String) : IWordEntity {
        try {
            return mapper.readValue(wordString, IWordEntity::class.java)
        } catch (e : Exception) {
            throw WordNotFoundException(e.message?:"")
        }
    }

    fun serializeWord(word: IWordEntity) : String {
        return try {
            mapper.writeValueAsString(word)
        } catch (e : Exception) {
            throw WordNotFoundException(e.message?:"")
        }
    }

    fun serializeLanguage(language: LanguageEntity) : String {
        synchronized(language) {
            return try {
                mapper.writeValueAsString(language)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }
}