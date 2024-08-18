package com.lavenderlang.backend.dao.word

import android.util.Log
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.data.PythonHandlerImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface WordDao {
    fun updateWord(word: IWordEntity, newWord: String, newTranslation: String, newImmutableAttrs: MutableMap<Attributes, Int>, newPartOfSpeech: PartOfSpeech)
    fun getImmutableAttrsInfo(word : IWordEntity) : String
}

class WordDaoImpl : WordDao {
    override fun updateWord(word: IWordEntity, newWord: String, newTranslation: String, newImmutableAttrs: MutableMap<Attributes, Int>, newPartOfSpeech: PartOfSpeech) {
        val newWordEntity = when (newPartOfSpeech) {
            PartOfSpeech.NOUN -> NounEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.VERB -> VerbEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.ADJECTIVE -> AdjectiveEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.ADVERB -> AdverbEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.PARTICIPLE -> ParticipleEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.VERB_PARTICIPLE -> VerbParticipleEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.PRONOUN -> PronounEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.NUMERAL -> NumeralEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
            PartOfSpeech.FUNC_PART -> FuncPartEntity(
                word.languageId,
                newWord,
                newTranslation,
                immutableAttrs = newImmutableAttrs
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            DeleteWordUseCase.execute(MyApp.language!!.dictionary, word,
                LanguageRepositoryImpl(), PythonHandlerImpl())
            AddWordUseCase.execute(MyApp.language!!, newWordEntity,
                LanguageRepositoryImpl(), PythonHandlerImpl())
            Log.d("update word", newWordEntity.word + " " + newWordEntity.translation)
        }
    }
    override fun getImmutableAttrsInfo(word: IWordEntity): String {
        var res = ""
        for (attr in word.immutableAttrs.keys) {
            res += when (attr) {
                Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[word.immutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return ""
        return res.slice(0 until res.length - 2)
    }
}