package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
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
        for (letter in newLetters) {
            if (letter == ' ') continue
            if (language.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Letter $letter is already in consonants")
            }
            // fixme: проверять отдельно для каждой строки-пунк. символа (m in MEOW)
            if (language.puncSymbols.values.contains(letter.toString())) {
                throw ForbiddenSymbolsException("Letter $letter is already in punctuation symbols")
            }
        }
        language.vowels = newLetters
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }

    override fun changeConsonants(language: LanguageEntity, newLetters: String) {
        for (letter in newLetters) {
            if (letter == ' ') continue
            if (language.vowels.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is already in vowels")
            }
            if (language.puncSymbols.values.contains(letter.toString())) {
                throw ForbiddenSymbolsException("Letter $letter is already in punctuation symbols")
            }
        }
        language.consonants = newLetters
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }

    override fun addCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }
}