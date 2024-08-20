package com.lavenderlang.domain.model.rule

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.TransformationEntity

interface IRuleEntity {
    var languageId: Int
    var masc: MascEntity
    var transformation: TransformationEntity
}