package com.lavenderlang.domain.model.language


import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity

data class GrammarEntity(
    var languageId : Int = 0,
    var grammarRules: ArrayList<GrammarRuleEntity> = arrayListOf(),
    var wordFormationRules : ArrayList<WordFormationRuleEntity> = arrayListOf(),

    var nextIds : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.GENDER to 3,
        Attributes.NUMBER to 2,
        Attributes.CASE to 6,
        Attributes.TIME to 3,
        Attributes.PERSON to 3,
        Attributes.MOOD to 3,
        Attributes.TYPE to 2,
        Attributes.VOICE to 2,
        Attributes.DEGREE_OF_COMPARISON to 3),

    var varsGender : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.GENDER, "мужской", 0),
        1 to CharacteristicEntity(1, Attributes.GENDER, "женский", 1),
        2 to CharacteristicEntity(2, Attributes.GENDER, "средний", 2)
    ),
    var varsNumber : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.NUMBER, "единственное", 0),
        1 to CharacteristicEntity(1, Attributes.NUMBER, "множественное", 1)
    ),
    var varsCase : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.CASE, "именительный", 0),
        1 to CharacteristicEntity(1, Attributes.CASE, "родительный", 1),
        2 to CharacteristicEntity(2, Attributes.CASE, "дательный", 2),
        3 to CharacteristicEntity(3, Attributes.CASE, "винительный", 3),
        4 to CharacteristicEntity(4, Attributes.CASE, "творительный", 4),
        5 to CharacteristicEntity(5, Attributes.CASE, "предложеный", 5)
    ),
    var varsTime : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.TIME, "настоящее", 0),
        1 to CharacteristicEntity(1, Attributes.TIME, "прошедшее", 1),
        2 to CharacteristicEntity(2, Attributes.TIME, "будущее", 2)
    ),
    var varsPerson : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.PERSON, "первое", 0),
        1 to CharacteristicEntity(1, Attributes.PERSON, "второе", 1),
        2 to CharacteristicEntity(2, Attributes.PERSON, "третье", 2)
    ),
    var varsMood : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.MOOD, "изъявительное", 0),
        1 to CharacteristicEntity(1, Attributes.MOOD, "повелительное", 1)
    ),
    var varsType : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.TYPE, "совершенный", 0),
        1 to CharacteristicEntity(1, Attributes.TYPE, "несовершенный", 1)
    ),
    var varsVoice : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.VOICE, "действительный", 0),
        1 to CharacteristicEntity(1, Attributes.VOICE, "страдательный", 1)
    ),
    var varsDegreeOfComparison : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.DEGREE_OF_COMPARISON, "положительная", 0),
        1 to CharacteristicEntity(1, Attributes.DEGREE_OF_COMPARISON, "сравнительная", 1),
        2 to CharacteristicEntity(2, Attributes.DEGREE_OF_COMPARISON, "превосходная", 2)
    ),
    // not shown to the user
    var varsIsInfinitive : MutableMap<Int, CharacteristicEntity> = mutableMapOf(
        0 to CharacteristicEntity(0, Attributes.IS_INFINITIVE, "инфинитив", 0),
        1 to CharacteristicEntity(1, Attributes.IS_INFINITIVE, "не инфинитив", 1)
    )
)