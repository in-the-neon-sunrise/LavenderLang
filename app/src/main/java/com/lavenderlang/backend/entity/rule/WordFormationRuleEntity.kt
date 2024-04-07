package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.*

data class WordFormationRuleEntity(
    override var languageId : Int = 0,
    override var masc : MascEntity = MascEntity(), // а тут требования все еще
    var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf(), // что хотим получить
    override var transformation: TransformationEntity = TransformationEntity(),
    var description: String = ""
): IRuleEntity