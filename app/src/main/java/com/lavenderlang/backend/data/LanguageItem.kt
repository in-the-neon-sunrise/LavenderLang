package com.lavenderlang.backend.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="language_table")
class LanguageItem(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    var name : String,
    var description: String,
    var dictionary: String,
    var grammar : String,
    var vowels : String,
    var consonants: String,
    var puncSymbols : String,
    var capitalizedPartsOfSpeech : String
)