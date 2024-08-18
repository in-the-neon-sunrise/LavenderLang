package com.lavenderlang.domain.usecase.translator

import android.util.Log
import com.lavenderlang.domain.exception.WordNotFoundException
import com.lavenderlang.domain.model.language.LanguageEntity

class TranslateFromConlangUseCase {
    companion object {
        private fun translateWordFromConlang(language: LanguageEntity, word: String): String {
            for (key in language.dictionary.fullDict.keys) {
                for (w in language.dictionary.fullDict[key]!!) {
                    if (w.word.lowercase() == word.lowercase()) {
                        return w.translation
                    }
                }
            }
            throw WordNotFoundException("Word not found")
        }

        private fun capitalizeWord(word: String): String {
            return word[0].uppercaseChar() + word.substring(1)
        }

        fun execute(language: LanguageEntity, text: String): String {
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
                                var translatedWord = translateWordFromConlang(language, curWord)
                                if (curWord[0].isUpperCase()) translatedWord =
                                    capitalizeWord(translatedWord)
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
                                var translatedWord = translateWordFromConlang(language, curWord)
                                if (curWord[0].isUpperCase()) translatedWord =
                                    capitalizeWord(translatedWord)
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
                        var translatedWord = translateWordFromConlang(language, curWord)
                        if (curWord[0].isUpperCase()) translatedWord =
                            capitalizeWord(translatedWord)
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
    }
}