package com.lavenderlang.backend.dao.word

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface WordDao {
    fun updateWord(word : IWordEntity, newWord : String)
    fun updateTranslation(word : IWordEntity, newTranslation : String)
    fun updateImmutableAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>)
    fun updatePartOfSpeech(word : IWordEntity, newPartOfSpeech : PartOfSpeech)
}

class WordDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                  private val languageRepository: LanguageRepository = LanguageRepository()
) : WordDao {
    override fun updateWord(word: IWordEntity, newWord: String) {
        val oldWord = when (word.partOfSpeech) {
            PartOfSpeech.NOUN -> (word as NounEntity).copy()
            PartOfSpeech.VERB -> (word as VerbEntity).copy()
            PartOfSpeech.ADJECTIVE -> (word as AdjectiveEntity).copy()
            PartOfSpeech.ADVERB -> (word as AdverbEntity).copy()
            PartOfSpeech.PARTICIPLE -> (word as ParticipleEntity).copy()
            PartOfSpeech.VERB_PARTICIPLE -> (word as VerbParticipleEntity).copy()
            PartOfSpeech.PRONOUN -> (word as PronounEntity).copy()
            PartOfSpeech.NUMERAL -> (word as NumeralEntity).copy()
            PartOfSpeech.FUNC_PART -> (word as FuncPartEntity).copy()
        }
        word.word = newWord
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }
    }

    override fun updateTranslation(word: IWordEntity, newTranslation: String) {
        val oldWord = when (word.partOfSpeech) {
            PartOfSpeech.NOUN -> (word as NounEntity).copy()
            PartOfSpeech.VERB -> (word as VerbEntity).copy()
            PartOfSpeech.ADJECTIVE -> (word as AdjectiveEntity).copy()
            PartOfSpeech.ADVERB -> (word as AdverbEntity).copy()
            PartOfSpeech.PARTICIPLE -> (word as ParticipleEntity).copy()
            PartOfSpeech.VERB_PARTICIPLE -> (word as VerbParticipleEntity).copy()
            PartOfSpeech.PRONOUN -> (word as PronounEntity).copy()
            PartOfSpeech.NUMERAL -> (word as NumeralEntity).copy()
            PartOfSpeech.FUNC_PART -> (word as FuncPartEntity).copy()
        }
        word.translation = newTranslation
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }
    }
    override fun updateImmutableAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>) {
        val oldWord = when (word.partOfSpeech) {
            PartOfSpeech.NOUN -> (word as NounEntity).copy()
            PartOfSpeech.VERB -> (word as VerbEntity).copy()
            PartOfSpeech.ADJECTIVE -> (word as AdjectiveEntity).copy()
            PartOfSpeech.ADVERB -> (word as AdverbEntity).copy()
            PartOfSpeech.PARTICIPLE -> (word as ParticipleEntity).copy()
            PartOfSpeech.VERB_PARTICIPLE -> (word as VerbParticipleEntity).copy()
            PartOfSpeech.PRONOUN -> (word as PronounEntity).copy()
            PartOfSpeech.NUMERAL -> (word as NumeralEntity).copy()
            PartOfSpeech.FUNC_PART -> (word as FuncPartEntity).copy()
        }
        for (attr in args.keys) {
            word.immutableAttrs[attr] = args[attr]!!
        }
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }
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
}