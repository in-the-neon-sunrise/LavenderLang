package com.lavenderlang.backend.dao.rule

import android.util.Log
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.TransformationDaoImpl
import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.data.LanguageRepositoryDEPRECATED
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
import com.lavenderlang.data.PythonHandlerImpl
import com.lavenderlang.domain.conlangToRusAttr
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.rule.IRuleEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface GrammarRuleDao {
    fun grammarTransformByRule(rule: GrammarRuleEntity, word : IWordEntity) : IWordEntity
    fun updateRule(rule : GrammarRuleEntity, masc: MascEntity, transformation: TransformationEntity, newAttrs: MutableMap<Attributes, Int>)
}
class GrammarRuleDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                         private val languageRepositoryDEPRECATED: LanguageRepositoryDEPRECATED = LanguageRepositoryDEPRECATED()
) : GrammarRuleDao {
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

        val russianMutAttrs = arrayListOf<Int>()
        for (attr in transformedWord.mutableAttrs.keys) {
            russianMutAttrs.add(
                conlangToRusAttr(
                    MyApp.language!!,
                    attr,
                    transformedWord.mutableAttrs[attr]!!
                )
            )
        }

        var res = ""

        if (word.translation.contains(' ')) {
            val translationParts = word.translation.split(" ")
            for (transPart in translationParts) {
                res +=
                    PythonHandlerImpl().inflectAttrs(
                    transPart,
                        word.partOfSpeech.toString(),
                        russianMutAttrs.toString()
                    )
                res += " "
            }
            res = res.slice(0 until res.length - 1)
        } else {
            res = PythonHandlerImpl().inflectAttrs(word.translation,
                word.partOfSpeech.toString(),
                russianMutAttrs.toString()
            )
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
        rule.mutableAttrs = newAttrs
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            helper.updateMadeByRule(MyApp.language!!.dictionary, oldRule, rule)
            languageRepositoryDEPRECATED.updateGrammar(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeGrammar(MyApp.language!!.grammar)
            )
            languageRepositoryDEPRECATED.updateDictionary(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeDictionary(MyApp.language!!.dictionary)
            )
        }
    }
}