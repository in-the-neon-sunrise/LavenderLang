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
        fun execute(
            dictionary: DictionaryEntity, word: IWordEntity, py: PythonHandler
        ) {
            if (dictionary.dict.contains(word)) {
                synchronized(dictionary) {
                    dictionary.dict.remove(word)
                }
            }
            val normalForm = py.getNormalForm(word.translation.lowercase())
            val key = "${word.word}:${normalForm}"
            synchronized(dictionary) {
                dictionary.fullDict.remove(key)
            }

        }
    }
}