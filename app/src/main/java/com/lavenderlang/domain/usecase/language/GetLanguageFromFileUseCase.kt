package com.lavenderlang.domain.usecase.language

import android.content.Context
import android.util.Log
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.openInputStream
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.exception.FileWorkException
import com.lavenderlang.domain.model.language.LanguageEntity
import java.io.File

class GetLanguageFromFileUseCase {
    companion object {
        suspend fun execute(
            path: String,
            id: Int,
            repo: LanguageRepository,
            context: Context
        ): LanguageEntity {
            val origFile = File(path)
            val file = DocumentFileCompat.fromFile(context, origFile)
            if (file == null) {
                Log.d("file", "no file")
                throw FileWorkException("Не удалось загрузить язык")
            }
            val inputStream = file.openInputStream(context)
            if (inputStream == null) {
                Log.d("file", "no input stream")
                throw FileWorkException("Не удалось загрузить язык")
            }
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val language = Serializer.getInstance().deserializeLanguage(inputString)
            language.languageId = id

            language.grammar.languageId = id
            for (rule in language.grammar.grammarRules) {
                rule.languageId = id
            }
            for (rule in language.grammar.wordFormationRules) {
                rule.languageId = id
            }
            for (word in language.dictionary.dict) {
                word.languageId = id
            }
            for (key in language.dictionary.fullDict.keys) {
                for (word in language.dictionary.fullDict[key]!!) {
                    word.languageId = id
                }
            }

            language.dictionary.languageId = id

            repo.insertLanguage(id, language)
            return language
        }
    }
}