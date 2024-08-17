package com.lavenderlang.domain.usecase.dictionary

import androidx.lifecycle.LifecycleCoroutineScope
import com.chaquo.python.Python
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteWordUseCase {
    companion object {
        suspend fun execute(
            dictionary: DictionaryEntity, word: IWordEntity,
            repo: LanguageRepository, lifecycleCoroutineScope: LifecycleCoroutineScope
        ) {
            dictionary.dict.remove(word)
            lifecycleCoroutineScope.launch(Dispatchers.IO) {
                val py = Python.getInstance()
                val module = py.getModule("pm3")
                val normalForm =
                    module.callAttr("getNormalForm", word.translation.lowercase()).toString()
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
}