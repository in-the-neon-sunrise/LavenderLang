package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class CreateLanguageUseCase {
    companion object {
        suspend fun execute(name: String, description: String, repo: LanguageRepository, id: Int) : LanguageEntity {
            val newLang = LanguageEntity(id, name, description)
            repo.insertLanguage(
                newLang.languageId, newLang
            )
            return newLang
        }
    }
}