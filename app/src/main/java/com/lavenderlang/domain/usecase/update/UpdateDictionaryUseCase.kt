package com.lavenderlang.domain.usecase.update

import com.lavenderlang.domain.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.DictionaryEntity

class UpdateDictionaryUseCase {
    companion object {
        suspend fun execute(dictionary: DictionaryEntity, repo: LanguageRepository) {
            repo.updateDictionary(
                dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
        }
    }
}