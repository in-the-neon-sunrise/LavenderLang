package com.lavenderlang.domain.usecase.language

import android.util.Log
import com.lavenderlang.domain.db.LanguageIdAndName
import com.lavenderlang.domain.db.LanguageRepository

class GetShortLanguagesUseCase {
    companion object {
        suspend fun execute(repo: LanguageRepository) : List<LanguageIdAndName> {
            val res = repo.getShortLanguageItems()
            Log.d("GetShortLanguagesUseCase", "res: $res")
            return res
        }
    }
}