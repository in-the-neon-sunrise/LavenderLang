package com.lavenderlang.domain.usecase.dictionary

import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.usecase.grammar.WordFormationTransformByRuleUseCase

class CreateWordsFromExistingUseCase {
    companion object {
        fun execute(word: IWordEntity, wordFormationRules: ArrayList<WordFormationRuleEntity>): ArrayList<Pair<String, IWordEntity>> {
            val possibleWords: ArrayList<Pair<String, IWordEntity>> = arrayListOf()
            val mascHandler = MascDaoImpl()
            for (rule in wordFormationRules) {
                if (!mascHandler.fits(rule.masc, word)) continue
                val posWord = WordFormationTransformByRuleUseCase.execute(word, rule)
                possibleWords.add(Pair(rule.description, posWord))
            }
            return possibleWords
        }
    }
}