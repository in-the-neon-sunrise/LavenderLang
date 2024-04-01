package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.entity.language.DictionaryEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.languages
import com.lavenderlang.serializer

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
        val mascHandler = MascDaoImpl()
        for (key in dictionary.fullDict.keys) {
            val keyWord = serializer.deserializeWord(key)
            if (!mascHandler.fits(rule.masc, keyWord)) continue
            for (word in dictionary.fullDict[key]!!) {
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

    override fun addMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) {
        val mascHandler = MascDaoImpl()
        val wordHandler = WordDaoImpl()
        for (key in dictionary.fullDict.keys) {
            val keyWord = serializer.deserializeWord(key)
            if (mascHandler.fits(rule.masc, keyWord)) {
                dictionary.fullDict[key]!!.add(wordHandler.grammarTransformByRule(keyWord, rule))
            }
        }
    }

    override fun updateMadeByRule(dictionary: DictionaryEntity, oldRule : GrammarRuleEntity, newRule: GrammarRuleEntity) {
        delMadeByRule(dictionary, oldRule)
        addMadeByRule(dictionary, newRule)
    }

    override fun delMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        val key = serializer.serializeWord(word)
        dictionary.fullDict.remove(key)
    }

    override fun addMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        val mascHandler = MascDaoImpl()
        val wordHandler = WordDaoImpl()
        val key = serializer.serializeWord(word)
        dictionary.fullDict[key] = arrayListOf(word)
        for (rule in languages[dictionary.languageId]!!.grammar.grammarRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            dictionary.fullDict[key]!!.add(wordHandler.grammarTransformByRule(word, rule))
        }
    }

    override fun updateMadeByWord(dictionary: DictionaryEntity, oldWord: IWordEntity, newWord: IWordEntity) {
        delMadeByWord(dictionary, oldWord)
        addMadeByWord(dictionary, newWord)
    }
}