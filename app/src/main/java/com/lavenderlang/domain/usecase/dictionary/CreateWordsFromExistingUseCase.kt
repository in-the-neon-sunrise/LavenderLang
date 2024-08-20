package com.lavenderlang.domain.usecase.dictionary

import com.lavenderlang.domain.fits
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.usecase.grammar.WordFormationTransformByRuleUseCase

class CreateWordsFromExistingUseCase {
    companion object {
        fun execute(word: IWordEntity, wordFormationRules: ArrayList<WordFormationRuleEntity>): ArrayList<Pair<String, IWordEntity>> {
            val possibleWords: ArrayList<Pair<String, IWordEntity>> = arrayListOf()
            for (rule in wordFormationRules) {
                if (!fits(rule.masc, word)) continue
                val posWord = WordFormationTransformByRuleUseCase.execute(word, rule)
                possibleWords.add(Pair(rule.description, posWord))
            }
            return possibleWords
        }
    }
}