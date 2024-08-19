package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity

class AddWordFormationRuleUseCase {
    companion object {
        fun execute(wordFormationRule: WordFormationRuleEntity, grammar: GrammarEntity) {
            if (!grammar.wordFormationRules.contains(wordFormationRule))
                grammar.wordFormationRules.add(wordFormationRule)
        }
    }
}