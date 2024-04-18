package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.frontend.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.frontend.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface WritingDao {
    fun changeVowels(language : LanguageEntity, newLetters : String)
    fun changeConsonants(language : LanguageEntity, newLetters : String)
    fun addCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
    fun deleteCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech)
}

class WritingDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : WritingDao {
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
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateVowels(
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
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateConsonants(
                MyApp.getInstance().applicationContext, language.languageId,
                language.consonants
            )
        }
    }

    override fun addCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        if (language.capitalizedPartsOfSpeech.contains(partOfSpeech)) return
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MyApp.getInstance().applicationContext, language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        GlobalScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MyApp.getInstance().applicationContext, language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }
}