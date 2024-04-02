package com.lavenderlang.backend.dao.word

import android.content.Context
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.dao.language.TranslatorHelperDaoImpl
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.languages

interface WordDao {
    //fun grammarTransformByAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>) : IWordEntity
    //fun wordFormationTransformByAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>) : IWordEntity
    fun updateWord(word : IWordEntity, newWord : String, context: Context)
    fun updateTranslation(word : IWordEntity, newTranslation : String, context: Context)
    //fun updateImmutableAttr(word : IWordEntity, attribute: Attributes, newId : Int, context: Context)
    fun updateImmutableAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>, context: Context)
    fun updatePartOfSpeech(word : IWordEntity, newPartOfSpeech : PartOfSpeech, context: Context)
}

class WordDaoImpl : WordDao {
    /*override fun grammarTransformByAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>): IWordEntity {
        val transformedWord: IWordEntity = when (word) {
            is NounEntity -> word.copy()
            is VerbEntity -> word.copy()
            is AdjectiveEntity -> word.copy()
            is AdverbEntity -> word.copy()
            is ParticipleEntity -> word.copy()
            is VerbParticipleEntity -> word.copy()
            is PronounEntity -> word.copy()
            is NumeralEntity -> word.copy()
            is FuncPartEntity -> word.copy()
            else -> throw Error() // какую-нибудь красивую
        }
        val mascHandler = MascDaoImpl();
        val grammar = languages[word.languageId]!!.grammar
        for (rule in grammar.grammarRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            var check = true
            for (attr in args.keys) {
                if (!rule.mutableAttrs.contains(attr) || rule.mutableAttrs[attr]!! != args[attr]) {
                    check = false
                    break
                }
            }
            if (!check) continue
            return grammarTransformByRule(word, rule)
        }
        return transformedWord
    }*/

    /*override fun wordFormationTransformByAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>): IWordEntity {
        val transformedWord : IWordEntity = when (word) {
            is NounEntity -> word.copy()
            is VerbEntity -> word.copy()
            is AdjectiveEntity -> word.copy()
            is AdverbEntity -> word.copy()
            is ParticipleEntity -> word.copy()
            is VerbParticipleEntity -> word.copy()
            is PronounEntity -> word.copy()
            is NumeralEntity -> word.copy()
            is FuncPartEntity -> word.copy()
            else -> throw Error() // какую-нибудь красивую
        }
        val mascHandler = MascDaoImpl();
        val grammar = languages[word.languageId]!!.grammar
        for (rule in grammar.wordFormationRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            var check = true
            for (attr in args.keys) {
                if (!rule.immutableAttrs.contains(attr) || rule.immutableAttrs[attr]!! != args[attr]) {
                    check = false
                    break
                }
            }
            if (!check) continue
            val newWord = rule.transformation.addToBeginning + word.word.slice(
                IntRange(
                    rule.transformation.delFromBeginning,
                    word.word.length - rule.transformation.delFromEnd - 1
                )
            ) +
                    rule.transformation.addToEnd
            transformedWord.word = newWord
            transformedWord.translation =
                "Введите перевод" // мы сами "кошечка" из "кошка" не образуем
            for (attr in args.keys) {
                transformedWord.immutableAttrs[attr] = args[attr]!!
            }
            return transformedWord
        }
        throw Error() // but like pretty error... Not Found??
    }*/

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
        DictionaryHelperDaoImpl().updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
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
        DictionaryHelperDaoImpl().updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
    }

    /*override fun updateImmutableAttr(word: IWordEntity, attribute: Attributes, newId: Int, context: Context) {
        if (!word.immutableAttrs.contains(attribute)) return
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
        word.immutableAttrs[attribute] = newId
        DictionaryHelperDaoImpl().updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
    }*/

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
        DictionaryHelperDaoImpl().updateMadeByWord(languages[word.languageId]!!.dictionary, oldWord, word)
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