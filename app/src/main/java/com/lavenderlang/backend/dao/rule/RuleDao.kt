package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.rule.IRuleEntity

interface RuleDao {
    fun updateMasc(rule : IRuleEntity, newMasc : MascEntity)
    fun getOrigInfo(rule: IRuleEntity) : String
    fun getResultInfo(rule: IRuleEntity) : String
}