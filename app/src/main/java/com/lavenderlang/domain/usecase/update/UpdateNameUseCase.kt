package com.lavenderlang.domain.usecase.update

import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class UpdateNameUseCase {
    companion object {
        suspend fun execute(id: Int, newName: String, repo: LanguageRepository) {
            repo.updateName(id, newName)
        }
    }
}