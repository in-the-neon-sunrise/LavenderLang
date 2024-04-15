package com.lavenderlang.backend.entity.rule

import com.lavenderlang.backend.entity.help.*

data class GrammarRuleEntity(
    override var languageId : Int = 0,
    override var masc : MascEntity = MascEntity(), // тут требования для того, чтобы слово подходило
    var mutableAttrs: MutableMap<Attributes, Int> = mutableMapOf(), // то, куда мы трансформируемся
    override var transformation: TransformationEntity = TransformationEntity()
) : IRuleEntity {
    init {
        if (masc.partOfSpeech == PartOfSpeech.VERB) mutableAttrs[Attributes.IS_INFINITIVE] = 1
    }
}