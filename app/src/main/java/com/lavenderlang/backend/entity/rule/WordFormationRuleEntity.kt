package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.*

data class WordFormationRuleEntity(
    override val languageId : Int = 0,
    override var masc : MascEntity = MascEntity(), // а тут требования все еще
    var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf(), // что хотим получить
    var transformation: TransformationEntity = TransformationEntity(),
    var description: String = ""
) : IRuleEntity, Comparable<WordFormationRuleEntity> {
    override fun compareTo(other: WordFormationRuleEntity): Int = compareValuesBy(this, other,
        { it.masc.partsOfSpeech.size } , { -it.masc.attrs.size }, { -it.masc.regex.length })
}