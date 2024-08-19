package com.lavenderlang.domain.usecase.grammar

import android.util.Log
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity

class AddGrammarRuleUseCase {
    companion object {
        fun execute(grammarRule: GrammarRuleEntity, language: LanguageEntity, py: PythonHandler) {
            if (!language.grammar.grammarRules.contains(grammarRule))
                language.grammar.grammarRules.add(grammarRule)

            val mascHandler = MascDaoImpl()
            val dictionary = language.dictionary
            val copyDict = HashMap(dictionary.fullDict)
            Log.d("AddGrammarRuleUseCase", "full ${language.dictionary.fullDict}")
            synchronized(dictionary) {
                for (key in copyDict.keys) {
                    if (dictionary.fullDict[key]!!.isEmpty()) continue
                    val keyWord = dictionary.fullDict[key]!![0]
                    if (mascHandler.fits(grammarRule.masc, keyWord)) {
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