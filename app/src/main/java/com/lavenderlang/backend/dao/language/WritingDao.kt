package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.frontend.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import kotlinx.coroutines.Dispatchers
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
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateVowels(
                MainActivity.getInstance(), language.languageId,
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
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateConsonants(
                MainActivity.getInstance(), language.languageId,
                language.consonants
            )
        }
    }

    override fun addCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        if (language.capitalizedPartsOfSpeech.contains(partOfSpeech)) return
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }
}