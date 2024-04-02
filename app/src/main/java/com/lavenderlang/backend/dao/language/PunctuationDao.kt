package com.lavenderlang.backend.dao.language

import android.content.Context
import com.lavenderlang.backend.entity.language.LanguageEntity

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: Char, context: Context)
// если символ в буквах конланга - ошибка
}

class PunctuationDaoImpl : PunctuationDao {
    override fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: Char, context: Context) {
        TODO()
    }
}