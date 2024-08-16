package com.lavenderlang.backend.dao.rule

import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.rule.IRuleEntity

interface RuleDao {
    fun updateMasc(rule : IRuleEntity, newMasc : MascEntity)
    fun getOrigInfo(rule: IRuleEntity) : String
    fun getResultInfo(rule: IRuleEntity) : String
}