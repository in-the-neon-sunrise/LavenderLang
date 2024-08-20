package com.lavenderlang.domain.model.word

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech

data class ParticipleEntity( // причастие
    override var languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.TIME to 0,
        Attributes.NUMBER to 0,
        Attributes.GENDER to 0,
        Attributes.CASE to 0),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.TYPE to 0,
        Attributes.VOICE to 0),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.PARTICIPLE
) : IWordEntity