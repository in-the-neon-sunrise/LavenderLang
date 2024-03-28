package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*

interface GrammarRuleDao : RuleDao {
    fun addAtr(rule: GrammarRuleEntity, attr : Attributes, ind : Int);
    fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int);
    fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity);
}
class GrammarRuleDaoImpl : GrammarRuleDao {
    override fun updateMasc(rule : IRuleEntity, newMasc : MascEntity) {
        rule.masc = newMasc
    }
    override fun addAtr(rule: GrammarRuleEntity, attr : Attributes, ind : Int) {
        rule.mutableAttrs[attr] = ind
    }
    override fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int) {
        rule.mutableAttrs.remove(attr)
    }
    override fun updateTransformation(rule : GrammarRuleEntity, newTransformation: TransformationEntity) {
        rule.transformation = newTransformation
    }
}