package com.lavenderlang.domain.model.rule

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity

data class WordFormationRuleEntity(
    override var languageId : Int = 0,
    override var masc : MascEntity = MascEntity(), // требования к оригинальному слову
    var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf(), // что хотим получить
    override var transformation: TransformationEntity = TransformationEntity(),
    var description: String = "",
    var partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN
): IRuleEntity