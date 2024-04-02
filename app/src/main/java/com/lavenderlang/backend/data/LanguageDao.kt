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
    fun selectAll() : LiveData<List<LanguageItem>>

    @Query("DELETE FROM language_table WHERE id = :id")
    fun deleteById(id: Int)

    @Update
    fun update(languageItem: LanguageItem)
}