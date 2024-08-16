package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String)
}

class PunctuationDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()): PunctuationDao {
    override fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: String) {
        //check if symbol is in language
        for (letter in newSymbol) {
            if (MyApp.language!!.vowels.contains(letter.lowercase()) ||
                MyApp.language!!.consonants.contains(letter.lowercase())
            ) throw ForbiddenSymbolsException("Буква $letter находится в алфавите языка!")
        }
        language.puncSymbols[language.puncSymbols.keys.toList()[id]] = newSymbol
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepository.updatePuncSymbols(
                MyApp.getInstance().applicationContext, language.languageId,
                Serializer.getInstance().serializePuncSymbols(language.puncSymbols)
            )
        }
    }
}