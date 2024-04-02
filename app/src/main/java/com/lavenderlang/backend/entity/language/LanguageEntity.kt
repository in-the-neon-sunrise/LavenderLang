package com.lavenderlang.backend.entity.language

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.word.IWordEntityKeyDeserializer
import com.lavenderlang.backend.entity.word.IWordEntityKeySerializer

data class LanguageEntity(
    val languageId : Int = 0,
    var name : String = "Введите название",
    var description: String = "Введите описание",
    var dictionary: DictionaryEntity = DictionaryEntity(languageId),
    var grammar : GrammarEntity = GrammarEntity(languageId),
    var vovels : String = "а е ё и о у ы э ю я",
    var consonants: String = "б в г д ж з й к л м н п р с т ф х ц ч ш ъ ь щ",
    var puncSymbols : ArrayList<String> = arrayListOf(".", ",", "!", "?", ":", ";", "\"", "-", "(",
        ")", "/", "\\,", "<", ">", "{", "}", "[", "]", "~"),
    var capitalizedPartsOfSpeech : ArrayList<PartOfSpeech> = arrayListOf()
) {
    override fun toString(): String {
        return name
    }
}