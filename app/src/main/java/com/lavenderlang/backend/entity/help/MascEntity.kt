package com.lavenderlang.backend.entity.help

data class MascEntity (
    var partOfSpeech : PartOfSpeech = PartOfSpeech.NOUN,
    var immutableAttrs : MutableMap<Attributes, ArrayList<Int>> = mutableMapOf(),
    var regex : String = ".*"
)