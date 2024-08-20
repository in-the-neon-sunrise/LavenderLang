package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity

class UpdateWordFormationRuleUseCase {
    companion object {
        fun execute(rule: WordFormationRuleEntity, masc: MascEntity, transformation: TransformationEntity,
                            description: String, newAttrs: MutableMap<Attributes, Int>, partOfSpeech: PartOfSpeech) {
            rule.masc = masc
            rule.transformation = transformation
            rule.description = description
            rule.immutableAttrs = newAttrs
            rule.partOfSpeech = partOfSpeech
        }
    }
}