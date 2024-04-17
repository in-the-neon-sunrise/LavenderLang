package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.frontend.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.frontend.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String)
// если символ в буквах конланга - ошибка
}

class PunctuationDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()): PunctuationDao {
    override fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String) {
        if (language.languageId !in languages) return
        //check if symbol is in language
        for (letter in newSymbol) {
            if (languages[language.languageId]!!.vowels.contains(letter.lowercase()) ||
                languages[language.languageId]!!.consonants.contains(letter.lowercase())
            ) throw ForbiddenSymbolsException("Буква $letter находится в алфавите языка!")
        }
        language.puncSymbols[language.puncSymbols.keys.toList()[id]] = newSymbol
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updatePuncSymbols(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializePuncSymbols(language.puncSymbols)
            )
        }
    }
}