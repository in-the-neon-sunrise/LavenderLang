package com.lavenderlang.backend.dao.language

import android.util.Log
import com.chaquo.python.Python
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.Characteristic
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.service.ResultAttrs
import com.lavenderlang.backend.service.WordNotFoundException
import com.lavenderlang.serializer


interface TranslatorDao {
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl(private val helper: TranslatorHelperDaoImpl = TranslatorHelperDaoImpl()) : TranslatorDao {
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        val delimiters = language.puncSymbols + " "
        var curWord = ""
        var res = ""
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
        return res
    }
    override fun translateTextToConlang(language: LanguageEntity, text: String): String {
        val delimiters = language.puncSymbols + " "
        var curWord = ""
        var res = ""
        for (letter in text) {
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
        return res
    }
}