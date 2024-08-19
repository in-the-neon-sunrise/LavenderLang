package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.language.GrammarEntity

class AddOptionUseCase {
    companion object {
        fun execute(
            option: CharacteristicEntity, grammar: GrammarEntity) {
            when (option.type) {
                Attributes.GENDER -> grammar.varsGender[grammar.nextIds[option.type]!!] = option
                Attributes.NUMBER -> grammar.varsNumber[grammar.nextIds[option.type]!!] = option
                Attributes.CASE -> grammar.varsCase[grammar.nextIds[option.type]!!] = option
                Attributes.TIME -> grammar.varsTime[grammar.nextIds[option.type]!!] = option
                Attributes.PERSON -> grammar.varsPerson[grammar.nextIds[option.type]!!] = option
                Attributes.MOOD -> grammar.varsMood[grammar.nextIds[option.type]!!] = option
                Attributes.TYPE -> grammar.varsType[grammar.nextIds[option.type]!!] = option
                Attributes.VOICE -> grammar.varsVoice[grammar.nextIds[option.type]!!] = option
                Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison[grammar.nextIds[option.type]!!] =
                    option

                Attributes.IS_INFINITIVE -> return
            }
            grammar.nextIds[option.type] = grammar.nextIds[option.type]!! + 1
        }
    }
}