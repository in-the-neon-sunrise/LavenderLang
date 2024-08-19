package com.lavenderlang.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.domain.db.LanguageItem
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.exception.LanguageNotFoundException
import com.lavenderlang.ui.MyApp

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

    fun serializeLanguage(language: LanguageEntity) : String {
        synchronized(MyApp) {
            return try {
                mapper.writeValueAsString(language)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }

    fun serializeGrammar(grammar: GrammarEntity) : String {
        synchronized(MyApp) {
            return try {
                mapper.writeValueAsString(grammar)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }

    fun deserializeGrammar(grammarString: String) : GrammarEntity {
        try {
            return mapper.readValue(grammarString, GrammarEntity::class.java)
        } catch (e : Exception) {
            throw LanguageNotFoundException(e.message?:"")
        }
    }

    fun serializeDictionary(dictionary: DictionaryEntity) : String {
        synchronized(MyApp) {
            return try {
                mapper.writeValueAsString(dictionary)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }

    fun deserializeDictionary(dictionaryString: String) : DictionaryEntity {
        try {
            return mapper.readValue(dictionaryString, DictionaryEntity::class.java)
        } catch (e : Exception) {
            throw LanguageNotFoundException(e.message?:"")
        }
    }

    fun serializePuncSymbols(puncSymbols: MutableMap<String, String>) : String {
        synchronized(MyApp) {
            return try {
                mapper.writeValueAsString(puncSymbols)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }

    fun deserializePuncSymbols(puncSymbolsString: String) : MutableMap<String, String> {
        try {
            return mapper.readValue(puncSymbolsString, MutableMap::class.java) as MutableMap<String, String>
        } catch (e : Exception) {
            throw LanguageNotFoundException(e.message?:"")
        }
    }

    fun serializeCapitalizedPartsOfSpeech(capitalizedPartsOfSpeech: ArrayList<PartOfSpeech>) : String {
        synchronized(MyApp) {
            return try {
                mapper.writeValueAsString(capitalizedPartsOfSpeech)
            } catch (e: Exception) {
                throw LanguageNotFoundException(e.message ?: "")
            }
        }
    }

    fun deserializeCapitalizedPartsOfSpeech(capitalizedPartsOfSpeechString: String) : ArrayList<PartOfSpeech> {
        try {
            val strings = mapper.readValue(capitalizedPartsOfSpeechString, ArrayList::class.java) as ArrayList<String>
            val partsOfSpeech = arrayListOf<PartOfSpeech>()
            for (string in strings) {
                if (!partsOfSpeech.contains(PartOfSpeech.valueOf(string))) partsOfSpeech.add(
                    PartOfSpeech.valueOf(string))
            }
            return partsOfSpeech
        } catch (e : Exception) {
            throw LanguageNotFoundException(e.message?:"")
        }
    }

    fun getLanguageEntityFromLanguageItem(languageItem: LanguageItem) : LanguageEntity {
        return LanguageEntity(
            languageItem.id,
            languageItem.name,
            languageItem.description,
            deserializeDictionary(languageItem.dictionary),
            deserializeGrammar(languageItem.grammar),
            languageItem.vowels,
            languageItem.consonants,
            deserializePuncSymbols(languageItem.puncSymbols),
            deserializeCapitalizedPartsOfSpeech(languageItem.capitalizedPartsOfSpeech)
        )
    }
}