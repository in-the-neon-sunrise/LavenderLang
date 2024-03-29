package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.languages

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity);
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity);
    fun createWordFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : ArrayList<IWordEntity>;
}
class DictionaryDaoImpl : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word : IWordEntity) {
        dictionary.dict.add(word)
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
}