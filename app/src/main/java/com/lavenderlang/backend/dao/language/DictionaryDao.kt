package com.lavenderlang.backend.dao.language

import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun createWordsFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : ArrayList<IWordEntity>
    fun filterDictByPartOfSpeech(dictionary: DictionaryEntity) : ArrayList<IWordEntity>
    fun sortDictByWord(dictionary: DictionaryEntity) : ArrayList<IWordEntity>
    fun sortDictByTranslation(dictionary: DictionaryEntity) : ArrayList<IWordEntity>

}
class DictionaryDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl()) : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word: IWordEntity) {
        for (letter in word.word) {
            /*if (!languages[dictionary.languageId]!!.letters.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }*/
        }
        dictionary.dict.add(word)
        helper.addMadeByWord(dictionary, word)
    }

    override fun deleteWord(dictionary: DictionaryEntity, word: IWordEntity) {
        dictionary.dict.remove(word)
        helper.delMadeByWord(dictionary, word)
    }

    override fun createWordsFromExisting(dictionary: DictionaryEntity, word: IWordEntity): ArrayList<IWordEntity> {
        val possibleWords: ArrayList<IWordEntity> = arrayListOf()
        val wfrHandler = WordFormationRuleDaoImpl()
        val mascHandler = MascDaoImpl()
        for (rule in languages[dictionary.languageId]!!.grammar.wordFormationRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            val posWord = wfrHandler.wordFormationTransformByRule(word, rule)
            if (dictionary.fullDict.containsKey(Serializer.getInstance().serializeWord(posWord))) continue
            possibleWords.add(posWord)
            addWord(dictionary, posWord)
        }
        return possibleWords
    }

    override fun filterDictByPartOfSpeech(dictionary: DictionaryEntity): ArrayList<IWordEntity> {
        TODO("Not yet implemented")
    }

    override fun sortDictByWord(dictionary: DictionaryEntity): ArrayList<IWordEntity> {
        TODO("Not yet implemented")
    }

    override fun sortDictByTranslation(dictionary: DictionaryEntity): ArrayList<IWordEntity> {
        TODO("Not yet implemented")
    }
}
