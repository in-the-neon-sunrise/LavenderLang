package com.lavenderlang.backend.service

import com.lavenderlang.backend.entity.help.PartOfSpeech

data class ResultAttrs(
    var partOfSpeech : PartOfSpeech = PartOfSpeech.NOUN,
    var inf : String = "",
    var mutableAttrs : ArrayList<Int> = arrayListOf(),
    var immutableAttrs : ArrayList<Int> = arrayListOf()
)