package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class ChangeDescriptionUseCase {
    companion object {
        suspend fun execute(language: LanguageEntity, newDescription: String, repo: LanguageRepository) {
            repo.updateDescription(language.languageId, newDescription)
        }
    }
}