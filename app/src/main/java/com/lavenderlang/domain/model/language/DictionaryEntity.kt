package com.lavenderlang.domain.model.language

import com.lavenderlang.domain.model.word.IWordEntity

data class DictionaryEntity (
    var languageId : Int = 0,
    var dict : ArrayList<IWordEntity> = arrayListOf(),
    val fullDict: MutableMap<String, ArrayList<IWordEntity>> = mutableMapOf()
)