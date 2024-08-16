package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.db.LanguageIdAndName
import com.lavenderlang.domain.db.LanguageRepository

class GetShortLanguagesUseCase {
    companion object {
        suspend fun execute(repo: LanguageRepository) : List<LanguageIdAndName> {
            return repo.getShortLanguageItems()
        }
    }
}