package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface DictionaryDao {
    fun addWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun deleteWord(dictionary: DictionaryEntity, word : IWordEntity)
    fun createWordsFromExisting(dictionary: DictionaryEntity, word : IWordEntity) : ArrayList<Pair<String, IWordEntity>>
    fun filterDictByPartOfSpeech(dictionary: DictionaryEntity, partOfSpeech: PartOfSpeech) : ArrayList<IWordEntity>
    fun sortDictByWord(dictionary: DictionaryEntity) : ArrayList<IWordEntity>
    fun sortDictByTranslation(dictionary: DictionaryEntity) : ArrayList<IWordEntity>
    fun getWordForms(dictionary: DictionaryEntity, word: String) : ArrayList<IWordEntity>

}
class DictionaryDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
    private val languageRepository: LanguageRepository = LanguageRepository()
) : DictionaryDao {
    override fun addWord(dictionary: DictionaryEntity, word: IWordEntity) {
        for (letter in word.word) {
            if (!languages[dictionary.languageId]!!.vowels.contains(letter.lowercase()) &&
                !languages[dictionary.languageId]!!.consonants.contains(letter.lowercase())) {
                throw ForbiddenSymbolsException("Буква $letter не находится в алфавите языка!")
            }
        }
        if (dictionary.dict.contains(word)) return
        dictionary.dict.add(word)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.addMadeByWord(dictionary, word)
            languageRepository.updateDictionary(
                MainActivity.getInstance(), dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
        }
    }

    override fun deleteWord(dictionary: DictionaryEntity, word: IWordEntity) {
        dictionary.dict.remove(word)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.delMadeByWord(dictionary, word)
            languageRepository.updateDictionary(
                MainActivity.getInstance(), dictionary.languageId,
                Serializer.getInstance().serializeDictionary(dictionary)
            )
        }
    }

    override fun createWordsFromExisting(dictionary: DictionaryEntity, word: IWordEntity): ArrayList<Pair<String, IWordEntity>> {
        if (dictionary.languageId !in languages) return arrayListOf()
        val possibleWords: ArrayList<Pair<String, IWordEntity>> = arrayListOf()
        val wfrHandler = WordFormationRuleDaoImpl()
        val mascHandler = MascDaoImpl()
        for (rule in languages[dictionary.languageId]!!.grammar.wordFormationRules) {
            if (!mascHandler.fits(rule.masc, word)) continue
            val posWord = wfrHandler.wordFormationTransformByRule(word, rule)
            if (dictionary.fullDict.containsKey("${posWord.word} ${posWord.translation}")) continue
            possibleWords.add(Pair(rule.description, posWord))
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

    override fun getWordForms(dictionary: DictionaryEntity, word: String): ArrayList<IWordEntity> {
        for (key in dictionary.fullDict.keys) {
            if (key.split(" ")[0] == word) return dictionary.fullDict[key]!!
        }
        return arrayListOf()
    }
}
