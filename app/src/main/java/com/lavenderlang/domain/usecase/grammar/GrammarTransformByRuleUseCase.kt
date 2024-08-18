package com.lavenderlang.domain.usecase.grammar

import com.lavenderlang.domain.conlangToRusAttr
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
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
import com.lavenderlang.domain.transformWord
class GrammarTransformByRuleUseCase {
    companion object {
        fun execute(
            rule: GrammarRuleEntity,
            word: IWordEntity,
            language: LanguageEntity,
            py: PythonHandler
        ): IWordEntity {
            val transformedWord: IWordEntity = when (word.partOfSpeech) {
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
            transformedWord.languageId = word.languageId
            transformedWord.immutableAttrs = word.immutableAttrs
            transformedWord.word = transformWord(rule.transformation, word.word)
            for (attr in rule.mutableAttrs.keys) {
                transformedWord.mutableAttrs[attr] = rule.mutableAttrs[attr]!!
            }

            val russianMutAttrs = arrayListOf<Int>()
            for (attr in transformedWord.mutableAttrs.keys) {
                russianMutAttrs.add(
                    conlangToRusAttr(
                        language,
                        attr,
                        transformedWord.mutableAttrs[attr]!!
                    )
                )
            }

            var res = ""

            if (word.translation.contains(' ')) {
                val translationParts = word.translation.split(" ")
                for (transPart in translationParts) {
                    res +=
                        py.inflectAttrs(
                            transPart,
                            word.partOfSpeech.toString(),
                            russianMutAttrs.toString()
                        )
                    res += " "
                }
                res = res.slice(0 until res.length - 1)
            } else {
                res = py.inflectAttrs(
                    word.translation,
                    word.partOfSpeech.toString(),
                    russianMutAttrs.toString()
                )
            }

            transformedWord.translation = res

            return transformedWord
        }
    }
}