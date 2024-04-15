package com.lavenderlang.backend.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item : LanguageItem) : Long

    @Query("SELECT * FROM language_table")
    fun selectAll() : List<LanguageItem>

    @Query("DELETE FROM language_table WHERE id = :id")
    fun deleteById(id: Int)

    /*@Update
    fun update(languageItem: LanguageItem)*/

    @Query("UPDATE language_table SET name = :name WHERE id = :id")
    fun updateName(id: Int, name: String)

    @Query("UPDATE language_table SET description = :description WHERE id = :id")
    fun updateDescription(id: Int, description: String)

    @Query("UPDATE language_table SET dictionary = :dictionary WHERE id = :id")
    fun updateDictionary(id: Int, dictionary: String)

    @Query("UPDATE language_table SET grammar = :grammar WHERE id = :id")
    fun updateGrammar(id: Int, grammar: String)

    @Query("UPDATE language_table SET vowels = :vowels WHERE id = :id")
    fun updateVowels(id: Int, vowels: String)

    @Query("UPDATE language_table SET consonants = :consonants WHERE id = :id")
    fun updateConsonants(id: Int, consonants: String)

    @Query("UPDATE language_table SET puncSymbols = :puncSymbols WHERE id = :id")
    fun updatePuncSymbols(id: Int, puncSymbols: String)

    @Query("UPDATE language_table SET capitalizedPartsOfSpeech = :capitalizedPartsOfSpeech WHERE id = :id")
    fun updateCapitalizedPartsOfSpeech(id: Int, capitalizedPartsOfSpeech: String)
}