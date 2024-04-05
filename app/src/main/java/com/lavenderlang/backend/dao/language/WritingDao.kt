package com.lavenderlang.backend.dao.language

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface WritingDao {
    fun changeVowels(language : LanguageEntity, newLetters : String, context: Context)
    fun changeConsonants(language : LanguageEntity, newLetters : String, context: Context)
    fun addCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech, context: Context)
    fun deleteCapitalizedPartOfSpeech(language : LanguageEntity, partOfSpeech : PartOfSpeech, context: Context)
}

class WritingDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : WritingDao {
    override fun changeVowels(language: LanguageEntity, newLetters: String, context: Context) {
        for (letter in newLetters) {
            if (letter == ' ') continue
            if (language.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is already in consonants")
            }
            if (language.puncSymbols.values.contains(letter.toString())) {
                throw ForbiddenSymbolsException("Letter $letter is already in punctuation symbols")
            }
        }
        language.vowels = newLetters
        Thread {
            languageRepository.updateLanguage(
                context, language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }.start()
    }

    override fun changeConsonants(language: LanguageEntity, newLetters: String, context: Context) {
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
                context, language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }

    override fun addCapitalizedPartOfSpeech(
        language: LanguageEntity,
        partOfSpeech: PartOfSpeech,
        context: Context
    ) {
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                context, language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(
        language: LanguageEntity,
        partOfSpeech: PartOfSpeech,
        context: Context
    ) {
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                context, language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }
}