package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.MascEntity

interface IRuleEntity {
    val languageId : Int
    var masc: MascEntity
}