package com.lavenderlang.backend.dao.language

import android.content.Context
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.languages
import com.lavenderlang.serializer

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity);
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity);
    fun createWordFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : ArrayList<IWordEntity>;
    fun delMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity);
    fun addMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity);
    fun updateMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) : Boolean;
    fun delMadeByWord(dictionary: DictionaryEntity, word: IWordEntity);
    fun addMadeByWord(dictionary: DictionaryEntity, word: IWordEntity);
    fun updateMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) : Boolean;
}
class DictionaryDaoImpl : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word : IWordEntity) {
        dictionary.dict.add(word)
        dictionary.fullDict[serializer.serializeWord(word)] = arrayListOf(word)
        //Пофиксить строку сверху!!ааа
    }
    override fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity) {
        dictionary.dict.remove(word)
    }
    override fun createWordFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : ArrayList<IWordEntity> {
        val possibleWords : ArrayList<IWordEntity> = arrayListOf()
        val wordHandler = WordDaoImpl()
        val mascHandler = MascDaoImpl()
        for (rule in languages[dictionary.languageId]!!.grammar.wordFormationRules) {
            if (mascHandler.fits(rule.masc, word)) {
                possibleWords.add(wordHandler.wordFormationTransformByRule(word, rule))
            }
        }
        return possibleWords
    }

    override fun delMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) {
        val mascHandler = MascDaoImpl()
        for (word in dictionary.fullDict.keys) {
            if (mascHandler.fits(rule.masc, serializer.deserializeWord(word))) {
                for (w in dictionary.fullDict[word]!!) {
                    var check = true
                    for (attr in rule.mutableAttrs.keys) {
                        if (!w.mutableAttrs.contains(attr) || rule.mutableAttrs[attr] != w.mutableAttrs[attr]) {
                            check = false
                            break
                        }
                    }
                    if (!check) continue
                    dictionary.fullDict[word]!!.remove(w)
                }

            }
        }
    }
    override fun addMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity) {
        val mascHandler = MascDaoImpl()
        val wordHandler = WordDaoImpl()
        for (word in dictionary.dict) {
            if (mascHandler.fits(rule.masc, word)) {
                TODO()
            }
        }
    }

    override fun updateMadeByRule(dictionary: DictionaryEntity, rule: GrammarRuleEntity): Boolean {
        TODO()
    }

    override fun delMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        TODO("Not yet implemented")
    }

    override fun addMadeByWord(dictionary: DictionaryEntity, word: IWordEntity) {
        TODO("Not yet implemented")
    }

    override fun updateMadeByWord(dictionary: DictionaryEntity, word: IWordEntity): Boolean {
        TODO("Not yet implemented")
    }
}