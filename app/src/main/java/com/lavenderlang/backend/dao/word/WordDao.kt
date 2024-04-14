package com.lavenderlang.backend.dao.word

import android.content.Context
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.rule.IRuleEntity
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface WordDao {
    fun updateWord(word : IWordEntity, newWord : String)
    fun updateTranslation(word : IWordEntity, newTranslation : String)
    fun updateImmutableAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>)
    fun updatePartOfSpeech(word : IWordEntity, newPartOfSpeech : PartOfSpeech)
    fun updateWord(word: IWordEntity, newWord: String, newTranslation: String, newImmutableAttrs: MutableMap<Attributes, Int>, newPartOfSpeech: PartOfSpeech)
    fun getImmutableAttrsInfo(word : IWordEntity) : String
}

class WordDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                  private val languageRepository: LanguageRepository = LanguageRepository()
) : WordDao {
    override fun updateWord(word: IWordEntity, newWord: String) {
        for (letter in newWord) {
            if (!languages[word.languageId]!!.vowels.contains(letter) &&
                !languages[word.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        word.word = newWord
    }

    override fun updateTranslation(word: IWordEntity, newTranslation: String) {
        word.translation = newTranslation
    }
    override fun updateImmutableAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>) {
        word.immutableAttrs = args
    }

    override fun updatePartOfSpeech(word: IWordEntity, newPartOfSpeech: PartOfSpeech) {
        val newWord = when (newPartOfSpeech) {
            PartOfSpeech.NOUN -> NounEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.VERB -> VerbEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.ADJECTIVE -> AdjectiveEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.ADVERB -> AdverbEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.PARTICIPLE -> ParticipleEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.VERB_PARTICIPLE -> VerbParticipleEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.PRONOUN -> PronounEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.NUMERAL -> NumeralEntity(
                word.languageId,
                word.word,
                word.translation
            )
            PartOfSpeech.FUNC_PART -> FuncPartEntity(
                word.languageId,
                word.word,
                word.translation
            )
        }
        val dictionaryHandler = DictionaryDaoImpl()
        dictionaryHandler.deleteWord(languages[word.languageId]!!.dictionary, word)
        dictionaryHandler.addWord(languages[word.languageId]!!.dictionary, newWord)
    }

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
        val dictionaryHandler = DictionaryDaoImpl()
        dictionaryHandler.deleteWord(languages[word.languageId]!!.dictionary, word)
        dictionaryHandler.addWord(languages[word.languageId]!!.dictionary, newWordEntity)
    }
    override fun getImmutableAttrsInfo(word: IWordEntity): String {
        var res = ""
        Log.d("frfrfr", word.word+word.partOfSpeech+word.immutableAttrs.toString())
        for (attr in word.immutableAttrs.keys) {
            res += when (attr) {
                Attributes.GENDER -> "род: ${languages[word.languageId]!!.grammar.varsGender[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.TYPE -> "вид: ${languages[word.languageId]!!.grammar.varsType[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${languages[word.languageId]!!.grammar.varsVoice[word.immutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return ""
        return res.slice(0 until res.length - 2)
    }
}