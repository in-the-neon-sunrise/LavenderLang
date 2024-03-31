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
}

class GrammarDaoImpl(val wordHandler : WordDaoImpl = WordDaoImpl()) : GrammarDao {
    override fun addOption(grammar: GrammarEntity, option: Characteristic) {
        when (option.type) {
            Attributes.GENDER -> grammar.varsGender[grammar.nextIds[option.type]!!] = option
            Attributes.NUMBER -> grammar.varsNumber[grammar.nextIds[option.type]!!] = option
            Attributes.CASE -> grammar.varsCase[grammar.nextIds[option.type]!!] = option
            Attributes.TIME -> grammar.varsTime[grammar.nextIds[option.type]!!] = option
            Attributes.PERSON -> grammar.varsPerson[grammar.nextIds[option.type]!!] = option
            Attributes.MOOD -> grammar.varsMood[grammar.nextIds[option.type]!!] = option
            Attributes.TYPE -> grammar.varsType[grammar.nextIds[option.type]!!] = option
            Attributes.VOICE -> grammar.varsVoice[grammar.nextIds[option.type]!!] = option
            Attributes.DEGREEOFCOMPARISON -> grammar.varsDegreeOfComparison[grammar.nextIds[option.type]!!] = option
            Attributes.ISINFINITIVE -> return
        }
        grammar.nextIds[option.type] = grammar.nextIds[option.type]!! + 1
    }

    override fun deleteOption(grammar: GrammarEntity, option: Characteristic) {
        when (option.type) {
            Attributes.GENDER -> grammar.varsGender.remove(option.characteristicId)
            Attributes.NUMBER -> grammar.varsNumber.remove(option.characteristicId)
            Attributes.CASE -> grammar.varsCase.remove(option.characteristicId)
            Attributes.TIME -> grammar.varsTime.remove(option.characteristicId)
            Attributes.PERSON -> grammar.varsPerson.remove(option.characteristicId)
            Attributes.MOOD -> grammar.varsMood.remove(option.characteristicId)
            Attributes.TYPE -> grammar.varsType.remove(option.characteristicId)
            Attributes.VOICE -> grammar.varsVoice.remove(option.characteristicId)
            Attributes.DEGREEOFCOMPARISON -> grammar.varsDegreeOfComparison.remove(option.characteristicId)
            Attributes.ISINFINITIVE -> return
        }
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
            Attributes.ISINFINITIVE -> return false
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
}