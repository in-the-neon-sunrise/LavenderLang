package com.lavenderlang.backend.dao.rule

import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.lavenderlang.MainActivity
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
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.exception.IncorrectRegexException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface GrammarRuleDao : RuleDao {
    fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity)
    fun updateMutableAttrs(rule : GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun grammarTransformByRule(rule: GrammarRuleEntity, word : IWordEntity) : IWordEntity
}
class GrammarRuleDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                         private val languageRepository: LanguageRepository = LanguageRepository()
) : GrammarRuleDao {
    override fun updateMasc(rule : IRuleEntity, newMasc : MascEntity) {
        try {
            newMasc.regex.toRegex()
        } catch (e : Exception) {
            throw IncorrectRegexException("Неверное регулярное выражение!")
        }
        val oldRule = (rule as GrammarRuleEntity).copy()
        rule.masc = newMasc
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )
        }
    }
    override fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity) {
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
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )
        }
    }

    override fun updateMutableAttrs(rule: GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>) {
        val oldRule = rule.copy()
        rule.mutableAttrs = newAttrs
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), rule.languageId,
                Serializer.getInstance().serializeLanguage(languages[rule.languageId]!!)
            )
        }
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

    override fun getOrigInfo(rule: IRuleEntity): String {
        var res = when (rule.masc.partOfSpeech) {
            PartOfSpeech.NOUN -> "существительное; "
            PartOfSpeech.VERB -> "глагол; "
            PartOfSpeech.ADJECTIVE -> "прилагательное; "
            PartOfSpeech.ADVERB -> "наречие; "
            PartOfSpeech.PARTICIPLE -> "причастие; "
            PartOfSpeech.VERB_PARTICIPLE -> "деепричастие; "
            PartOfSpeech.PRONOUN -> "местоимение; "
            PartOfSpeech.NUMERAL -> "числительное; "
            PartOfSpeech.FUNC_PART -> "служебное слово; "
        }
        for (attr in rule.masc.immutableAttrs.keys) {
            res += when (attr) {
                Attributes.GENDER -> "род: "
                Attributes.TYPE -> "вид: "
                Attributes.VOICE -> "залог: "
                else -> continue
            }
            for (e in rule.masc.immutableAttrs[attr]!!) {
                res += when (attr) {
                    Attributes.GENDER -> languages[rule.languageId]!!.grammar.varsGender[e]?.name + ", "
                    Attributes.TYPE -> languages[rule.languageId]!!.grammar.varsType[e]?.name + ", "
                    Attributes.VOICE -> languages[rule.languageId]!!.grammar.varsVoice[e]?.name + ", "
                    else -> break
                }
            }
        }
        return res.slice(0 until res.length - 2)
    }

    override fun getResultInfo(rule: IRuleEntity): String {
        if (rule !is GrammarRuleEntity) return ""
        var res = ""
        for (attr in rule.mutableAttrs.keys) {
            res += when (attr) {
                Attributes.CASE -> "падеж: ${languages[rule.languageId]!!.grammar.varsCase[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.NUMBER -> "число: ${languages[rule.languageId]!!.grammar.varsNumber[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${languages[rule.languageId]!!.grammar.varsVoice[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.DEGREE_OF_COMPARISON -> "степень сравнения: ${languages[rule.languageId]!!.grammar.varsDegreeOfComparison[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.TIME -> "время: ${languages[rule.languageId]!!.grammar.varsTime[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.MOOD -> "наклонение: ${languages[rule.languageId]!!.grammar.varsMood[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.PERSON -> "лицо: ${languages[rule.languageId]!!.grammar.varsPerson[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.GENDER -> "род: ${languages[rule.languageId]!!.grammar.varsGender[rule.mutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return res
        return res.slice(0 until res.length - 2)
    }
}