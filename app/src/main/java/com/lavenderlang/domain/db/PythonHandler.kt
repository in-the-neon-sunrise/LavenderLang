package com.lavenderlang.domain.db

import com.lavenderlang.domain.model.help.PartOfSpeech

interface PythonHandler {
    fun inflectAttrs(word: String, partOfSpeech: String, attrs: String) : String
    fun getNormalForm(word: String) : String
}