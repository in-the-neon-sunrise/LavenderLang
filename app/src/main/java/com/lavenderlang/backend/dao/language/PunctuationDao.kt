package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String)
// если символ в буквах конланга - ошибка
}

class PunctuationDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()): PunctuationDao {
    override fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String) {
        //check if symbol is in language
        for (letter in newSymbol) {
            if (languages[language.languageId]!!.vowels.contains(newSymbol.lowercase()) ||
                languages[language.languageId]!!.consonants.contains(newSymbol.lowercase())
            ) throw ForbiddenSymbolsException("Symbol $newSymbol is in language!")
        }
        language.puncSymbols[language.puncSymbols.keys.toList()[id]] = newSymbol
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(), language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }
    }
}