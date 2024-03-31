package com.lavenderlang.backend.dao.language

import com.chaquo.python.Python
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.Characteristic
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.service.ResultAttrs
import com.lavenderlang.backend.service.WordNotFoundException
import com.lavenderlang.languages
import com.lavenderlang.serializer

interface TranslatorDao {
    fun rusToConlangAttr(language: LanguageEntity, attr: Attributes, id: Int) : Int
    fun conlangToRusAttr(language: LanguageEntity, attr: Attributes, id: Int) : Int
    fun translateWordFromConlang(language: LanguageEntity, word: IWordEntity) : String;
    fun translateTextFromConlang(language: LanguageEntity, text: String) : String;
    fun immutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int>;
    fun mutableAttrsToNormalForm(attrs: ResultAttrs) : MutableMap<Attributes, Int>;
    fun translateWordToConlang(language: LanguageEntity, wrappedAttrs: String) : String;
    fun translateTextToConlang(language: LanguageEntity, text: String) : String
}

class TranslatorDaoImpl: TranslatorDao {
    //problem with multiple links to one russian attribute: we will find only first mention
    override fun rusToConlangAttr(language: LanguageEntity, attr: Attributes, id: Int): Int {
        val vars : MutableMap<Int, Characteristic> = when (attr) {
            Attributes.GENDER -> language.grammar.varsGender
            Attributes.NUMBER -> language.grammar.varsNumber
            Attributes.CASE -> language.grammar.varsCase
            Attributes.TIME -> language.grammar.varsTime
            Attributes.PERSON -> language.grammar.varsPerson
            Attributes.MOOD -> language.grammar.varsMood
            Attributes.TYPE -> language.grammar.varsType
            Attributes.VOICE -> language.grammar.varsVoice
            Attributes.DEGREEOFCOMPARISON -> language.grammar.varsDegreeOfComparison
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
            }
        } catch (e: Exception) {
            0
        }
    }

    override fun translateWordFromConlang(language: LanguageEntity, word: IWordEntity): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        val russianMutableAttrs: ArrayList<Int> = arrayListOf()
        for (attr in word.mutableAttrs.keys) {
            russianMutableAttrs.add(conlangToRusAttr(language, attr, word.mutableAttrs[attr]!!))
        }
        return module.callAttr(
            "inflectAttrs", word.translation,
            word.partOfSpeech.toString(),
            russianMutableAttrs.toString()
        ).toString()
    }
    override fun translateTextFromConlang(language: LanguageEntity, text: String): String {
        val words = text.split(" ")
        var res = ""
        for (word in words) {
            var check = false
            for (key in language.dictionary.fullDict.keys) {
                if (check) break
                for (w in language.dictionary.fullDict[key]!!) {
                    if (word == w.word) {
                        res += "${translateWordFromConlang(language, w)} "
                        check = true
                        break
                    }
                }
            }
            if (!check) res += "$word "
        }
        return res
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
                Attributes.MOOD to attrs.mutableAttrs[4])
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

    override fun translateWordToConlang(language: LanguageEntity, wrappedAttrs: String): String {
        val m = ObjectMapper()
        val attrs = m.readValue(wrappedAttrs, ResultAttrs::class.java)
        val rusMutAttrs = mutableAttrsToNormalForm(attrs)
        val rusImmutAttrs = immutableAttrsToNormalForm(attrs)
        val mutAttrs = mutableMapOf<Attributes, Int>()
        val immutAttrs = mutableMapOf<Attributes, Int>()
        for (attr in rusMutAttrs.keys) {
            mutAttrs[attr] = rusToConlangAttr(language, attr, rusMutAttrs[attr]!!)
        }
        for (attr in rusImmutAttrs.keys) {
            immutAttrs[attr] = rusToConlangAttr(language, attr, rusImmutAttrs[attr]!!)
        }

        var res: String
        for (key in language.dictionary.fullDict.keys) {
            if (key.split(":")[1] != attrs.inf) continue
            res = key.split(":")[0]
            for (word in language.dictionary.fullDict[key]!!) {
                if (word.mutableAttrs == mutAttrs) {
                    res = word.word
                    break
                }
            }
            return res
        }
        throw WordNotFoundException("Word not found")
    }

    override fun translateTextToConlang(language: LanguageEntity, text: String): String {
        val words = text.split(" ")
        var res = ""
        val py = Python.getInstance()
        val module = py.getModule("pm3")

        for (word in words) {
            val attrs = module.callAttr("getWrappedAttrs", word).toString()
            res += try {
                " ${translateWordToConlang(language, attrs)}"
            } catch (e: WordNotFoundException) {
                " $word"
            }
        }
        return res
    }

}