package com.lavenderlang.domain.model.help

data class MascEntity (
    var partOfSpeech : PartOfSpeech = PartOfSpeech.NOUN,
    var immutableAttrs : MutableMap<Attributes, Int> = mutableMapOf(),
    var regex : String = ".*"
)