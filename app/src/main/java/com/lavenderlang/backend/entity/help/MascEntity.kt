package com.lavenderlang.backend.entity.help

data class MascEntity (
    var partsOfSpeech : ArrayList<PartOfSpeech> = arrayListOf(
        PartOfSpeech.NOUN,
        PartOfSpeech.VERB,
        PartOfSpeech.ADJECTIVE,
        PartOfSpeech.ADVERB,
        PartOfSpeech.PARTICIPLE,
        PartOfSpeech.VERBPARTICIPLE,
        PartOfSpeech.PRONOUN,
        PartOfSpeech.NUMERAL,
        PartOfSpeech.FUNCPART
    ),
    var attrs : MutableMap<Attributes, ArrayList<Int>> = mutableMapOf(), // постоянные
    var regex : String = ""
)