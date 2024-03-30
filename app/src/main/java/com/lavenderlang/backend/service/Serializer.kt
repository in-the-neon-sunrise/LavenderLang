package com.lavenderlang.backend.service

import com.chaquo.python.Python
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.Characteristic
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.languages
import java.io.File
import java.util.SortedSet

class Serializer(path: String = "") {
    private var dir : String = ""
    init {
        dir = "$path\\data"
        val folder = File(dir)
        if (!folder.exists()) folder.mkdirs()
    }

    companion object {
        val mapper = ObjectMapper()
    }

    fun f(): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        val r = mutableMapOf(Attributes.NUMBER to 1, Attributes.CASE to 3)
        return module.callAttr(
            "inflectAttrs", "кошечка",
            PartOfSpeech.NOUN.toString(),
            r.values.toString()
        ).toString()
    }

    fun serializeGrammarRules(rules: SortedSet<GrammarRuleEntity>) : String {
        return try {
            mapper.writeValueAsString(rules)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeWordFormationRules(rules: SortedSet<WordFormationRuleEntity>) : String {
        return try {
            mapper.writeValueAsString(rules)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeVars(vars: MutableMap<Int, Characteristic>) : String {
        return try {
            mapper.writeValueAsString(vars)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeNextIds(nextIds: MutableMap<Attributes, Int>) : String {
        return try {
            mapper.writeValueAsString(nextIds)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun deserialize(languageString: String) : LanguageEntity {
        try {
            return mapper.readValue(languageString, LanguageEntity::class.java)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException(e.message!!)
        }
    }

    fun deserializeWord(wordString: String) : IWordEntity {
        try {
            return mapper.readValue(wordString, IWordEntity::class.java)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException(e.message!!)
        }
    }

    fun serializeFullDict(fullDict: MutableMap<IWordEntity, MutableList<IWordEntity>>) : String {
        return try {
            mapper.writeValueAsString(fullDict)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeDict(dict: ArrayList<IWordEntity>) : String {
        return try {
            mapper.writeValueAsString(dict)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeWord(word: IWordEntity) : String {
        return try {
            mapper.writeValueAsString(word)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun serializeLanguage(language: LanguageEntity) : String {
        return try {
            mapper.writeValueAsString(language)
        } catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun createDir() : String {
        val folder = File("$dir\\data")
        if (!folder.exists()) return folder.mkdirs().toString()
        return "already there"
    }

    fun updateMaxLanguageId(newId : Int = getMaxLanguageId() + 1) {
        val jsonFile = File("$dir\\maxLanguageId.json")
        mapper.writeValue(jsonFile, newId)
    }

    fun getMaxLanguageId(): Int {
        val jsonFile = File("$dir\\maxLanguageId.json")
        if (!jsonFile.exists()) {
            return 0
        }
        val rootNode: JsonNode = mapper.readTree(jsonFile)
        return rootNode.asInt()
    }

    fun saveLanguage(languageId : Int) {
        val jsonFile = File("$dir\\language$languageId.json")
        try {
            mapper.writeValue(jsonFile, languages[languageId])
        }
        catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun saveAllLanguages() {
        for (ind in languages.keys) saveLanguage(ind)
    }

    fun readLanguage(languageId: Int) : LanguageEntity {
        val jsonFile = File("$dir\\language$languageId.json")
        try {
            return mapper.readValue(jsonFile, LanguageEntity::class.java)
        }
        catch (e : Exception) {
            println(e.message)
            throw LanguageNotFoundException("")
        }
    }

    fun readAllLanguages() :  MutableMap<Int, LanguageEntity> {
        val newLanguages : MutableMap<Int, LanguageEntity> = mutableMapOf()
        for (i in (0..<getMaxLanguageId())) {
            try {
                newLanguages[i] = readLanguage(i);
            }
            catch (_: LanguageNotFoundException) {}
        }
        return newLanguages
    }
}