package com.lavenderlang.backend.dao.help

import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.word.IWordEntity

// всякая нормальная редактура masc, position, transformation, etc.
interface MascDao {
    fun addPartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech);
    fun deletePartOfSpeech(masc : MascEntity, partOfSpeech : PartOfSpeech) : Boolean;
    fun addAttribute(masc : MascEntity, attribute : Attributes, ind : Int);
    fun deleteAttribute(masc: MascEntity, attribute: Attributes, ind : Int) : Boolean;
    fun updateRegex(masc : MascEntity, newRegex : String);
    fun fits(masc : MascEntity, word : IWordEntity) : Boolean;
}
class MascDaoImpl : MascDao {
    override fun addPartOfSpeech(masc : MascEntity, partOfSpeech: PartOfSpeech) {
        masc.partsOfSpeech.add(partOfSpeech)
    }
    override fun deletePartOfSpeech(masc : MascEntity, partOfSpeech : PartOfSpeech) : Boolean {
        return masc.partsOfSpeech.remove(partOfSpeech)
    }
    override fun addAttribute(masc : MascEntity, attribute : Attributes, ind : Int) {
        if (masc.attrs.contains(attribute)) {
            masc.attrs[attribute]!!.add(ind)
        } else {
            masc.attrs[attribute] = arrayListOf(ind)
        }
    }
    override fun deleteAttribute(masc: MascEntity, attribute : Attributes, ind : Int) : Boolean {
        if (!masc.attrs.contains(attribute)) return false
        return masc.attrs[attribute]!!.remove(ind)
    }
    override fun updateRegex(masc : MascEntity, newRegex : String) {
        masc.regex = newRegex
    }
    override fun fits(masc : MascEntity, word : IWordEntity) : Boolean {
        if (!masc.partsOfSpeech.contains(word.partOfSpeech)) return false
        for (attr in masc.attrs.keys) {
            var check = false;
            for (ind in masc.attrs[attr]!!) {
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