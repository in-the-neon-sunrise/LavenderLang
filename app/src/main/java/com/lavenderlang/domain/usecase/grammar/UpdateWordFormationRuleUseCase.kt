package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity

class UpdateWordFormationRuleUseCase {
    companion object {
        suspend fun execute(rule: WordFormationRuleEntity, masc: MascEntity, transformation: TransformationEntity,
                            description: String, newAttrs: MutableMap<Attributes, Int>, partOfSpeech: PartOfSpeech,
                            grammar: GrammarEntity, repo: LanguageRepository
        ) {
            rule.masc = masc
            rule.transformation = transformation
            rule.description = description
            rule.immutableAttrs = newAttrs
            rule.partOfSpeech = partOfSpeech
            repo.updateGrammar(grammar.languageId, Serializer.getInstance().serializeGrammar(grammar))
        }
    }
}