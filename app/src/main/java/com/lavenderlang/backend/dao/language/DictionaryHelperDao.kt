package com.lavenderlang.backend.dao.language

import android.util.Log
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.entity.language.DictionaryEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.frontend.languages

interface DictionaryHelperDao {
    fun delMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity)
    fun addMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity)
    fun updateMadeByRule(dictionary: DictionaryEntity, oldRule : GrammarRuleEntity, newRule: GrammarRuleEntity)
    fun delMadeByWord(dictionary: DictionaryEntity, word: IWordEntity)
    fun addMadeByWord(dictionary: DictionaryEntity, word: IWordEntity)
    fun updateMadeByWord(dictionary: DictionaryEntity, oldWord: IWordEntity, newWord: IWordEntity)
}
class DictionaryHelperDaoImpl : DictionaryHelperDao {
    override fun delMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) {
        Log.d("delMadeByRule", "rule: $rule")
        val mascHandler = MascDaoImpl()
        val copyDict = HashMap(dictionary.fullDict)
        synchronized(languages) {
            for (key in copyDict.keys) {
                if (languages[dictionary.languageId]!!.dictionary.fullDict[key]!!.size < 2) continue
                val keyWord = languages[dictionary.languageId]!!.dictionary.fullDict[key]!![0]
                if (!mascHandler.fits(rule.masc, keyWord)) continue
                val copyOfWords = ArrayList(dictionary.fullDict[key]!!.subList(1, dictionary.fullDict[key]!!.size))
                for (word in copyOfWords) {
                    var check = true
                    for (attr in rule.mutableAttrs.keys) {
                        if (!word.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != word.mutableAttrs[attr]) {
                            check = false
                            break
                        }
                    }
                    if (!check) continue
                    dictionary.fullDict[key]!!.remove(word)
                }
            }
        }
    }

    override fun addMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) {
        Log.d("addMadeByRule", "rule: $rule")
        val mascHandler = MascDaoImpl()
        val ruleHandler = GrammarRuleDaoImpl()
        val copyDict = HashMap(dictionary.fullDict)
        if (dictionary.languageId !in languages) return
        synchronized(languages) {
            for (key in copyDict.keys) {
                if (languages[dictionary.languageId]!!.dictionary.fullDict[key]!!.isEmpty()) continue
                val keyWord = languages[dictionary.languageId]!!.dictionary.fullDict[key]!![0]
                if (mascHandler.fits(rule.masc, keyWord)) {
                    dictionary.fullDict[key]!!.add(
                        ruleHandler.grammarTransformByRule(
                            rule,
                            keyWord
                        )
                    )
                }
            }
        }
    }

    override fun updateMadeByRule(dictionary: DictionaryEntity, oldRule : GrammarRuleEntity, newRule: GrammarRuleEntity) {
        delMadeByRule(dictionary, oldRule)
        addMadeByRule(dictionary, newRule)
    }

    override fun delMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        val key = "${word.word}:${word.translation}"
        synchronized(languages) {
            dictionary.fullDict.remove(key)
        }
    }

    override fun addMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        val mascHandler = MascDaoImpl()
        val ruleHandler = GrammarRuleDaoImpl()
        val key = "${word.word}:${word.translation}"
        synchronized(languages) {
            dictionary.fullDict[key] = arrayListOf(word)
            for (rule in languages[dictionary.languageId]!!.grammar.grammarRules) {
                if (!mascHandler.fits(rule.masc, word)) continue
                dictionary.fullDict[key]!!.add(ruleHandler.grammarTransformByRule(rule, word))
            }
        }
    }

    override fun updateMadeByWord(dictionary: DictionaryEntity, oldWord: IWordEntity, newWord: IWordEntity) {
        delMadeByWord(dictionary, oldWord)
        addMadeByWord(dictionary, newWord)
    }
}