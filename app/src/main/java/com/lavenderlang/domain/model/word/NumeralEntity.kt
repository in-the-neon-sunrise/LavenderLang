package com.lavenderlang.domain.model.word

import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech

data class NumeralEntity(
    override var languageId : Int = 0,
    override var word : String = "",
    override var translation : String = "",
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.NUMERAL
) : IWordEntity