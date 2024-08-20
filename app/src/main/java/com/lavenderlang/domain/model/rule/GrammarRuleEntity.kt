package com.lavenderlang.domain.model.rule

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.help.TransformationEntity

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