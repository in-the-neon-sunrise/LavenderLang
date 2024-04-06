package com.lavenderlang.backend.dao.word

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
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
        GlobalScope.launch {
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
        GlobalScope.launch {
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
        MainActivity.getInstance().lifecycleScope.launch {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }
    }

    override fun updatePartOfSpeech(word: IWordEntity, newPartOfSpeech: PartOfSpeech) {
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
        /*when (newPartOfSpeech) {
            PartOfSpeech.NOUN -> NounEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.VERB -> VerbEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.ADJECTIVE -> AdjectiveEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.ADVERB -> AdverbEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.PARTICIPLE -> ParticipleEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.VERBPARTICIPLE -> VerbParticipleEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.PRONOUN -> PronounEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.NUMERAL -> NumeralEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
            PartOfSpeech.FUNCPART -> FuncPartEntity(
                word.languageId,
                word.word,
                word.translation,
                partOfSpeech=newPartOfSpeech
            )
        }*/
        TODO("how to update the whole word???")
        //DictionaryHelperDaoImpl().updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
    }
}