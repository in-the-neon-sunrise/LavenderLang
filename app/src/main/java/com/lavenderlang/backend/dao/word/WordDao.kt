package com.lavenderlang.backend.dao.word

import android.content.Context
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages

interface WordDao {
    fun updateWord(word : IWordEntity, newWord : String, context: Context)
    fun updateTranslation(word : IWordEntity, newTranslation : String, context: Context)
    fun updateImmutableAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>, context: Context)
    fun updatePartOfSpeech(word : IWordEntity, newPartOfSpeech : PartOfSpeech, context: Context)
}

class WordDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                  private val languageRepository: LanguageRepository = LanguageRepository()
) : WordDao {
    override fun updateWord(word: IWordEntity, newWord: String, context: Context) {
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
        Thread {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                context, word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }.start()
    }

    override fun updateTranslation(word: IWordEntity, newTranslation: String, context: Context) {
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
        Thread {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                context, word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }.start()
    }
    override fun updateImmutableAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>, context: Context) {
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
        Thread {
            helper.updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
            languageRepository.updateLanguage(
                context, word.languageId,
                Serializer.getInstance().serializeLanguage(languages[word.languageId]!!))
        }.start()
    }

    override fun updatePartOfSpeech(word: IWordEntity, newPartOfSpeech: PartOfSpeech, context: Context) {
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