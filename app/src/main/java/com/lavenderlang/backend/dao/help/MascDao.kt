package com.lavenderlang.backend.dao.help

import android.util.Log
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.service.exception.IncorrectRegexException

interface MascDao {
    fun changePartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech)
    fun changeAttribute(masc : MascEntity, attribute : Attributes, ind : Int)
    fun updateRegex(masc : MascEntity, newRegex : String)
    fun fits(masc : MascEntity, word : IWordEntity) : Boolean
}
class MascDaoImpl : MascDao {
    override fun changePartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech) {
        masc.partOfSpeech = partOfSpeech
    }
    override fun changeAttribute(masc : MascEntity, attribute : Attributes, ind : Int) {
        masc.immutableAttrs[attribute] = ind
    }
    override fun updateRegex(masc : MascEntity, newRegex : String) {
        try {
            newRegex.toRegex()
        } catch (e : Exception) {
            throw IncorrectRegexException("Неверное регулярное выражение!")
        }
        masc.regex = newRegex
    }
    override fun fits(masc : MascEntity, word : IWordEntity) : Boolean {
        Log.d("fits", "${masc.immutableAttrs}, word: $word")
        if (masc.partOfSpeech != word.partOfSpeech) return false
        for (attr in masc.immutableAttrs.keys) {
            if (!word.immutableAttrs.containsKey(attr) || word.immutableAttrs[attr] != masc.immutableAttrs[attr]) {
                return false
            }
        }
        Log.d("why god why", "${word.word} ${masc.immutableAttrs}")
        return word.word.matches(masc.regex.toRegex())
    }
}