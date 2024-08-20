package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageRepository

class DeleteLanguageUseCase {
    companion object {
        suspend fun execute(id: Int, repo: LanguageRepository) {
            repo.deleteLanguage(id)
        }
    }
}