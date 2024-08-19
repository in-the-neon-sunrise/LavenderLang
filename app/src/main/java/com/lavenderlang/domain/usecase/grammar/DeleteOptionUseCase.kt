package com.lavenderlang.domain.usecase.grammar

import android.util.Log
import com.lavenderlang.data.PythonHandlerImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.LanguageEntity
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
import com.lavenderlang.domain.usecase.dictionary.AddWordUseCase
import com.lavenderlang.domain.usecase.dictionary.DeleteWordUseCase
import com.lavenderlang.ui.MyApp

class DeleteOptionUseCase {
    companion object {
        suspend fun execute(option: CharacteristicEntity, language: LanguageEntity) {
            val grammar = language.grammar
            when (option.type) {
                Attributes.GENDER -> grammar.varsGender.remove(option.characteristicId)
                Attributes.NUMBER -> grammar.varsNumber.remove(option.characteristicId)
                Attributes.CASE -> grammar.varsCase.remove(option.characteristicId)
                Attributes.TIME -> grammar.varsTime.remove(option.characteristicId)
                Attributes.PERSON -> grammar.varsPerson.remove(option.characteristicId)
                Attributes.MOOD -> grammar.varsMood.remove(option.characteristicId)
                Attributes.TYPE -> grammar.varsType.remove(option.characteristicId)
                Attributes.VOICE -> grammar.varsVoice.remove(option.characteristicId)
                Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison.remove(option.characteristicId)
                Attributes.IS_INFINITIVE -> return
            }
            for (word in language.dictionary.dict) {
                if (word.mutableAttrs.contains(option.type) &&
                    word.mutableAttrs[option.type] == option.characteristicId
                ) {
                    val immutAttrs = word.immutableAttrs
                    immutAttrs[option.type] = 0 // инфинитив
                    updateWord(word, word.word, word.translation, immutAttrs, word.partOfSpeech)
                }
            }
        }


        private suspend fun updateWord(
            oldWordEntity: IWordEntity,
            newWord: String,
            newTranslation: String,
            newImmutableAttrs: MutableMap<Attributes, Int>,
            newPartOfSpeech: PartOfSpeech
        ) {
            val newWordEntity = when (newPartOfSpeech) {
                PartOfSpeech.NOUN -> NounEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.VERB -> VerbEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.ADJECTIVE -> AdjectiveEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.ADVERB -> AdverbEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.PARTICIPLE -> ParticipleEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.VERB_PARTICIPLE -> VerbParticipleEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.PRONOUN -> PronounEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.NUMERAL -> NumeralEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.FUNC_PART -> FuncPartEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )
            }
                DeleteWordUseCase.execute(
                    MyApp.language!!.dictionary, oldWordEntity, PythonHandlerImpl()
                )
                AddWordUseCase.execute(
                    MyApp.language!!, newWordEntity, PythonHandlerImpl()
                )
                Log.d("update word", newWordEntity.word + " " + newWordEntity.translation)
        }
    }
}