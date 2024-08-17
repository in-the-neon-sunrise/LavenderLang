package com.lavenderlang.domain.usecase.dictionary

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.anggrayudi.storage.extension.launchOnUiThread
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddWordUseCase {
    companion object {
        suspend fun execute(
            dictionary: DictionaryEntity, word: IWordEntity,
            grammarRules: ArrayList<GrammarRuleEntity>, repo: LanguageRepository,
            lifecycleCoroutineScope: LifecycleCoroutineScope
        ) {
            for (letter in word.word) {
                if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                    !MyApp.language!!.consonants.contains(letter.lowercase())) {
                    throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
                }
            }
            if (dictionary.dict.contains(word)) return
            dictionary.dict.add(word)

            lifecycleCoroutineScope.launch(Dispatchers.IO) {
                val mascHandler = MascDaoImpl()
                val ruleHandler = GrammarRuleDaoImpl()
                val py = Python.getInstance()
                val module = py.getModule("pm3")
                val normalForm =
                    module.callAttr("getNormalForm", word.translation.lowercase()).toString()
                val key = "${word.word}:${normalForm}"
                synchronized(dictionary) {
                    dictionary.fullDict[key] = arrayListOf(word)
                    for (rule in grammarRules) {
                        if (!mascHandler.fits(rule.masc, word)) continue
                        dictionary.fullDict[key]!!.add(
                            ruleHandler.grammarTransformByRule(
                                rule,
                                word
                            )
                        )
                    }
                }

                repo.updateDictionary(
                    dictionary.languageId,
                    Serializer.getInstance().serializeDictionary(dictionary)
                )
                Log.d("AddWordUseCase", word.word + " " + word.translation)
            }
        }
    }
}