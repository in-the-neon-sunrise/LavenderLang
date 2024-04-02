package com.lavenderlang.backend.dao.language

import android.content.Context
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String, context: Context)
// если символ в буквах конланга - ошибка
}

class PunctuationDaoImpl(val languageRepository: LanguageRepository = LanguageRepository()): PunctuationDao {
    override fun updatePunctuationSymbol(
        language: LanguageEntity,
        id: Int,
        newSymbol: String,
        context: Context
    ) {
        //check if symbol is in language
        for (letter in newSymbol) {
            if (languages[language.languageId]!!.vowels.contains(newSymbol) ||
                languages[language.languageId]!!.consonants.contains(newSymbol)
            ) throw ForbiddenSymbolsException("Symbol $newSymbol is in language!")
        }
        language.puncSymbols[language.puncSymbols.keys.toList()[id]] = newSymbol
        Thread {
            languageRepository.updateLanguage(
                context, language.languageId,
                Serializer.getInstance().serializeLanguage(language)
            )
        }.start()
    }
}