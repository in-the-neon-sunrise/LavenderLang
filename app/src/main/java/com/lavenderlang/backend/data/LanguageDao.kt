package com.lavenderlang.backend.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LanguageDao {
    @Insert
    fun insert(item : LanguageItem) : Long

    @Query("SELECT * FROM language_table WHERE id = :id")
    fun selectById(id : Int) : LiveData<ArrayList<LanguageItem>>

    @Query("SELECT * FROM language_table")
    fun selectAll() : LiveData<ArrayList<LanguageItem>>
}