package com.lavenderlang.backend.dao.rule

import android.content.Context
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.TransformationDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.dao.language.TranslatorHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.entity.word.AdjectiveEntity
import com.lavenderlang.backend.entity.word.AdverbEntity
import com.lavenderlang.backend.entity.word.FuncPartEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.entity.word.NounEntity
import com.lavenderlang.backend.entity.word.NumeralEntity
import com.lavenderlang.backend.entity.word.ParticipleEntity
import com.lavenderlang.backend.entity.word.PronounEntity
import com.lavenderlang.backend.entity.word.VerbEntity
import com.lavenderlang.backend.entity.word.VerbParticipleEntity
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages

interface GrammarRuleDao : RuleDao {
    fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity, context: Context)
    fun updateMutableAttrs(rule : GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>, context: Context)
    fun grammarTransformByRule(rule: GrammarRuleEntity, word : IWordEntity) : IWordEntity
}
class GrammarRuleDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                         private val languageRepository: LanguageRepository = LanguageRepository()
) : GrammarRuleDao {
    override fun updateMasc(rule : IRuleEntity, newMasc : MascEntity, context: Context) {
        val oldRule = (rule as GrammarRuleEntity).copy()
        rule.masc = newMasc
        Thread {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                context, rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )

        }.start()
    }
    override fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity, context: Context) {
        //check if rule is correct (letters in transformation are in language)
        for (letter in newTransformation.addToBeginning) {
            if (!languages[rule.languageId]!!.vowels.contains(letter) &&
                !languages[rule.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        for (letter in newTransformation.addToEnd) {
            if (!languages[rule.languageId]!!.vowels.contains(letter) &&
                !languages[rule.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        val oldRule = rule.copy()
        rule.transformation = newTransformation
        Thread {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                context, rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )
        }.start()
    }

    override fun updateMutableAttrs(rule: GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>, context: Context) {
        val oldRule = rule.copy()
        rule.mutableAttrs = newAttrs
        Thread {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                context, rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )
        }.start()
    }
    override fun grammarTransformByRule(rule: GrammarRuleEntity, word: IWordEntity): IWordEntity {
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
        val newWord = TransformationDaoImpl().transform(rule.transformation, word.word)
        transformedWord.word = newWord
        for (attr in rule.mutableAttrs.keys) {
            transformedWord.mutableAttrs[attr] = rule.mutableAttrs[attr]!!
        }

        val translatorHelper = TranslatorHelperDaoImpl()
        val russianMutAttrs = arrayListOf<Int>()
        for (attr in transformedWord.mutableAttrs.keys) {
            russianMutAttrs.add(translatorHelper.conlangToRusAttr(
                languages[transformedWord.languageId]!!, attr, transformedWord.mutableAttrs[attr]!!)
            )
        }
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        val res = module.callAttr(
            "inflectAttrs", word.translation,
            word.partOfSpeech.toString(),
            russianMutAttrs.toString()
        ).toString()
        transformedWord.translation = res

        return transformedWord
    }
}