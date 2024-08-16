package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.data.LanguageRepositoryDEPRECATED
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface WritingDao {
    fun changeVowels(language : LanguageEntity, newLetters : String)
    fun changeConsonants(language : LanguageEntity, newLetters : String)
    fun addCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
    fun deleteCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
}

class WritingDaoImpl(private val languageRepositoryDEPRECATED: LanguageRepositoryDEPRECATED = LanguageRepositoryDEPRECATED()) : WritingDao {
    override fun changeVowels(language: LanguageEntity, newLetters: String) {
        for (letter in newLetters.lowercase()) {
            if (letter == ' ') continue
            if (language.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Буква $letter уже находится в согласных!")
            }
            for (ps in language.puncSymbols.values) {
                if (ps.lowercase().contains(letter)) {
                    throw ForbiddenSymbolsException("Буква $letter уже находится в символах пунктуации!")
                }
            }
        }
        language.vowels = newLetters.lowercase()
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepositoryDEPRECATED.updateVowels(
                MyApp.getInstance().applicationContext, language.languageId,
                language.vowels
            )
        }
    }

    override fun changeConsonants(language: LanguageEntity, newLetters: String) {
        for (letter in newLetters.lowercase()) {
            if (letter == ' ') continue
            if (language.vowels.contains(letter)) {
                throw ForbiddenSymbolsException("Буква $letter уже находится в гласных!")
            }
            for (ps in language.puncSymbols.values) {
                if (ps.lowercase().contains(letter)) {
                    throw ForbiddenSymbolsException("Буква $letter уже находится в символах пунктуации!")
                }
            }
        }
        language.consonants = newLetters.lowercase()
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepositoryDEPRECATED.updateConsonants(
                MyApp.getInstance().applicationContext, language.languageId,
                language.consonants
            )
        }
    }

    override fun addCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        if (language.capitalizedPartsOfSpeech.contains(partOfSpeech)) return
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepositoryDEPRECATED.updateCapitalizedPartsOfSpeech(
                MyApp.getInstance().applicationContext, language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepositoryDEPRECATED.updateCapitalizedPartsOfSpeech(
                MyApp.getInstance().applicationContext, language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }
}