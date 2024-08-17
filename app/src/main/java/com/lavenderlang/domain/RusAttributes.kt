package com.lavenderlang.domain

import com.lavenderlang.domain.model.help.Attributes
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