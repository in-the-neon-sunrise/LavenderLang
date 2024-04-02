package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.TransformationEntity

interface IRuleEntity {
    val languageId: Int
    var masc: MascEntity
    var transformation: TransformationEntity
}