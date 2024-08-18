package com.lavenderlang.domain.usecase.dictionary

import androidx.lifecycle.LifecycleCoroutineScope
import com.chaquo.python.Python
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteWordUseCase {
    companion object {
        suspend fun execute(
            dictionary: DictionaryEntity, word: IWordEntity, repo: LanguageRepository, py: PythonHandler
        ) {
            val normalForm = py.getNormalForm(word.translation.lowercase())
            val key = "${word.word}:${normalForm}"
            synchronized(dictionary) {
                dictionary.fullDict.remove(key)
            }

            repo.updateDictionary(
                dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
        }
    }
}