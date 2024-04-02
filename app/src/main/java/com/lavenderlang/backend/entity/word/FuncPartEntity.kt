package com.lavenderlang.backend.entity.word

import com.lavenderlang.backend.entity.help.*

data class FuncPartEntity(
    override val languageId : Int,
    override var word : String,
    override var translation : String,
    override var mutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    override var partOfSpeech: PartOfSpeech = PartOfSpeech.FUNC_PART
) : IWordEntity