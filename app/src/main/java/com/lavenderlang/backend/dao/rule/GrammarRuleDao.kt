package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.dao.language.DictionaryHelperDaoImpl
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.languages

interface GrammarRuleDao : RuleDao {
    fun addAtr(rule: GrammarRuleEntity, attr : Attributes, ind : Int)
    fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int)
    fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity)
    fun updateMutableAttrs(rule : GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>)
}
class GrammarRuleDaoImpl : GrammarRuleDao {
    override fun updateMasc(rule : IRuleEntity, newMasc : MascEntity) {
        val oldRule = (rule as GrammarRuleEntity).copy()
        rule.masc = newMasc
        DictionaryHelperDaoImpl().updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
    }
    override fun addAtr(rule: GrammarRuleEntity, attr : Attributes, ind : Int) {
        val oldRule = rule.copy()
        rule.mutableAttrs[attr] = ind
        DictionaryHelperDaoImpl().updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)

    }
    override fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int) {
        val oldRule = rule.copy()
        rule.mutableAttrs.remove(attr)
        DictionaryHelperDaoImpl().updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)

    }
    override fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity) {
        val oldRule = rule.copy()
        rule.transformation = newTransformation
        DictionaryHelperDaoImpl().updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
    }

    override fun updateMutableAttrs(rule: GrammarRuleEntity, newAttrs: MutableMap<Attributes, Int>
    ) {
        val oldRule = rule.copy()
        rule.mutableAttrs = newAttrs
        DictionaryHelperDaoImpl().updateMadeByRule(languages[rule.languageId]!!.dictionary, oldRule, rule)
    }
}