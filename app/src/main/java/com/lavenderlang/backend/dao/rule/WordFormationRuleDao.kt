package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.dao.help.TransformationDaoImpl
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
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.exception.IncorrectRegexException
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity
import com.lavenderlang.domain.model.rule.IRuleEntity
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface WordFormationRuleDao : RuleDao {
    fun updateTransformation(rule : WordFormationRuleEntity, newTransformation: TransformationEntity)
    fun updateDescription(rule : WordFormationRuleEntity, newDescription: String)
    fun updateImmutableAttrs(rule : WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun updatePartOfSpeech(rule : WordFormationRuleEntity, newPartOfSpeech: PartOfSpeech)
    fun updateRule(rule : WordFormationRuleEntity, masc: MascEntity, transformation: TransformationEntity, description: String, newAttrs: MutableMap<Attributes, Int>, partOfSpeech: PartOfSpeech)
    fun wordFormationTransformByRule(word : IWordEntity, rule : WordFormationRuleEntity) : IWordEntity

}
class WordFormationRuleDaoImpl(private val languageRepositoryDEPRECATED: LanguageRepositoryDEPRECATED = LanguageRepositoryDEPRECATED())
    : WordFormationRuleDao {
    override fun updateMasc(rule: IRuleEntity, newMasc: MascEntity) {
        try {
            newMasc.regex.toRegex()
        } catch (e : Exception) {
            throw IncorrectRegexException("Неверное регулярное выражение!")
        }
        rule.masc = newMasc
    }

    override fun updateTransformation(rule: WordFormationRuleEntity, newTransformation: TransformationEntity) {
        //check if rule is correct (letters in transformation are in language)
        for (letter in newTransformation.addToBeginning) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        for (letter in newTransformation.addToEnd) {
            if (!MyApp.language!!.vowels.contains(letter) &&
                !MyApp.language!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        rule.transformation = newTransformation
    }

    override fun updateDescription(rule: WordFormationRuleEntity, newDescription: String) {
        rule.description = newDescription
    }

    override fun updateImmutableAttrs(rule: WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>) {
        rule.immutableAttrs = newAttrs
    }

    override fun updateRule(rule: WordFormationRuleEntity, masc: MascEntity, transformation: TransformationEntity, description: String, newAttrs: MutableMap<Attributes, Int>, partOfSpeech: PartOfSpeech) {
        updateMasc(rule, masc)
        updateTransformation(rule, transformation)
        updateDescription(rule, description)
        updateImmutableAttrs(rule, newAttrs)
        updatePartOfSpeech(rule, partOfSpeech)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            languageRepositoryDEPRECATED.updateGrammar(
                MyApp.getInstance().applicationContext, rule.languageId,
                Serializer.getInstance().serializeGrammar(MyApp.language!!.grammar)
            )
        }
    }
    override fun wordFormationTransformByRule(word: IWordEntity, rule: WordFormationRuleEntity) : IWordEntity {
        val transformedWord : IWordEntity = when (rule.partOfSpeech) {
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
        val newWord = TransformationDaoImpl().transform(rule.transformation, word.word)
        transformedWord.word = newWord
        transformedWord.translation = "Введите перевод" // мы сами "кошечка" из "кошка" не образуем
        transformedWord.immutableAttrs = rule.immutableAttrs
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
                Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.masc.immutableAttrs[attr]]!!.name}, "
                Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.masc.immutableAttrs[attr]]!!.name}, "
                else -> continue
            }
        }
        return res.slice(0 until res.length - 2)
    }

    override fun updatePartOfSpeech(rule : WordFormationRuleEntity, newPartOfSpeech: PartOfSpeech) {
        rule.partOfSpeech = newPartOfSpeech
    }

    override fun getResultInfo(rule: IRuleEntity): String {
        if (rule !is WordFormationRuleEntity) return ""
        var res = when (rule.partOfSpeech) {
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
        for (attr in rule.immutableAttrs.keys) {
            res += when (attr) {
                Attributes.GENDER -> "род: ${MyApp.language!!.grammar.varsGender[rule.immutableAttrs[attr]!!]?.name}, "
                Attributes.TYPE -> "вид: ${MyApp.language!!.grammar.varsType[rule.immutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${MyApp.language!!.grammar.varsVoice[rule.immutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return res
        return res.slice(0 until res.length - 2)
    }
}