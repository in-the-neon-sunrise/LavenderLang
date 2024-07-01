package com.lavenderlang.backend.dao.language

import com.chaquo.python.Python
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.exception.WordNotFoundException

interface TranslatorHelperDao {
    fun conlangToRusAttr(language: LanguageEntity, attr: Attributes, id: Int) : Int
    fun translateWordFromConlang(language: LanguageEntity, word: String) : String
    fun translateWordToConlang(language: LanguageEntity, word: String): String
    fun capitalizeWord(word: String) : String
}

class TranslatorHelperDaoImpl : TranslatorHelperDao {

    override fun conlangToRusAttr(language: LanguageEntity, attr: Attributes, id: Int): Int {
        return try {
            when (attr) {
                Attributes.GENDER -> language.grammar.varsGender[id]!!.russianId
                Attributes.NUMBER -> language.grammar.varsNumber[id]!!.russianId
                Attributes.CASE -> language.grammar.varsCase[id]!!.russianId
                Attributes.TIME -> language.grammar.varsTime[id]!!.russianId
                Attributes.PERSON -> language.grammar.varsPerson[id]!!.russianId
                Attributes.MOOD -> language.grammar.varsMood[id]!!.russianId
                Attributes.TYPE -> language.grammar.varsType[id]!!.russianId
                Attributes.VOICE -> language.grammar.varsVoice[id]!!.russianId
                Attributes.DEGREE_OF_COMPARISON -> language.grammar.varsDegreeOfComparison[id]!!.russianId
                Attributes.IS_INFINITIVE -> id
            }
        } catch (e: Exception) {
            0
        }
    }

    override fun capitalizeWord(word: String): String {
        return word[0].uppercaseChar() + word.substring(1)
    }

    override fun translateWordFromConlang(language: LanguageEntity, word: String): String {
        for (key in language.dictionary.fullDict.keys) {
            for (w in language.dictionary.fullDict[key]!!) {
                if (w.word.lowercase() == word.lowercase()) {
                    return w.translation
                }
            }
        }
        throw WordNotFoundException("Word not found")
    }
    override fun translateWordToConlang(language: LanguageEntity, word: String): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        val normalForm = module.callAttr("getNormalForm", word.lowercase()).toString()
        for (key in language.dictionary.fullDict.keys) {
            val keyWord = key.split(":")[0]
            val keyTranslation = key.split(":")[1]
            if (keyTranslation != normalForm) continue
            for (w in language.dictionary.fullDict[key]!!) {
                if (w.translation.lowercase() == word.lowercase()) {
                    if (language.capitalizedPartsOfSpeech.contains(w.partOfSpeech)) {
                        return capitalizeWord(w.word)
                    }
                    return w.word
                }
            }
            if (language.dictionary.fullDict[key]!!.isEmpty()) {
                return keyWord
            }
            if (language.capitalizedPartsOfSpeech.contains(
                    language.dictionary.fullDict[key]!![0].partOfSpeech)
                ) {
                return capitalizeWord(keyWord)
            }
            return keyWord
        }
        throw WordNotFoundException("Word not found")
    }
}