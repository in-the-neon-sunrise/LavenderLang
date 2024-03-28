package com.lavenderlang.backend.dao.rule

interface PunctuationRuleDao : RuleDao {
    fun updateChar(ruleId : Int, newChar : Char) : Boolean;
    fun updatePosition(ruleId : Int, newPosition : String) : Boolean;
}