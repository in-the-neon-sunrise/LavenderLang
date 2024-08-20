package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.domain.model.word.AdjectiveEntity
import com.lavenderlang.domain.model.word.AdverbEntity
import com.lavenderlang.domain.model.word.FuncPartEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.model.word.NounEntity
import com.lavenderlang.domain.model.word.NumeralEntity
import com.lavenderlang.domain.model.word.ParticipleEntity
import com.lavenderlang.domain.model.word.PronounEntity
import com.lavenderlang.domain.model.word.VerbEntity
import com.lavenderlang.domain.model.word.VerbParticipleEntity

class WordFormationTransformByRuleUseCase {
    companion object {
        fun execute(word: IWordEntity, rule: WordFormationRuleEntity): IWordEntity {
            val transformedWord: IWordEntity = when (rule.partOfSpeech) {
                PartOfSpeech.NOUN -> NounEntity()
                PartOfSpeech.VERB -> VerbEntity()
                PartOfSpeech.ADJECTIVE -> AdjectiveEntity()
                PartOfSpeech.ADVERB -> AdverbEntity()
                PartOfSpeech.PARTICIPLE -> ParticipleEntity()
                PartOfSpeech.VERB_PARTICIPLE -> VerbParticipleEntity()
                PartOfSpeech.PRONOUN -> PronounEntity()
                PartOfSpeech.NUMERAL -> NumeralEntity()
                PartOfSpeech.FUNC_PART -> FuncPartEntity()
            }
            val newWord = rule.transformation.addToBeginning + word.word.slice(
                IntRange(rule.transformation.delFromBeginning,
                    word.word.length - rule.transformation.delFromEnd - 1)) +
                    rule.transformation.addToEnd
            transformedWord.word = newWord
            transformedWord.translation = "" // мы сами "кошечка" из "кошка" не образуем
            transformedWord.immutableAttrs = rule.immutableAttrs
            return transformedWord
        }
    }
}