package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.MascEntity

data class PunctuationRuleEntity(
    override val languageId : Int = 0,
    override var masc : MascEntity = MascEntity(),
    var sign : Char = ' ',
    var position: String = ""
) : IRuleEntity