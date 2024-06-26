package com.lavenderlang.backend.entity.word

import com.lavenderlang.backend.entity.help.*

data class VerbParticipleEntity( // деепричастие
    override var languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(
        Attributes.TYPE to 0),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.VERB_PARTICIPLE
) : IWordEntity