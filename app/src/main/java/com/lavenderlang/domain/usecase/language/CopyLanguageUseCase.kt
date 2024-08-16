package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class CopyLanguageUseCase {
    companion object {
        suspend fun execute(language: LanguageEntity, id: Int, repo: LanguageRepository) {
            val newLang =
                language.copy(languageId = id, name = language.name + " копия")

            newLang.grammar.languageId = id
            for (rule in newLang.grammar.grammarRules) {
                rule.languageId = id
            }
            for (rule in newLang.grammar.wordFormationRules) {
                rule.languageId = id
            }
            for (word in newLang.dictionary.dict) {
                word.languageId = id
            }
            for (key in newLang.dictionary.fullDict.keys) {
                for (word in newLang.dictionary.fullDict[key]!!) {
                    word.languageId = id
                }
            }
            newLang.dictionary.languageId = id

            repo.insertLanguage(id, newLang)
        }
    }
}