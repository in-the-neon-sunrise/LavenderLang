package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity

class DeleteWordFormationRuleUseCase {
    companion object {
        fun execute(
            wordFormationRule: WordFormationRuleEntity, grammar: GrammarEntity) {
            grammar.wordFormationRules.remove(wordFormationRule)
        }
    }
}