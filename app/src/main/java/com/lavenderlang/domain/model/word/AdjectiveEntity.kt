package com.lavenderlang.domain.model.word

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech

data class AdjectiveEntity(
    override var languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.GENDER to 0,
        Attributes.NUMBER to 0,
        Attributes.CASE to 0,
        Attributes.DEGREE_OF_COMPARISON to 0),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.ADJECTIVE
) : IWordEntity