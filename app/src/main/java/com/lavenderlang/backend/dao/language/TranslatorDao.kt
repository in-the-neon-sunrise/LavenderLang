package com.lavenderlang.backend.dao.language

import com.chaquo.python.Python
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.languages

interface TranslatorDao {
    fun translateWordFromConlang(language: LanguageEntity, word: IWordEntity) : String;
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl: TranslatorDao {
    override fun translateWordFromConlang(language: LanguageEntity, word: IWordEntity): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        return module.callAttr(
            "inflectAttrs", word.word,
            word.partOfSpeech.toString(),
            word.mutableAttrs.values.toString()
        ).toString()
    }
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        val words = text.split(" ")
        var res = ""
        for (word in words) {
            var check = false
            for (w in language.grammar.fullDict) {
                if (word == w.word) {
                    res += "${translateWordFromConlang(language, w)} "
                    check = true
                }
            }
            if (!check) res += "$word "
        }
        return res
    }

    override fun translateTextToConlang(language: LanguageEntity, text: String): String {
        TODO("Not yet implemented")
    }

}