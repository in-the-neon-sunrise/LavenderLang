package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.*

data class GrammarRuleEntity(
    override val languageId : Int = 0,
    override var masc : MascEntity = MascEntity(), // тут требования для того чтобы слово подходило
    var mutableAttrs: MutableMap<Attributes, Int> = mutableMapOf(), // то куда мы трансформируемся
    var transformation: TransformationEntity = TransformationEntity()
) : IRuleEntity, Comparable<GrammarRuleEntity> {
    init {
        if (masc.partsOfSpeech == PartOfSpeech.VERB) mutableAttrs[Attributes.ISINFINITIVE] = 1
    }
    override fun compareTo(other: GrammarRuleEntity): Int =
        compareValuesBy(this, other,
            { -it.masc.attrs.size },
            { -it.masc.regex.length })
}