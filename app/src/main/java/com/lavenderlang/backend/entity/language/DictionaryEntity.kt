package com.lavenderlang.backend.entity.language

import com.lavenderlang.backend.entity.word.IWordEntity

data class DictionaryEntity (
    var languageId : Int = 0,
    var dict : ArrayList<IWordEntity> = arrayListOf(),
    val fullDict: MutableMap<String, ArrayList<IWordEntity>> = mutableMapOf()
)