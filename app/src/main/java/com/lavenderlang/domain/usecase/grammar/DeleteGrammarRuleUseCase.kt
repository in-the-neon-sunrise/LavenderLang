package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.fits
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity

class DeleteGrammarRuleUseCase {
    companion object {
        fun execute(rule: GrammarRuleEntity, language: LanguageEntity) {
            val grammar = language.grammar
            val dictionary = language.dictionary

            grammar.grammarRules.remove(rule)

            val copyDict = HashMap(dictionary.fullDict)
            synchronized(dictionary) {
                for (key in copyDict.keys) {
                    if (dictionary.fullDict[key]!!.size < 2) continue
                    val keyWord = dictionary.fullDict[key]!![0]
                    if (!fits(rule.masc, keyWord)) continue
                    val copyOfWords = ArrayList(dictionary.fullDict[key]!!.subList(1, dictionary.fullDict[key]!!.size))
                    for (word in copyOfWords) {
                        var check = true
                        for (attr in rule.mutableAttrs.keys) {
                            if (!word.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != word.mutableAttrs[attr]) {
                                check = false
                                break
                            }
                        }
                        if (!check) continue
                        dictionary.fullDict[key]!!.remove(word)
                    }
                }
            }
        }
    }
}