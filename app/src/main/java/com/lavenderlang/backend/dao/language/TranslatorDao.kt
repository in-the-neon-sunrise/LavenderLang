package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.WordNotFoundException


interface TranslatorDao {
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl(private val helper: TranslatorHelperDaoImpl = TranslatorHelperDaoImpl()) : TranslatorDao {
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        val delimiters = language.puncSymbols + " "
        var curWord = ""
        var res = ""
        TODO("fix delimiters")
        /*
        for (letter in text) {
            if (delimiters.contains(letter)) {
                if (curWord != "") {
                    res += try {
                        var translatedWord = helper.translateWordFromConlang(language, curWord)
                        if (curWord[0].isUpperCase()) translatedWord = helper.capitalizeWord(translatedWord)
                        translatedWord
                    } catch (e: WordNotFoundException) {
                        curWord
                    }
                    curWord = ""
                }
                res += letter
            } else {
                curWord += letter
            }
        }
        if (curWord != "") {
            res += try {
                var translatedWord = helper.translateWordFromConlang(language, curWord)
                if (curWord[0].isUpperCase()) translatedWord = helper.capitalizeWord(translatedWord)
                translatedWord
            } catch (e: WordNotFoundException) {
                curWord
            }
        }
        return res*/
    }
    override fun translateTextToConlang(language: LanguageEntity, text: String): String {
        val delimiters = language.puncSymbols + " "
        var curWord = ""
        var res = ""
        TODO("fix delimiters")
        /*for (letter in text) {
            if (delimiters.contains(letter)) {
                if (curWord != "") {
                    res += try {
                        var translatedWord = helper.translateWordToConlang(language, curWord)
                        if (curWord[0].isUpperCase()) translatedWord = helper.capitalizeWord(translatedWord)
                        translatedWord
                    } catch (e: WordNotFoundException) {
                        curWord
                    }
                }
                curWord = ""
                res += letter
            }
            else curWord += letter
        }
        if (curWord != "") {
            res += try {
                var translatedWord = helper.translateWordToConlang(language, curWord)
                if (curWord[0].isUpperCase()) translatedWord = helper.capitalizeWord(translatedWord)
                translatedWord
            } catch (e: WordNotFoundException) {
                curWord
            }
        }
        return res*/
    }
}