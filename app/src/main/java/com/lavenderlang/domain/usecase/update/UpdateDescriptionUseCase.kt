package com.lavenderlang.domain.usecase.update

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class UpdateDescriptionUseCase {
    companion object {
        suspend fun execute(id: Int, newDescription: String, repo: LanguageRepository) {
            repo.updateDescription(id, newDescription)
        }
    }
}