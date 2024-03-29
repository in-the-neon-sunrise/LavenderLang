package com.lavenderlang.backend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="language_table")
class LanguageItem(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val lang : String
)