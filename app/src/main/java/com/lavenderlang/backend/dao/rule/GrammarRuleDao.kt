package com.lavenderlang.backend.dao.rule

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.lavenderlang.frontend.MainActivity
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
import com.lavenderlang.frontend.MyApp
import com.lavenderlang.frontend.languages
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
            if (!languages[rule.languageId]!!.vowels.contains(letter.lowercase()) &&
                !languages[rule.languageId]!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        for (letter in newTransformation.addToEnd) {
            if (!languages[rule.languageId]!!.vowels.contains(letter.lowercase()) &&
                !languages[rule.languageId]!!.consonants.contains(letter.lowercase())) {
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
                    languages[transformedWord.languageId]!!,
                    attr,
                    transformedWord.mutableAttrs[attr]!!
                )
            )
        }
        val py = Python.getInstance()
        val module = py.getModule("pm3")
//        val translationParts = word.translation.split(" ")
//        val res = ""
//        for (transPart in translationParts) {
//            module.callAttr(
//                "inflectAttrs", transPart,
//                word.partOfSpeech.toString(),
//                russianMutAttrs.toString()
//            ).toString()
//        }
        val res = module.callAttr(
            "inflectAttrs", word.translation,
            word.partOfSpeech.toString(),
            russianMutAttrs.toString()
        ).toString()
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
            if (!languages[rule.languageId]!!.vowels.contains(letter.lowercase()) &&
                !languages[rule.languageId]!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        for (letter in transformation.addToEnd) {
            if (!languages[rule.languageId]!!.vowels.contains(letter.lowercase()) &&
                !languages[rule.languageId]!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        rule.transformation = transformation
        updateMutableAttrs(rule, newAttrs)
        GlobalScope.launch(Dispatchers.IO) {
            helper.updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
            languageRepository.updateGrammar(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeGrammar(languages[rule.languageId]!!.grammar)
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
                Attributes.GENDER -> "род: ${languages[rule.languageId]!!.grammar.varsGender[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.TYPE -> "вид: ${languages[rule.languageId]!!.grammar.varsType[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.VOICE -> "залог: ${languages[rule.languageId]!!.grammar.varsVoice[rule.masc.immutableAttrs[attr]]!!.name}, "
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