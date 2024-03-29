package com.lavenderlang.backend.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.languages
import java.io.File

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

    fun serialize(language: LanguageEntity) : String {
        return try {
            mapper.writeValueAsString(language)
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