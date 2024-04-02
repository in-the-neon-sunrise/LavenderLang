package com.lavenderlang.backend.entity.word

import com.lavenderlang.backend.entity.help.*

data class ParticipleEntity( // причастие
    override val languageId : Int = 0,
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