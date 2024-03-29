package com.lavenderlang.backend.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[LanguageItem::class], version=1)
abstract class LanguageDB : RoomDatabase() {
    companion object {
        private var instance : LanguageDB? = null
        @Synchronized
        fun getInstance(context: Context) : LanguageDB {
            if (instance == null) instance = Room.databaseBuilder(
                context.applicationContext,
                LanguageDB::class.java,
                "language_database"
            ).fallbackToDestructiveMigration().build()
            return instance!!
        }
    }
    abstract var languageDao : LanguageDao
}