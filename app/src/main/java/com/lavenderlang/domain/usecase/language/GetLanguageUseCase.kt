package com.lavenderlang.domain.usecase.language

import com.lavenderlang.domain.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity

class GetLanguageUseCase {
    companion object {
        suspend fun execute(id: Int, repo: LanguageRepository): LanguageEntity? {
            val item = repo.getLanguage(id)
            if (item != null)
                return Serializer.getInstance().getLanguageEntityFromLanguageItem(item)
            return null
        }
    }
}