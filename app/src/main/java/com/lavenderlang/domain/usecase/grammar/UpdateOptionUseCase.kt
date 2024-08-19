package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.language.GrammarEntity

class UpdateOptionUseCase {
    companion object {
        fun execute(
            grammar: GrammarEntity, optionId: Int, newOption: CharacteristicEntity) {
            val map: MutableMap<Int, CharacteristicEntity> = when (newOption.type) {
                Attributes.GENDER -> grammar.varsGender
                Attributes.NUMBER -> grammar.varsNumber
                Attributes.CASE -> grammar.varsCase
                Attributes.TIME -> grammar.varsTime
                Attributes.PERSON -> grammar.varsPerson
                Attributes.MOOD -> grammar.varsMood
                Attributes.TYPE -> grammar.varsType
                Attributes.VOICE -> grammar.varsVoice
                Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison
                Attributes.IS_INFINITIVE -> return
            }
            if (!map.contains(optionId)) return
            map[optionId] = newOption
        }
    }
}