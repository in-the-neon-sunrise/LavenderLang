package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.WordNotFoundException


interface TranslatorDao {
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl(private val helper: TranslatorHelperDaoImpl = TranslatorHelperDaoImpl()) : TranslatorDao {
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        val letters = (language.vowels + language.consonants).split(" ").joinToString("")
        var curWord = ""
        var curDelimiter = ""
        var res = ""
        synchronized(language) {
            for (letter in text) {
                if (letter == ' ') {
                    if (curWord != "") {
                        res += try {
                            var translatedWord = helper.translateWordFromConlang(language, curWord)
                            if (curWord[0].isUpperCase()) translatedWord =
                                helper.capitalizeWord(translatedWord)
                            translatedWord
                        } catch (e: WordNotFoundException) {
                            curWord
                        }
                        curWord = ""
                    }
                    if (curDelimiter != "") {
                        if (language.puncSymbols.containsValue(curDelimiter)) {
                            for (key in language.puncSymbols.keys) {
                                if (language.puncSymbols[key] == curDelimiter) {
                                    res += key
                                    break
                                }
                            }
                        } else res += curDelimiter
                        curDelimiter = ""
                    }
                    res += letter
                    continue
                }
                if (!letters.contains(letter.lowercaseChar())) {
                    if (curWord != "") {
                        res += try {
                            var translatedWord = helper.translateWordFromConlang(language, curWord)
                            if (curWord[0].isUpperCase()) translatedWord =
                                helper.capitalizeWord(translatedWord)
                            translatedWord
                        } catch (e: WordNotFoundException) {
                            curWord
                        }
                        curWord = ""
                    }
                    curDelimiter += letter
                    continue
                }
                if (curDelimiter != "") {
                    if (language.puncSymbols.containsValue(curDelimiter)) {
                        for (key in language.puncSymbols.keys) {
                            if (language.puncSymbols[key] == curDelimiter) {
                                res += key
                                break
                            }
                        }
                    } else res += curDelimiter
                    curDelimiter = ""
                }
                curWord += letter
            }
            if (curWord != "") {
                res += try {
                    var translatedWord = helper.translateWordFromConlang(language, curWord)
                    if (curWord[0].isUpperCase()) translatedWord =
                        helper.capitalizeWord(translatedWord)
                    translatedWord
                } catch (e: WordNotFoundException) {
                    curWord
                }
            } else if (curDelimiter != "") {
                if (language.puncSymbols.containsValue(curDelimiter)) {
                    for (key in language.puncSymbols.keys) {
                        if (language.puncSymbols[key] == curDelimiter) {
                            res += key
                            break
                        }
                    }
                } else res += curDelimiter
            }
            return res
        }
    }
    override fun translateTextToConlang(language: LanguageEntity, text: String): String {
        val letters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        var curWord = ""
        var curDelimiter = ""
        var res = ""
        synchronized(language) {
            for (letter in text) {
                if (letter == ' ') {
                    if (curWord != "") {
                        res += try {
                            var translatedWord = helper.translateWordToConlang(language, curWord)
                            if (curWord[0].isUpperCase()) translatedWord =
                                helper.capitalizeWord(translatedWord)
                            translatedWord
                        } catch (e: WordNotFoundException) {
                            curWord
                        }
                        curWord = ""
                    }
                    if (curDelimiter != "") {
                        res += if (language.puncSymbols.containsKey(curDelimiter)) {
                            language.puncSymbols[curDelimiter]
                        } else curDelimiter
                        curDelimiter = ""
                    }
                    res += letter
                    continue
                }
                if (!letters.contains(letter.lowercaseChar())) {
                    if (curWord != "") {
                        res += try {
                            var translatedWord = helper.translateWordToConlang(language, curWord)
                            if (curWord[0].isUpperCase()) translatedWord =
                                helper.capitalizeWord(translatedWord)
                            translatedWord
                        } catch (e: WordNotFoundException) {
                            curWord
                        }
                    }
                    curWord = ""
                    curDelimiter += letter
                } else {
                    if (curDelimiter != "") {
                        res += if (language.puncSymbols.containsKey(curDelimiter)) {
                            language.puncSymbols[curDelimiter]
                        } else curDelimiter
                        curDelimiter = ""
                    }
                    curWord += letter
                }
            }
            if (curWord != "") {
                res += try {
                    var translatedWord = helper.translateWordToConlang(language, curWord)
                    if (curWord[0].isUpperCase()) translatedWord =
                        helper.capitalizeWord(translatedWord)
                    translatedWord
                } catch (e: WordNotFoundException) {
                    curWord
                }
            } else if (curDelimiter != "") {
                res += if (language.puncSymbols.containsKey(curDelimiter)) {
                    language.puncSymbols[curDelimiter]
                } else curDelimiter
            }
            return res
        }
    }
}