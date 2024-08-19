package com.lavenderlang.domain.usecase.translator

import android.util.Log
import com.chaquo.python.Python
import com.lavenderlang.domain.exception.WordNotFoundException
import com.lavenderlang.domain.model.language.LanguageEntity

class TranslateToConlangUseCase {
    companion object {
        private fun translateWordToConlang(language: LanguageEntity, word: String): String {
            val py = Python.getInstance()
            val module = py.getModule("pm3")
            val normalForm = module.callAttr("get_normal_form", word.lowercase()).toString()
            for (key in language.dictionary.fullDict.keys) {
                val keyWord = key.split(":")[0]
                val keyTranslation = key.split(":")[1]
                if (keyTranslation != normalForm) continue
                return keyWord
            }
            throw WordNotFoundException("Word not found")
        }

        private fun capitalizeWord(word: String): String {
            return word[0].uppercaseChar() + word.substring(1)
        }

        fun execute(language: LanguageEntity, text: String): String {
            Log.d("dict", "dict: ${language.dictionary.fullDict}")
            Log.d("rules", "rules: ${language.grammar.grammarRules}")
            val letters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            var curWord = ""
            var prevWord1 = ""
            var prevTranslation1 = ""
            var prevDelimiter1 = ""
            var prevWord2 = ""
            var prevTranslation2 = ""
            var prevDelimiter2 = ""
            var curDelimiter = ""
            var res = ""
            Log.d("text", "text: $text, letters: $letters")
            synchronized(language) {
                for (letter in text) {
                    if (letter == ' ') {
                        Log.d(
                            "meow1", "$curWord;$prevWord1," +
                                    "$prevTranslation1;$prevWord2,$prevTranslation2"
                        )
                        var translatedWord: String
                        if (curWord != "") {
                            // all three words are collocation
                            var collocation = "$prevWord1 $prevWord2 $curWord"
                            var translation = if (prevWord2 == "" || prevWord1 == "") "" else {
                                try {
                                    translateWordToConlang(language, collocation)
                                } catch (e: WordNotFoundException) {
                                    ""
                                }
                            }
                            Log.d("col3", "$collocation $translation")
                            if (translation.isNotEmpty()) {
                                if (prevWord1.isNotEmpty() && prevWord1[0].isUpperCase()) translation =
                                    capitalizeWord(translation)
                                var translatedDelimiter = ""
                                translatedDelimiter =
                                    if (language.puncSymbols.containsKey(curDelimiter)) {
                                        language.puncSymbols[curDelimiter]!!
                                    } else curDelimiter
                                res += "$translation$translatedDelimiter "
                                Log.d("space", "322")
                                prevWord1 = ""
                                prevTranslation1 = ""
                                prevDelimiter1 = ""
                                prevWord2 = ""
                                prevTranslation2 = ""
                                prevDelimiter2 = ""
                            }

                            // two words are collocation
                            if (translation.isEmpty()) {
                                collocation = "$prevWord2 $curWord"
                                translation = if (prevWord2 == "") "" else try {
                                    translateWordToConlang(language, collocation)
                                } catch (e: WordNotFoundException) {
                                    ""
                                }
                                Log.d("col2", "$collocation $translation")
                                if (translation.isNotEmpty()) {
                                    if (prevWord2.isNotEmpty() && prevWord2[0].isUpperCase()) translation =
                                        capitalizeWord(translation)
                                    if (prevWord1 != "") {
                                        val translatedDelimiter1 =
                                            if (language.puncSymbols.containsKey(prevDelimiter1)) {
                                                language.puncSymbols[prevDelimiter1]!!
                                            } else prevDelimiter1
                                        res += "$prevTranslation1$translatedDelimiter1 "
                                    }
                                    val translatedDelimiter =
                                        if (language.puncSymbols.containsKey(curDelimiter)) {
                                            language.puncSymbols[curDelimiter]!!
                                        } else curDelimiter
                                    res += "$translation$translatedDelimiter "
                                    prevWord1 = ""
                                    prevTranslation1 = ""
                                    prevDelimiter1 = ""
                                    prevWord2 = ""
                                    prevTranslation2 = ""
                                    prevDelimiter2 = ""
                                }
                            }

                            if (translation.isEmpty()) {
                                // no collocation, just one word
                                translatedWord = try {
                                    Log.d("word", "word: $curWord")
                                    translatedWord = translateWordToConlang(language, curWord)
                                    if (curWord[0].isUpperCase()) translatedWord =
                                        capitalizeWord(translatedWord)
                                    translatedWord
                                } catch (e: WordNotFoundException) {
                                    curWord
                                }
                                Log.d("col1", "$curWord $translatedWord")
                                if (prevWord1 != "") {
                                    res += prevTranslation1
                                    val translatedDelimiter1 =
                                        if (language.puncSymbols.containsKey(prevDelimiter1)) {
                                            language.puncSymbols[prevDelimiter1]!!
                                        } else prevDelimiter1
                                    res += "$translatedDelimiter1 "
                                }

                                prevWord1 = prevWord2
                                prevTranslation1 = prevTranslation2
                                prevDelimiter1 = prevDelimiter2
                                prevWord2 = curWord
                                prevTranslation2 = translatedWord
                                prevDelimiter2 = curDelimiter
                            }

                            curWord = ""
                            curDelimiter = ""
                        }

                        Log.d(
                            "meow2",
                            "$curWord;$prevWord1,$prevTranslation1;$prevWord2,$prevTranslation2"
                        )
                        Log.d("cur res", res)
                        continue
                    }

                    if (!letters.contains(letter.lowercaseChar())) {
                        curDelimiter += letter
                        continue
                    }

                    curWord += letter
                }


                var translatedWord: String
                if (curWord != "") {
                    // all three words are collocation
                    var collocation = "$prevWord1 $prevWord2 $curWord"
                    var translation = if (prevWord1 == "" || prevWord2 == "") "" else try {
                        translateWordToConlang(language, collocation)
                    } catch (e: WordNotFoundException) {
                        ""
                    }
                    if (translation.isNotEmpty()) {
                        if (prevWord1.isNotEmpty() && prevWord1[0].isUpperCase()) translation =
                            capitalizeWord(translation)
                        val translatedDelimiter =
                            if (language.puncSymbols.containsKey(curDelimiter)) {
                                language.puncSymbols[curDelimiter]!!
                            } else curDelimiter
                        res += "$translation$translatedDelimiter "
                        prevWord1 = ""
                        prevTranslation1 = ""
                        prevDelimiter1 = ""
                        prevWord2 = ""
                        prevTranslation2 = ""
                        prevDelimiter2 = ""
                    }

                    // two words are collocation
                    if (translation.isEmpty()) {
                        collocation = "$prevWord2 $curWord"
                        translation = if (prevWord2 == "") "" else try {
                            translateWordToConlang(language, collocation)
                        } catch (e: WordNotFoundException) {
                            ""
                        }
                        if (translation.isNotEmpty()) {
                            if (prevWord2.isNotEmpty() && prevWord2[0].isUpperCase()) translation =
                                capitalizeWord(translation)
                            if (prevWord1 != "") {
                                val translatedDelimiter1 =
                                    if (language.puncSymbols.containsKey(prevDelimiter1)) {
                                        language.puncSymbols[prevDelimiter1]!!
                                    } else prevDelimiter1
                                res += "$prevTranslation1$translatedDelimiter1 "
                            }
                            val translatedDelimiter =
                                if (language.puncSymbols.containsKey(curDelimiter)) {
                                    language.puncSymbols[curDelimiter]!!
                                } else curDelimiter
                            res += "$translation$translatedDelimiter "
                            prevWord1 = ""
                            prevTranslation1 = ""
                            prevDelimiter1 = ""
                            prevWord2 = ""
                            prevTranslation2 = ""
                            prevDelimiter2 = ""
                        }
                    }

                    if (translation.isEmpty()) {
                        // no collocation, just one word
                        translatedWord = try {
                            Log.d("word", "word: $curWord")
                            translatedWord = translateWordToConlang(language, curWord)
                            if (curWord[0].isUpperCase()) translatedWord =
                                capitalizeWord(translatedWord)
                            translatedWord
                        } catch (e: WordNotFoundException) {
                            curWord
                        }

                        if (prevWord1 != "") {
                            res += prevTranslation1
                            val translatedDelimiter1 =
                                if (language.puncSymbols.containsKey(prevDelimiter1)) {
                                    language.puncSymbols[prevDelimiter1]!!
                                } else prevDelimiter1
                            res += "$translatedDelimiter1 "
                        }

                        prevWord1 = prevWord2
                        prevTranslation1 = prevTranslation2
                        prevDelimiter1 = prevDelimiter2
                        prevWord2 = curWord
                        prevTranslation2 = translatedWord
                        prevDelimiter2 = curDelimiter
                    }

                    curWord = ""
                    curDelimiter = ""
                }

                if (prevWord1 != "") {
                    res += prevTranslation1
                    val translatedDelimiter1 =
                        if (language.puncSymbols.containsKey(prevDelimiter1)) {
                            language.puncSymbols[prevDelimiter1]!!
                        } else prevDelimiter1
                    res += "$translatedDelimiter1 "
                }

                if (prevWord2 != "") {
                    res += prevTranslation2
                    val translatedDelimiter2 =
                        if (language.puncSymbols.containsKey(prevDelimiter2)) {
                            language.puncSymbols[prevDelimiter2]!!
                        } else prevDelimiter2
                    res += translatedDelimiter2
                }

                Log.d("translated", "translated: $res")
                return res
            }
        }
    }
}