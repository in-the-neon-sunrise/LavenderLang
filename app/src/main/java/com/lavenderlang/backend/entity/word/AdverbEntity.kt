package com.lavenderlang.backend.entity.word

import com.lavenderlang.backend.entity.help.*

data class AdverbEntity( // наречие
    override var languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(Attributes.DEGREE_OF_COMPARISON to 0),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.ADVERB
) : IWordEntity