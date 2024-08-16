package com.lavenderlang.domain.db

import android.content.Context
import com.lavenderlang.domain.db.LanguageIdAndName
import com.lavenderlang.domain.db.LanguageItem
import com.lavenderlang.domain.model.language.LanguageEntity

interface LanguageRepository {
    suspend fun insertLanguage(id: Int, language: LanguageEntity)

    suspend fun deleteLanguage(id: Int)

    suspend fun updateName(id: Int, name: String)

    suspend fun updateDescription(id: Int, description: String)

    suspend fun updateDictionary(id: Int, dictionary: String)

    suspend fun updateGrammar(id: Int, grammar: String)

    suspend fun updateVowels(id: Int, vowels: String)

    suspend fun updateConsonants(id: Int, consonants: String)

    suspend fun updatePuncSymbols(id: Int, puncSymbols: String)

    suspend fun updateCapitalizedPartsOfSpeech(id: Int, capitalizedPartsOfSpeech: String)
    suspend fun getLanguage(id: Int) : LanguageItem?

    suspend fun exists(id: Int) : Boolean

    suspend fun getShortLanguageItems() : List<LanguageIdAndName>

    suspend fun getMaxId() : Int
    suspend fun createUser()
}