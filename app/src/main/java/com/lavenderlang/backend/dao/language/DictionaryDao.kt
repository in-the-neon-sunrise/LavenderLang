package com.lavenderlang.backend.dao.language

import android.util.Log
import com.chaquo.python.Python
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun createWordsFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : List<Pair<String, IWordEntity>>
    fun filterDictByPartOfSpeech(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech) : List<IWordEntity>
    fun sortDictByWord(dictionary: DictionaryEntity) : List<IWordEntity>
    fun sortDictByTranslation(dictionary: DictionaryEntity) : List<IWordEntity>
    fun sortDictByWordFiltered(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech) : List<IWordEntity>
    fun sortDictByTranslationFiltered(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech) : List<IWordEntity>
    fun getWordForms(dictionary: DictionaryEntity, word: String) : ArrayList<IWordEntity>

}
class DictionaryDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
    private val languageRepository: LanguageRepository = LanguageRepository()
) : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word: IWordEntity) {
        for (letter in word.word) {
            if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                !MyApp.language!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        if (dictionary.dict.contains(word)) return
        dictionary.dict.add(word)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            helper.addMadeByWord(dictionary, word)
            languageRepository.updateDictionary(
                MyApp.getInstance().applicationContext, dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
            Log.d("updated word", word.word+" "+word.translation)
        }
    }

    override fun deleteWord(dictionary: DictionaryEntity, word: IWordEntity) {
        dictionary.dict.remove(word)
        MyApp.lifecycleScope!!.launch(Dispatchers.IO) {
            helper.delMadeByWord(dictionary, word)
            languageRepository.updateDictionary(
                MyApp.getInstance().applicationContext, dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
        }
    }

    override fun createWordsFromExisting(dictionary: DictionaryEntity, word: IWordEntity): List<Pair<String, IWordEntity>> {
        val possibleWords: ArrayList<Pair<String, IWordEntity>> = arrayListOf()
        val wfrHandler = WordFormationRuleDaoImpl()
        val mascHandler = MascDaoImpl()
        for (rule in MyApp.language!!.grammar.wordFormationRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            val posWord = wfrHandler.wordFormationTransformByRule(word, rule)
            if (dictionary.fullDict.containsKey("${posWord.word} ${posWord.translation}")) continue
            possibleWords.add(Pair(rule.description, posWord))
        }
        return possibleWords
    }

    override fun filterDictByPartOfSpeech(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech): List<IWordEntity> {
        val filteredDict: ArrayList<IWordEntity> = arrayListOf()
        for (word in dictionary.dict) {
            if (word.partOfSpeech == partOfSpeech) {
                filteredDict.add(word)
            }
        }
        return filteredDict
    }

    override fun sortDictByWord(dictionary: DictionaryEntity): List<IWordEntity> {
        return dictionary.dict.sortedBy { it.word }
    }

    override fun sortDictByTranslation(dictionary: DictionaryEntity): List<IWordEntity> {
        return dictionary.dict.sortedBy { it.translation }
    }

    override fun sortDictByWordFiltered(
        dictionary: DictionaryEntity,
        partOfSpeech: PartOfSpeech
    ): List<IWordEntity> {
        val filteredDict: ArrayList<IWordEntity> = arrayListOf()
        for (word in dictionary.dict) {
            if (word.partOfSpeech == partOfSpeech) {
                filteredDict.add(word)
            }
        }
        return filteredDict.sortedBy { it.word }
    }

    override fun sortDictByTranslationFiltered(
        dictionary: DictionaryEntity,
        partOfSpeech: PartOfSpeech
    ): List<IWordEntity> {
        val filteredDict: ArrayList<IWordEntity> = arrayListOf()
        for (word in dictionary.dict) {
            if (word.partOfSpeech == partOfSpeech) {
                filteredDict.add(word)
            }
        }
        return filteredDict.sortedBy { it.translation }
    }


    override fun getWordForms(dictionary: DictionaryEntity, word: String): ArrayList<IWordEntity> {
        for (key in dictionary.fullDict.keys) {
            if (key.split(":")[0] == word) return dictionary.fullDict[key]!!
        }
        return arrayListOf()
    }
}
