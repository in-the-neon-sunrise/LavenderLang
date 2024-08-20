package com.lavenderlang.domain.usecase.update

import com.lavenderlang.domain.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.GrammarEntity

class UpdateGrammarUseCase {
    companion object {
        suspend fun execute(grammar: GrammarEntity, repo: LanguageRepository) {
            repo.updateGrammar(grammar.languageId, Serializer.getInstance().serializeGrammar(grammar))
        }
    }
}