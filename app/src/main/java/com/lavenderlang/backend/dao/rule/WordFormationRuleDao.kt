package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.dao.help.TransformationDaoImpl
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
import com.lavenderlang.languages

interface WordFormationRuleDao : RuleDao {
    fun updateTransformation(rule : WordFormationRuleEntity, newTransformation: TransformationEntity)
    fun updateDescription(rule : WordFormationRuleEntity, newDescription: String)
    fun updateImmutableAttrs(rule : WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun updatePartOfSpeech(rule : WordFormationRuleEntity, newPartOfSpeech: PartOfSpeech)
    fun wordFormationTransformByRule(word : IWordEntity, rule : WordFormationRuleEntity) : IWordEntity

}
class WordFormationRuleDaoImpl : WordFormationRuleDao {
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
        rule.transformation = newTransformation
    }

    override fun updateDescription(rule: WordFormationRuleEntity, newDescription: String) {
        rule.description = newDescription
    }

    override fun updateImmutableAttrs(rule: WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>) {
        rule.immutableAttrs = newAttrs
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
                Attributes.GENDER -> "род: ${languages[rule.languageId]!!.grammar.varsGender[rule.immutableAttrs[attr]!!]?.name}, "
                Attributes.TYPE -> "вид: ${languages[rule.languageId]!!.grammar.varsType[rule.immutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${languages[rule.languageId]!!.grammar.varsVoice[rule.immutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return res
        return res.slice(0 until res.length - 2)
    }
}