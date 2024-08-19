package com.lavenderlang.domain.usecase.update

import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.help.PartOfSpeech

class UpdateWritingUseCase {
    companion object {
        suspend fun execute(vowels: String, consonants: String, capitalizedPartsOfSpeech: ArrayList<PartOfSpeech>,
                            id: Int, repo: LanguageRepository) {
            repo.updateVowels(id, vowels)
            repo.updateConsonants(id, consonants)
            repo.updateCapitalizedPartsOfSpeech(id, Serializer.getInstance().serializeCapitalizedPartsOfSpeech(capitalizedPartsOfSpeech))
        }
    }
}