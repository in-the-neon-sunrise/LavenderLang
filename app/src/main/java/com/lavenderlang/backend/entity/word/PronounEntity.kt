package com.lavenderlang.backend.entity.word

import com.lavenderlang.backend.entity.help.*

data class PronounEntity(
    override val languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.NUMBER to 0,
        Attributes.CASE to 0),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.GENDER to 0),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.PRONOUN
) : IWordEntity