package com.lavenderlang.backend.entity.language

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.lavenderlang.backend.entity.word.IWordEntityKeyDeserializer
import com.lavenderlang.backend.entity.word.IWordEntityKeySerializer

data class LanguageEntity(
    val languageId : Int = 0,
    var name : String = "Введите название",
    var description: String = "Введите описание",

    @JsonSerialize(keyUsing = IWordEntityKeySerializer::class)
    @JsonDeserialize(keyUsing = IWordEntityKeyDeserializer::class)
    var dictionary: DictionaryEntity = DictionaryEntity(languageId),
    var grammar : GrammarEntity = GrammarEntity(languageId),
    var letters : String = "а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я",
    var puncSymbols : String = ". , ! ? : ; ' \" -"
) {
    /*init {
        languages[nextLanguageId++] = this
        // а еще сереализация типа... если есть в файле возьми оттуда
    }
     */
    override fun toString(): String {
        return name
    }
}