package com.lavenderlang.domain.usecase

import androidx.lifecycle.LifecycleCoroutineScope
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdatePuncSymbolUseCase {
    companion object {
        fun execute(language: LanguageEntity, id: Int, newSymbol: String,
                    repo: LanguageRepositoryImpl, lifecycleCoroutineScope: LifecycleCoroutineScope) {
            //check if symbol is in language
            for (letter in newSymbol) {
                if (MyApp.language!!.vowels.contains(letter.lowercase()) ||
                    MyApp.language!!.consonants.contains(letter.lowercase())
                ) throw ForbiddenSymbolsException("Буква $letter находится в алфавите языка!")
            }
            language.puncSymbols[language.puncSymbols.keys.toList()[id]] = newSymbol
            lifecycleCoroutineScope.launch(Dispatchers.IO) {
                repo.updatePuncSymbols(language.languageId,
                    Serializer.getInstance().serializePuncSymbols(language.puncSymbols))
            }
        }
    }
}