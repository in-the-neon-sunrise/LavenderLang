package com.lavenderlang.backend.dao.help

import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.service.exception.IncorrectRegexException

interface MascDao {
    fun changePartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech)
    fun addAttribute(masc : MascEntity, attribute : Attributes, ind : Int)
    fun deleteAttribute(masc: MascEntity, attribute: Attributes, ind : Int) : Boolean
    fun updateRegex(masc : MascEntity, newRegex : String)
    fun fits(masc : MascEntity, word : IWordEntity) : Boolean
}
class MascDaoImpl : MascDao {
    override fun changePartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech) {
        masc.partOfSpeech = partOfSpeech
    }
    override fun addAttribute(masc : MascEntity, attribute : Attributes, ind : Int) {
        if (masc.immutableAttrs.contains(attribute)) {
            masc.immutableAttrs[attribute]!!.add(ind)
        } else {
            masc.immutableAttrs[attribute] = arrayListOf(ind)
        }
    }
    override fun deleteAttribute(masc: MascEntity, attribute : Attributes, ind : Int) : Boolean {
        if (!masc.immutableAttrs.contains(attribute)) return false
        return masc.immutableAttrs[attribute]!!.remove(ind)
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
        if (masc.partOfSpeech != word.partOfSpeech) return false
        for (attr in masc.immutableAttrs.keys) {
            //if (attr == Attributes.IS_INFINITIVE) continue
            var check = false
            for (ind in masc.immutableAttrs[attr]!!) {
                if (word.immutableAttrs.contains(attr)) {
                    check = true
                    break
                }
            }
            if (!check) return false
        }
        return word.word.matches(masc.regex.toRegex())
    }
}