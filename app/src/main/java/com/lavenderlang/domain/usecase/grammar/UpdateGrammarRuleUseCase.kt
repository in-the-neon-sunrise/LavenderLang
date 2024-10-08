package com.lavenderlang.domain.usecase.grammar

import android.util.Log
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.fits
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity

class UpdateGrammarRuleUseCase {
    companion object {
        fun execute(
            rule: GrammarRuleEntity, masc: MascEntity, transformation: TransformationEntity,
            newAttrs: MutableMap<Attributes, Int>, language: LanguageEntity, py: PythonHandler
        ) {
            Log.d("updateRule", "oldRule: $rule")

            val dictionary = language.dictionary
            var copyDict = HashMap(dictionary.fullDict)

            Log.d("updateRule", "full ${language.dictionary.fullDict}")

            synchronized(dictionary) {
                for (key in copyDict.keys) {
                    if (language.dictionary.fullDict[key]!!.size < 2) continue
                    val keyWord = language.dictionary.fullDict[key]!![0]
                    if (!fits(rule.masc, keyWord)) continue
                    val copyOfWords = ArrayList(
                        dictionary.fullDict[key]!!.subList(
                            1,
                            dictionary.fullDict[key]!!.size
                        )
                    )
                    for (word in copyOfWords) {
                        var check = true
                        for (attr in rule.mutableAttrs.keys) {
                            if (!word.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != word.mutableAttrs[attr]) {
                                check = false
                                break
                            }
                        }
                        for (attr in word.mutableAttrs.keys) {
                            if (!rule.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != word.mutableAttrs[attr]) {
                                check = false
                                break
                            }
                        }
                        if (!check) continue
                        Log.d("updateRule", "remove $word")
                        dictionary.fullDict[key]!!.remove(word)
                    }
                }
            }

            rule.masc = masc
            rule.transformation = transformation
            rule.mutableAttrs = newAttrs

            copyDict = HashMap(dictionary.fullDict)
            synchronized(dictionary) {
                for (key in copyDict.keys) {
                    if (language.dictionary.fullDict[key]!!.isEmpty()) continue
                    val keyWord = language.dictionary.fullDict[key]!![0]
                    if (fits(rule.masc, keyWord)) {
                        dictionary.fullDict[key]!!.add(
                            GrammarTransformByRuleUseCase.execute(
                                rule,
                                keyWord,
                                language,
                                py
                            )
                        )
                    }
                }
            }

            Log.d("updateRule", "new full ${language.dictionary.fullDict}")
        }
    }
}