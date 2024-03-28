package com.lavenderlang.backend.entity.language

data class LanguageEntity(
    val languageId : Int = 0,
    var name : String = "Введите название",
    var description: String = "Введите описание",
    var dictionary: DictionaryEntity = DictionaryEntity(languageId),
    var grammar : GrammarEntity = GrammarEntity(languageId)
) {
    /*init {
        languages[nextLanguageId++] = this
        // а еще сереализация типа... если есть в файле возьми оттуда
    }
     */
}