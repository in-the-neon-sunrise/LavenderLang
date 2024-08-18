package com.lavenderlang.domain

import com.lavenderlang.domain.exception.WordNotFoundException
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.rule.IRuleEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.ui.MyApp
import kotlin.collections.ArrayList

val rusGender : ArrayList<String> = arrayListOf("мужской", "женский", "средний")
val rusNumber : ArrayList<String> = arrayListOf("единственное", "множественное")
var rusCase : ArrayList<String> = arrayListOf("именительный", "родительный", "дательный", "винительный",
    "творительный", "предложный")
val rusTime : ArrayList<String> = arrayListOf("настоящее", "прошедшее", "будущее")
val rusPerson : ArrayList<String> = arrayListOf("первое", "второе", "третье")
val rusMood : ArrayList<String> = arrayListOf("изъявительное", "повелительное")
val rusType : ArrayList<String> = arrayListOf("совершенный", "несовершенный")
val rusVoice : ArrayList<String> = arrayListOf("действительный", "страдательный")
val rusDegreeOfComparison : ArrayList<String> = arrayListOf("положительная", "сравнительная", "превосходная")

fun getImmutableAttrsInfo(word: IWordEntity): String {
    var res = ""
    for (attr in word.immutableAttrs.keys) {
        res += when (attr) {
            Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[word.immutableAttrs[attr]!!]?.name}, "
            Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[word.immutableAttrs[attr]!!]?.name}, "
            Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[word.immutableAttrs[attr]!!]?.name}, "
            else -> ""
        }
    }
    if (res.length < 2) return ""
    return res.slice(0 until res.length - 2)
}

fun conlangToRusAttr(language: LanguageEntity, attr: Attributes, id: Int): Int {
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

fun capitalizeWord(word: String): String {
    return word[0].uppercaseChar() + word.substring(1)
}

fun transformWord(transformation: TransformationEntity, word: String): String {
    return transformation.addToBeginning + word.slice(
        IntRange(transformation.delFromBeginning,
            word.length - transformation.delFromEnd - 1)) +
            transformation.addToEnd
}

fun getOrigInfo(rule: GrammarRuleEntity): String {
    var res = when (rule.masc.partOfSpeech) {
        PartOfSpeech.NOUN -> "существительное; "
        PartOfSpeech.VERB -> "глагол; "
        PartOfSpeech.ADJECTIVE -> "прилагательное; "
        PartOfSpeech.ADVERB -> "наречие; "
        PartOfSpeech.PARTICIPLE -> "причастие; "
        PartOfSpeech.VERB_PARTICIPLE -> "деепричастие; "
        PartOfSpeech.PRONOUN -> "местоимение; "
        PartOfSpeech.NUMERAL -> "числительное; "
        PartOfSpeech.FUNC_PART -> "служебное слово; "
    }
    for (attr in rule.masc.immutableAttrs.keys) {
        res += when (attr) {
            Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.masc.immutableAttrs[attr]]!!.name}, "
            Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.masc.immutableAttrs[attr]]!!.name}, "
            Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.masc.immutableAttrs[attr]]!!.name}, "
            else -> continue
        }
    }
    return res.slice(0 until res.length - 2)
}

fun getResultInfo(rule: GrammarRuleEntity): String {
    var res = ""
    for (attr in rule.mutableAttrs.keys) {
        res += when (attr) {
            Attributes.CASE -> "падеж: ${MyApp.language!!.grammar.varsCase[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.NUMBER -> "число: ${MyApp.language!!.grammar.varsNumber[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.DEGREE_OF_COMPARISON -> "степень сравнения: ${MyApp.language!!.grammar.varsDegreeOfComparison[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.TIME -> "время: ${MyApp.language!!.grammar.varsTime[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.MOOD -> "наклонение: ${MyApp.language!!.grammar.varsMood[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.PERSON -> "лицо: ${MyApp.language!!.grammar.varsPerson[rule.mutableAttrs[attr]!!]?.name}, "
            Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.mutableAttrs[attr]!!]?.name}, "
            else -> ""
        }
    }
    if (res.length < 2) return res
    return res.slice(0 until res.length - 2)
}

fun getOrigInfo(rule: WordFormationRuleEntity): String {
    var res = when (rule.masc.partOfSpeech) {
        PartOfSpeech.NOUN -> "существительное; "
        PartOfSpeech.VERB -> "глагол; "
        PartOfSpeech.ADJECTIVE -> "прилагательное; "
        PartOfSpeech.ADVERB -> "наречие; "
        PartOfSpeech.PARTICIPLE -> "причастие; "
        PartOfSpeech.VERB_PARTICIPLE -> "деепричастие; "
        PartOfSpeech.PRONOUN -> "местоимение; "
        PartOfSpeech.NUMERAL -> "числительное; "
        PartOfSpeech.FUNC_PART -> "служебное слово; "
    }
    for (attr in rule.masc.immutableAttrs.keys) {
        res += when (attr) {
            Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.masc.immutableAttrs[attr]]!!.name}, "
            Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.masc.immutableAttrs[attr]]!!.name}, "
            Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.masc.immutableAttrs[attr]]!!.name}, "
            else -> continue
        }
    }
    return res.slice(0 until res.length - 2)
}

fun getResultInfo(rule: WordFormationRuleEntity): String {
    var res = when (rule.partOfSpeech) {
        PartOfSpeech.NOUN -> "существительное; "
        PartOfSpeech.VERB -> "глагол; "
        PartOfSpeech.ADJECTIVE -> "прилагательное; "
        PartOfSpeech.ADVERB -> "наречие; "
        PartOfSpeech.PARTICIPLE -> "причастие; "
        PartOfSpeech.VERB_PARTICIPLE -> "деепричастие; "
        PartOfSpeech.PRONOUN -> "местоимение; "
        PartOfSpeech.NUMERAL -> "числительное; "
        PartOfSpeech.FUNC_PART -> "служебное слово; "
    }
    for (attr in rule.immutableAttrs.keys) {
        res += when (attr) {
            Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.immutableAttrs[attr]!!]?.name}, "
            Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.immutableAttrs[attr]!!]?.name}, "
            Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.immutableAttrs[attr]!!]?.name}, "
            else -> ""
        }
    }
    if (res.length < 2) return res
    return res.slice(0 until res.length - 2)
}