package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity

interface WritingDao {
    fun changeVowels(language : LanguageEntity, newLetters : String)
    fun changeConsonants(language : LanguageEntity, newLetters : String)
    fun addCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
    fun deleteCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
}

class WritingDaoImpl : WritingDao{
    override fun changeVowels(language: LanguageEntity, newLetters: String) {
        TODO("Not yet implemented")
    }

    override fun changeConsonants(language: LanguageEntity, newLetters: String) {
        TODO("Not yet implemented")
    }

    override fun addCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        TODO("Not yet implemented")
    }

    override fun deleteCapitalizedPartOfSpeech(
        language: LanguageEntity,
        partOfSpeech: PartOfSpeech
    ) {
        TODO("Not yet implemented")
    }
}