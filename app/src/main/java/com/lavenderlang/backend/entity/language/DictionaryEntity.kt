package com.lavenderlang.backend.entity.language

import com.lavenderlang.backend.entity.word.IWordEntity

data class DictionaryEntity (
    val languageId : Int = 0,
    var nextWordId : Int = 0,
    var dict : ArrayList<IWordEntity> = arrayListOf()
)