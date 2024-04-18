package com.lavenderlang.backend.entity.language

import com.lavenderlang.backend.entity.help.PartOfSpeech

data class LanguageEntity(
    var languageId : Int = 0,
    var name : String = "Введите название",
    var description: String = "Введите описание",
    var dictionary: DictionaryEntity = DictionaryEntity(languageId),
    var grammar : GrammarEntity = GrammarEntity(languageId),
    var vowels : String = "а е ё и о у ы э ю я",
    var consonants: String = "б в г д ж з й к л м н п р с т ф х ц ч ш ъ ь щ",
    var puncSymbols : MutableMap<String, String> = mutableMapOf("." to ".", "," to ",", "!" to "!", "?" to "?",
        ":" to ":", ";" to ";", "\"" to "\"", "(" to "(", ")" to ")", "/" to "/", "\\" to "\\",
        "<" to "<", ">" to ">", "{" to "{", "}" to "}", "[" to "[", "]" to "]", "~" to "~", "—" to "—"),
    var capitalizedPartsOfSpeech : ArrayList<PartOfSpeech> = arrayListOf()
) {
    override fun toString(): String {
        return name
    }
}