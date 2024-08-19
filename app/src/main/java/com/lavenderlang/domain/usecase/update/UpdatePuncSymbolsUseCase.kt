package com.lavenderlang.domain.usecase.update

import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository

class UpdatePuncSymbolsUseCase {
    companion object {
        suspend fun execute(id: Int, puncSymbols: MutableMap<String, String>,
                    repo: LanguageRepository
        ) {
            repo.updatePuncSymbols(id, Serializer.getInstance().serializePuncSymbols(puncSymbols))
            }
        }
    }
