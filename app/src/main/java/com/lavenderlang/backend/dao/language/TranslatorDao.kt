package com.lavenderlang.backend.dao.language

import android.util.Log
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.WordNotFoundException


interface TranslatorDao {
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl(private val helper: TranslatorHelperDaoImpl = TranslatorHelperDaoImpl()) : TranslatorDao {
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        Log.d("dict", "dict: ${language.dictionary.fullDict}")
        Log.d("rules", "rules: ${language.grammar.grammarRules}")
        val letters = (language.vowels + language.consonants).split(" ").joinToString("")
        var curWord = ""
        var curDelimiter = ""
        var res = ""
        Log.d("text", "text: $text, letters: $letters")
        synchronized(language) {
            for (letter in text) {
                Log.d("letter", "letter: $letter, $curWord")
                if (letter == ' ') {
                    if (curWord != "") {
                        res += try {
                            Log.d("word", "word: $curWord")
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

            Log.d("translated", "translated: $res")
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