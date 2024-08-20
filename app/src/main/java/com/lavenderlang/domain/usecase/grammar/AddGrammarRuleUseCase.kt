package com.lavenderlang.domain.usecase.grammar

import android.util.Log
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.fits
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity

class AddGrammarRuleUseCase {
    companion object {
        fun execute(grammarRule: GrammarRuleEntity, language: LanguageEntity, py: PythonHandler) {
            if (!language.grammar.grammarRules.contains(grammarRule))
                language.grammar.grammarRules.add(grammarRule)

            val dictionary = language.dictionary
            val copyDict = HashMap(dictionary.fullDict)
            Log.d("AddGrammarRuleUseCase", "full ${language.dictionary.fullDict}")
            synchronized(dictionary) {
                for (key in copyDict.keys) {
                    if (dictionary.fullDict[key]!!.isEmpty()) continue
                    val keyWord = dictionary.fullDict[key]!![0]
                    if (fits(grammarRule.masc, keyWord)) {
                        dictionary.fullDict[key]!!.add(
                            GrammarTransformByRuleUseCase.execute(grammarRule, keyWord, language, py)
                        )
                    }
                }
            }
            Log.d("AddGrammarRuleUseCase", "new full ${language.dictionary.fullDict}")
        }
    }
}