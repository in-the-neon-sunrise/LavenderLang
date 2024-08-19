package com.lavenderlang.domain.usecase.dictionary

import android.util.Log
import com.lavenderlang.domain.db.PythonHandler
import com.lavenderlang.domain.fits
import com.lavenderlang.domain.model.help.MascEntity
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.usecase.grammar.GrammarTransformByRuleUseCase

class AddWordUseCase {
    companion object {
        fun execute(
            language: LanguageEntity, word: IWordEntity, py: PythonHandler
        ) {
            if (!language.dictionary.dict.contains(word)) {
                language.dictionary.dict.add(word)
            }

            val normalForm = py.getNormalForm(word.translation.lowercase())
            val key = "${word.word}:${normalForm}"
            synchronized(language.dictionary) {
                language.dictionary.fullDict[key] = arrayListOf(word)
                for (rule in language.grammar.grammarRules) {
                    if (!fits(rule.masc, word)) continue
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
            Log.d("AddWordUseCase", word.word + " " + word.translation)
        }
    }
}
