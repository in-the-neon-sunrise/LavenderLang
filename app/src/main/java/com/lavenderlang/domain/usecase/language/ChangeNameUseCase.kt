package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class ChangeNameUseCase {
    companion object {
        suspend fun execute(language: LanguageEntity, newName: String, repo: LanguageRepository) {
            repo.updateName(language.languageId, newName)
        }
    }
}