package com.lavenderlang.backend.dao.language

import android.util.Log
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
        for (letter in newLetters.lowercase()) {
            if (letter == ' ') continue
            if (language.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is already in consonants")
            }
            for (ps in language.puncSymbols.values) {
                if (ps.contains(letter)) {
                    throw ForbiddenSymbolsException("Letter $letter is already in punctuation symbols")
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
                throw ForbiddenSymbolsException("Letter $letter is already in vowels")
            }
            for (ps in language.puncSymbols.values) {
                if (ps.contains(letter)) {
                    throw ForbiddenSymbolsException("Letter $letter is already in punctuation symbols")
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
        Log.d("frfrfr", "add $partOfSpeech")
        Log.d("frfrfr", language.capitalizedPartsOfSpeech.toString())
        language.capitalizedPartsOfSpeech.add(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }

    override fun deleteCapitalizedPartOfSpeech(language: LanguageEntity, partOfSpeech: PartOfSpeech) {
        Log.d("frfrfr", "del $partOfSpeech")
        language.capitalizedPartsOfSpeech.remove(partOfSpeech)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateCapitalizedPartsOfSpeech(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
            )
        }
    }
}