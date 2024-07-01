package com.lavenderlang.frontend

import android.app.Application
import android.util.Log
import com.anggrayudi.storage.SimpleStorageHelper
import com.lavenderlang.backend.entity.language.LanguageEntity

var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()

class MyApp : Application() {
    companion object {
        private var instance : MyApp? = null
        var storageHelper : SimpleStorageHelper? = null
        var nextLanguageId : Int = -1
        fun getInstance() : MyApp {
            if (instance == null) throw Exception("MyApp is not created")
            return instance!!
        }
        fun setInstance(myApp: MyApp) {
            if (instance == null) instance = myApp
        }
    }
    override fun onCreate() {
        super.onCreate()
        Log.d("MyApp", "onCreate")
        setInstance(this)
        Log.d("MyApp", "instance set $nextLanguageId")
    }
}