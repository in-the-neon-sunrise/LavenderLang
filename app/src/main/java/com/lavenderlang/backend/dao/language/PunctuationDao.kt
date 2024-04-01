package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.entity.language.LanguageEntity

interface PunctuationDao {
    fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: Char) // если символ в буквах конланга - ошибка
}

class PunctuationDaoImpl : PunctuationDao {
    override fun updatePunctuationSymbol(language: LanguageEntity, id: Int, newSymbol: Char) {
        TODO()
    }
}