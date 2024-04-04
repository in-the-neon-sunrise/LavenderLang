package com.lavenderlang.backend.dao.language

import android.content.Context
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity, context: Context)
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity, context: Context)
    fun createWordsFromExisting(dictionary: DictionaryEntity, word : IWordEntity, context: Context) : ArrayList<IWordEntity>
    fun filterDictByPartOfSpeech(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech) : ArrayList<IWordEntity>
    fun sortDictByWord(dictionary: DictionaryEntity) : ArrayList<IWordEntity>
    fun sortDictByTranslation(dictionary: DictionaryEntity) : ArrayList<IWordEntity>

}
class DictionaryDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
    private val languageRepository: LanguageRepository = LanguageRepository()
) : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word: IWordEntity, context: Context) {
        word.word = word.word.lowercase()
        for (letter in word.word) {
            if (!languages[dictionary.languageId]!!.vowels.contains(letter) &&
                !languages[dictionary.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        dictionary.dict.add(word)
        Thread {
            helper.addMadeByWord(dictionary, word)
            languageRepository.updateLanguage(
                context, dictionary.languageId,
                Serializer.getInstance().serializeLanguage(languages[dictionary.languageId]!!)
            )
        }.start()
    }

    override fun deleteWord(dictionary: DictionaryEntity, word: IWordEntity, context: Context) {
        dictionary.dict.remove(word)
        Thread {
            helper.delMadeByWord(dictionary, word)
            languageRepository.updateLanguage(
                context, dictionary.languageId,
                Serializer.getInstance().serializeLanguage(languages[dictionary.languageId]!!)
            )
        }.start()

    }

    override fun createWordsFromExisting(dictionary: DictionaryEntity, word: IWordEntity, context: Context): ArrayList<IWordEntity> {
        val possibleWords: ArrayList<IWordEntity> = arrayListOf()
        val wfrHandler = WordFormationRuleDaoImpl()
        val mascHandler = MascDaoImpl()
        for (rule in languages[dictionary.languageId]!!.grammar.wordFormationRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            val posWord = wfrHandler.wordFormationTransformByRule(word, rule)
            if (dictionary.fullDict.containsKey("${posWord.word} ${posWord.translation}")) continue
            possibleWords.add(posWord)
            addWord(dictionary, posWord, context)
        }
        return possibleWords
    }

    override fun filterDictByPartOfSpeech(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech): ArrayList<IWordEntity> {
        val filteredDict: ArrayList<IWordEntity> = arrayListOf()
        for (word in dictionary.dict) {
            if (word.partOfSpeech == partOfSpeech) {
                filteredDict.add(word)
            }
        }
        return filteredDict
    }

    override fun sortDictByWord(dictionary: DictionaryEntity): ArrayList<IWordEntity> {
        return dictionary.dict.sortedBy { it.word } as ArrayList<IWordEntity>
    }

    override fun sortDictByTranslation(dictionary: DictionaryEntity): ArrayList<IWordEntity> {
        return dictionary.dict.sortedBy { it.translation } as ArrayList<IWordEntity>
    }
}
