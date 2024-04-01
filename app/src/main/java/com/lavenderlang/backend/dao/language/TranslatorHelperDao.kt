package com.lavenderlang.backend.dao.language

import android.util.Log
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.service.ResultAttrs
import com.lavenderlang.backend.service.WordNotFoundException

interface TranslatorHelperDao {
    fun rusToConlangAttr(language: LanguageEntity, attr: Attributes, id: Int) : Int
    fun conlangToRusAttr(language: LanguageEntity, attr: Attributes, id: Int) : Int
    fun translateWordFromConlang(language: LanguageEntity, word: String) : String
    fun translateWordToConlang(language: LanguageEntity, word: String): String
    fun immutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int>
    fun mutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int>
    fun capitalizeWord(word: String) : String {
        return word[0].uppercaseChar() + word.substring(1)
    }
}

class TranslatorHelperDaoImpl : TranslatorHelperDao {
    //problem with multiple links to one russian attribute: we will find only first mention
    override fun rusToConlangAttr(language: LanguageEntity, attr: Attributes, id: Int): Int {
        val vars : MutableMap<Int, CharacteristicEntity> = when (attr) {
            Attributes.GENDER -> language.grammar.varsGender
            Attributes.NUMBER -> language.grammar.varsNumber
            Attributes.CASE -> language.grammar.varsCase
            Attributes.TIME -> language.grammar.varsTime
            Attributes.PERSON -> language.grammar.varsPerson
            Attributes.MOOD -> language.grammar.varsMood
            Attributes.TYPE -> language.grammar.varsType
            Attributes.VOICE -> language.grammar.varsVoice
            Attributes.DEGREEOFCOMPARISON -> language.grammar.varsDegreeOfComparison
            Attributes.ISINFINITIVE -> return id
        }
        for (option in vars.keys) {
            if (vars[option]!!.russianId == id) return vars[option]!!.characteristicId
        }
        return 0
    }

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
                Attributes.DEGREEOFCOMPARISON -> language.grammar.varsDegreeOfComparison[id]!!.russianId
                Attributes.ISINFINITIVE -> id
            }
        } catch (e: Exception) {
            0
        }
    }
    override fun immutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int> {
        val partOfSpeech = attrs.partOfSpeech
        when(partOfSpeech) {
            PartOfSpeech.NOUN -> return mutableMapOf(
                Attributes.GENDER to attrs.immutableAttrs[0])
            PartOfSpeech.VERB -> return mutableMapOf(
                Attributes.TYPE to attrs.immutableAttrs[0],
                Attributes.VOICE to attrs.immutableAttrs[1])
            PartOfSpeech.PARTICIPLE -> return mutableMapOf(
                Attributes.TYPE to attrs.immutableAttrs[0],
                Attributes.VOICE to attrs.immutableAttrs[1])
            PartOfSpeech.VERBPARTICIPLE -> return mutableMapOf(
                Attributes.TYPE to attrs.immutableAttrs[0])
            PartOfSpeech.PRONOUN -> return mutableMapOf(
                Attributes.GENDER to attrs.immutableAttrs[0])

            else -> return mutableMapOf()

        }
    }
    override fun mutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int> {
        val partOfSpeech = attrs.partOfSpeech
        when(partOfSpeech) {
            PartOfSpeech.NOUN -> return mutableMapOf(
                Attributes.NUMBER to attrs.mutableAttrs[0],
                Attributes.CASE to attrs.mutableAttrs[1])
            PartOfSpeech.VERB -> return mutableMapOf(
                Attributes.TIME to attrs.mutableAttrs[0],
                Attributes.NUMBER to attrs.mutableAttrs[1],
                Attributes.GENDER to attrs.mutableAttrs[2],
                Attributes.PERSON to attrs.mutableAttrs[3],
                Attributes.MOOD to attrs.mutableAttrs[4],
                Attributes.ISINFINITIVE to attrs.mutableAttrs[5])
            PartOfSpeech.ADJECTIVE -> return mutableMapOf(
                Attributes.GENDER to attrs.mutableAttrs[0],
                Attributes.NUMBER to attrs.mutableAttrs[1],
                Attributes.CASE to attrs.mutableAttrs[2],
                Attributes.DEGREEOFCOMPARISON to attrs.mutableAttrs[3])
            PartOfSpeech.PARTICIPLE -> return mutableMapOf(
                Attributes.TIME to attrs.mutableAttrs[0],
                Attributes.NUMBER to attrs.mutableAttrs[1],
                Attributes.GENDER to attrs.mutableAttrs[2],
                Attributes.CASE to attrs.mutableAttrs[3])
            PartOfSpeech.PRONOUN -> return mutableMapOf(
                Attributes.NUMBER to attrs.immutableAttrs[0],
                Attributes.CASE to attrs.immutableAttrs[1])
            else -> return mutableMapOf()
        }
    }
    override fun translateWordFromConlang(language: LanguageEntity, word: String): String {
        for (key in language.dictionary.fullDict.keys) {
            for (w in language.dictionary.fullDict[key]!!) {
                if (w.word == word.lowercase()) {
                    return w.translation
                }
            }
        }
        throw WordNotFoundException("Word not found")
    }
    override fun translateWordToConlang(language: LanguageEntity, word: String): String {
        for (key in language.dictionary.fullDict.keys) {
            for (w in language.dictionary.fullDict[key]!!) {
                if (w.translation == word.lowercase()) {
                    val TAG = "woof"
                    Log.d(TAG, w.toString())
                    return w.word
                }
            }
        }
        throw WordNotFoundException("Word not found")
    }
}