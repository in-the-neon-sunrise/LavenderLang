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
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.usecase.grammar.GrammarTransformByRuleUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddWordUseCase {
    companion object {
        suspend fun execute(
            language: LanguageEntity, word: IWordEntity, repo: LanguageRepository, py: PythonHandler) {

                val mascHandler = MascDaoImpl()
                val normalForm = py.getNormalForm(word.translation.lowercase())
                val key = "${word.word}:${normalForm}"
                synchronized(language.dictionary) {
                    language.dictionary.fullDict[key] = arrayListOf(word)
                    for (rule in language.grammar.grammarRules) {
                        if (!mascHandler.fits(rule.masc, word)) continue
                        language.dictionary.fullDict[key]!!.add(
                            GrammarTransformByRuleUseCase.execute(
                                rule,
                                word,
                                language,
                                py
                            )
                        )
                    }
                }

                repo.updateDictionary(
                    language.dictionary.languageId,
                    Serializer.getInstance().serializeDictionary(language.dictionary)
                )
                Log.d("AddWordUseCase", word.word + " " + word.translation)
            }
        }
    }
