package com.lavenderlang.backend.dao.rule

import android.util.Log
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.TransformationDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.dao.language.TranslatorHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
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
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.exception.IncorrectRegexException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.rule.IRuleEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface GrammarRuleDao : RuleDao {
    fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity)
    fun updateMutableAttrs(rule : GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun grammarTransformByRule(rule: GrammarRuleEntity, word : IWordEntity) : IWordEntity
    fun updateRule(rule : GrammarRuleEntity, masc: MascEntity, transformation: TransformationEntity, newAttrs: MutableMap<Attributes, Int>)
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
        rule.masc = newMasc
    }
    override fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity) {
        //check if rule is correct (letters in transformation are in language)
        for (letter in newTransformation.addToBeginning) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        for (letter in newTransformation.addToEnd) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        rule.transformation = newTransformation
    }

    override fun updateMutableAttrs(rule: GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>) {
        rule.mutableAttrs = newAttrs
    }
    override fun grammarTransformByRule(rule: GrammarRuleEntity, word: IWordEntity): IWordEntity {
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
        transformedWord.word = TransformationDaoImpl().transform(rule.transformation, word.word)
        for (attr in rule.mutableAttrs.keys) {
            transformedWord.mutableAttrs[attr] = rule.mutableAttrs[attr]!!
        }

        val translatorHelper = TranslatorHelperDaoImpl()
        val russianMutAttrs = arrayListOf<Int>()
        for (attr in transformedWord.mutableAttrs.keys) {
            russianMutAttrs.add(
                translatorHelper.conlangToRusAttr(
                    MyApp.language!!,
                    attr,
                    transformedWord.mutableAttrs[attr]!!
                )
            )
        }
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        var res = ""

        if (word.translation.contains(' ')) {
            val translationParts = word.translation.split(" ")
            for (transPart in translationParts) {
                res +=
                    module.callAttr(
                        "inflectAttrs", transPart,
                        word.partOfSpeech.toString(),
                        russianMutAttrs.toString()
                    ).toString()
                res += " "
            }
            res = res.slice(0 until res.length - 1)
        } else {
            res = module.callAttr(
                "inflectAttrs", word.translation,
                word.partOfSpeech.toString(),
                russianMutAttrs.toString()
            ).toString()
        }

        transformedWord.translation = res

        return transformedWord
    }

    override fun updateRule(rule: GrammarRuleEntity, masc: MascEntity, transformation: TransformationEntity, newAttrs: MutableMap<Attributes, Int>) {
        val oldRule = rule.copy()
        Log.d("updateRule", "oldRule: $oldRule")

        try {
            masc.regex.toRegex()
        } catch (e : Exception) {
            throw IncorrectRegexException("Неверное регулярное выражение!")
        }
        rule.masc = masc

        for (letter in transformation.addToBeginning) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        for (letter in transformation.addToEnd) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        rule.transformation = transformation
        updateMutableAttrs(rule, newAttrs)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            helper.updateMadeByRule(MyApp.language!!.dictionary, oldRule, rule)
            languageRepository.updateGrammar(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeGrammar(MyApp.language!!.grammar)
            )
            languageRepository.updateDictionary(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeDictionary(MyApp.language!!.dictionary)
            )
        }
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
                Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.masc.immutableAttrs[attr]]!!.name}, "
                else -> continue
            }
        }
        return res.slice(0 until res.length - 2)
    }

    override fun getResultInfo(rule: IRuleEntity): String {
        if (rule !is GrammarRuleEntity) return ""
        var res = ""
        for (attr in rule.mutableAttrs.keys) {
            res += when (attr) {
                Attributes.CASE -> "падеж: ${MyApp.language!!.grammar.varsCase[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.NUMBER -> "число: ${MyApp.language!!.grammar.varsNumber[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.DEGREE_OF_COMPARISON -> "степень сравнения: ${MyApp.language!!.grammar.varsDegreeOfComparison[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.TIME -> "время: ${MyApp.language!!.grammar.varsTime[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.MOOD -> "наклонение: ${MyApp.language!!.grammar.varsMood[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.PERSON -> "лицо: ${MyApp.language!!.grammar.varsPerson[rule.mutableAttrs[attr]!!]?.name}, "
                Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.mutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return res
        return res.slice(0 until res.length - 2)
    }
}