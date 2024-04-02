package com.lavenderlang.backend.dao.rule

import android.content.Context
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.rule.IRuleEntity

interface RuleDao {
    fun updateMasc(rule : IRuleEntity, newMasc : MascEntity, context: Context)
}