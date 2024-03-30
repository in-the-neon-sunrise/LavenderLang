package com.lavenderlang.backend.entity.language

import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.entity.word.IWordEntity
import java.util.SortedSet

data class GrammarEntity(
    val languageId : Int = 0,
    var base : Int = 10,
    //var adposition : Adposition = Adposition.PREPOSITION, // если будет время и желание потом

    var grammarRules: SortedSet<GrammarRuleEntity> = sortedSetOf(
        GrammarRuleEntity(languageId, MascEntity(), mutableMapOf(), TransformationEntity())
    ),
    var wordFormationRules : SortedSet<WordFormationRuleEntity> = sortedSetOf(
        WordFormationRuleEntity(languageId, MascEntity(), mutableMapOf(), TransformationEntity(), "Слово не изменяется")
    ),

    var nextIds : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.GENDER to 3,
        Attributes.NUMBER to 2,
        Attributes.CASE to 6,
        Attributes.TIME to 3,
        Attributes.PERSON to 3,
        Attributes.MOOD to 3,
        Attributes.TYPE to 2,
        Attributes.VOICE to 2,
        Attributes.DEGREEOFCOMPARISON to 3),

    var varsGender : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.GENDER, "мужской", 0, true),
        1 to Characteristic(languageId, 1, Attributes.GENDER, "женский", 1),
        2 to Characteristic(languageId, 2, Attributes.GENDER, "средний", 2)
    ),
    var varsNumber : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.NUMBER, "единственное", 0, true),
        1 to Characteristic(languageId, 1, Attributes.NUMBER, "множественное", 1)
    ),
    var varsCase : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.CASE, "именительный", 0, true),
        1 to Characteristic(languageId, 1, Attributes.CASE, "родительный", 1),
        2 to Characteristic(languageId, 2, Attributes.CASE, "дательный", 2),
        3 to Characteristic(languageId, 3, Attributes.CASE, "винительный", 3),
        4 to Characteristic(languageId, 4, Attributes.CASE, "творительный", 4),
        5 to Characteristic(languageId, 5, Attributes.CASE, "предложеный", 5)
    ),
    var varsTime : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.TIME, "настоящее", 0, true),
        1 to Characteristic(languageId, 1, Attributes.TIME, "прошедшее", 1),
        2 to Characteristic(languageId, 2, Attributes.TIME, "будущее", 2)
    ),
    var varsPerson : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.PERSON, "первое", 0, true),
        1 to Characteristic(languageId, 1, Attributes.PERSON, "второе", 1),
        2 to Characteristic(languageId, 2, Attributes.PERSON, "третье", 2)
    ),
    var varsMood : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.MOOD, "изъявительное", 0, true),
        1 to Characteristic(languageId, 1, Attributes.MOOD, "повелительное", 1),
        2 to Characteristic(languageId, 2, Attributes.MOOD, "условное", 2)
    ),
    var varsType : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.TYPE, "совершенный", 0, true),
        1 to Characteristic(languageId, 1, Attributes.TYPE, "несовершенный", 1)
    ),
    var varsVoice : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.VOICE, "действительный", 0, true),
        1 to Characteristic(languageId, 1, Attributes.VOICE, "страдательный", 1)
    ),
    var varsDegreeOfComparison : MutableMap<Int, Characteristic> = mutableMapOf(
        0 to Characteristic(languageId, 0, Attributes.DEGREEOFCOMPARISON, "положительная", 0, true),
        1 to Characteristic(languageId, 1, Attributes.DEGREEOFCOMPARISON, "сравнительная", 1),
        2 to Characteristic(languageId, 2, Attributes.DEGREEOFCOMPARISON, "превосходная", 2)
    ),
)