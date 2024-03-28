package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*

interface WordFormationRuleDao : RuleDao {
    fun addAtr(rule: GrammarRuleEntity, attr : Attributes, ind : Int);
    fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int);
    fun updateTransformation(rule : WordFormationRuleEntity, newTransformation: TransformationEntity);
    fun updateDescription(rule : WordFormationRuleEntity, newDescription: String);
}
class WordFormationRuleDaoImpl : WordFormationRuleDao {
    override fun updateMasc(rule: IRuleEntity, newMasc: MascEntity) {
        rule.masc = newMasc
    }

    override fun addAtr(rule: GrammarRuleEntity, attr: Attributes, ind: Int) {
        rule.mutableAttrs[attr] = ind
    }
    override fun deleteAttr(rule: GrammarRuleEntity, attr : Attributes, ind : Int) {
        rule.mutableAttrs.remove(attr)
    }

    override fun updateTransformation(rule: WordFormationRuleEntity, newTransformation: TransformationEntity) {
        rule.transformation = newTransformation
    }

    override fun updateDescription(rule: WordFormationRuleEntity, newDescription: String) {
        rule.description = newDescription
    }
}