package com.lavenderlang.backend.dao.language

import android.content.Context
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity

interface WritingDao {
    fun changeVowels(language : LanguageEntity, newLetters : String, context: Context)
    fun changeConsonants(language : LanguageEntity, newLetters : String, context: Context)
    fun addCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech, context: Context)
    fun deleteCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech, context: Context)
}

class WritingDaoImpl : WritingDao {
    override fun changeVowels(language: LanguageEntity, newLetters: String, context: Context) {
        TODO("Not yet implemented")
    }

    override fun changeConsonants(language: LanguageEntity, newLetters: String, context: Context) {
        TODO("Not yet implemented")
    }

    override fun addCapitalizedPartOfSpeech(
        language: LanguageEntity,
        partOfSpeech: PartOfSpeech,
        context: Context
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteCapitalizedPartOfSpeech(
        language: LanguageEntity,
        partOfSpeech: PartOfSpeech,
        context: Context
    ) {
        TODO("Not yet implemented")
    }
}