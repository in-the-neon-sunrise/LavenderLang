package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.languages

interface GrammarDao {
    fun addOption(grammar : GrammarEntity, option : Characteristic);
    fun deleteOption(grammar : GrammarEntity, option : Characteristic);
    fun updateOption(grammar : GrammarEntity, optionId: Int, newOption: Characteristic) : Boolean;
    fun updateBase(grammar : GrammarEntity, newBase : Int);
    fun addGrammarRule(grammar : GrammarEntity, rule: GrammarRuleEntity);
    fun deleteGrammarRule(grammar : GrammarEntity, rule : GrammarRuleEntity) : Boolean;
    fun addWordFormationRule(grammar : GrammarEntity, rule: WordFormationRuleEntity);
    fun deleteWordFormationRule(grammar : GrammarEntity, rule : WordFormationRuleEntity) : Boolean;
    fun delMadeByRule(grammar: GrammarEntity, rule: GrammarRuleEntity);
    fun addMadeByRule(grammar: GrammarEntity, rule: GrammarRuleEntity);
}

class GrammarDaoImpl(val wordHandler : WordDaoImpl = WordDaoImpl()) : GrammarDao {
    override fun addOption(grammar: GrammarEntity, option: Characteristic) {
        val map: MutableMap<Int, Characteristic> = when (option.type) {
            Attributes.GENDER -> grammar.varsGender
            Attributes.NUMBER -> grammar.varsNumber
            Attributes.CASE -> grammar.varsCase
            Attributes.TIME -> grammar.varsTime
            Attributes.PERSON -> grammar.varsPerson
            Attributes.MOOD -> grammar.varsMood
            Attributes.TYPE -> grammar.varsType
            Attributes.VOICE -> grammar.varsVoice
            Attributes.DEGREEOFCOMPARISON -> grammar.varsDegreeOfComparison
        }
        map[grammar.nextIds[option.type]!!] = option
        grammar.nextIds[option.type] = grammar.nextIds[option.type]!! + 1
    }

    override fun deleteOption(grammar: GrammarEntity, option: Characteristic) {
        val map: MutableMap<Int, Characteristic> = when (option.type) {
            Attributes.GENDER -> grammar.varsGender
            Attributes.NUMBER -> grammar.varsNumber
            Attributes.CASE -> grammar.varsCase
            Attributes.TIME -> grammar.varsTime
            Attributes.PERSON -> grammar.varsPerson
            Attributes.MOOD -> grammar.varsMood
            Attributes.TYPE -> grammar.varsType
            Attributes.VOICE -> grammar.varsVoice
            Attributes.DEGREEOFCOMPARISON -> grammar.varsDegreeOfComparison
        }
        map.remove(option.characteristicId)
    }

    override fun updateOption(
        grammar: GrammarEntity,
        optionId: Int,
        newOption: Characteristic
    ): Boolean {
        val map: MutableMap<Int, Characteristic> = when (newOption.type) {
            Attributes.GENDER -> grammar.varsGender
            Attributes.NUMBER -> grammar.varsNumber
            Attributes.CASE -> grammar.varsCase
            Attributes.TIME -> grammar.varsTime
            Attributes.PERSON -> grammar.varsPerson
            Attributes.MOOD -> grammar.varsMood
            Attributes.TYPE -> grammar.varsType
            Attributes.VOICE -> grammar.varsVoice
            Attributes.DEGREEOFCOMPARISON -> grammar.varsDegreeOfComparison
        }
        if (!map.contains(optionId)) return false
        map[optionId] = newOption
        return true
    }

    override fun updateBase(grammar: GrammarEntity, newBase: Int) {
        grammar.base = newBase
    }

    override fun addGrammarRule(grammar: GrammarEntity, rule: GrammarRuleEntity) {
        grammar.grammarRules.add(rule)
    }

    override fun deleteGrammarRule(grammar: GrammarEntity, rule: GrammarRuleEntity): Boolean {
        return grammar.grammarRules.remove(rule)
    }

    override fun addWordFormationRule(grammar: GrammarEntity, rule: WordFormationRuleEntity) {
        grammar.wordFormationRules.add(rule)
    }

    override fun deleteWordFormationRule(
        grammar: GrammarEntity,
        rule: WordFormationRuleEntity
    ): Boolean {
        return grammar.wordFormationRules.remove(rule)
    }

    override fun delMadeByRule(grammar: GrammarEntity, rule: GrammarRuleEntity) {
        val mascHandler = MascDaoImpl()
        for (word in grammar.fullDict) {
            if (mascHandler.fits(rule.masc, word)) {
                var check = true
                for (attr in rule.mutableAttrs.keys) {
                    if (!word.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != word.mutableAttrs[attr]) {
                        check = false
                        break
                    }
                }
                if (!check) continue
                grammar.fullDict.remove(word)
            }
        }
    }
    override fun addMadeByRule(grammar: GrammarEntity, rule: GrammarRuleEntity) {
        val mascHandler = MascDaoImpl()
        val wordHandler = WordDaoImpl()
        for (word in languages[grammar.languageId]!!.dictionary.dict) {
            if (mascHandler.fits(rule.masc, word)) {
                grammar.fullDict.add(wordHandler.grammarTransformByRule(word, rule))
            }
        }
    }
}