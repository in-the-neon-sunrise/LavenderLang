package com.lavenderlang.backend.entity.help

data class MascEntity (
    var partsOfSpeech : PartOfSpeech = PartOfSpeech.NOUN,
    var attrs : MutableMap<Attributes, ArrayList<Int>> = mutableMapOf(), // постоянные
    var regex : String = ""
)