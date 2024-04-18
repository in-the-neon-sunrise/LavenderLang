package com.lavenderlang.frontend

import android.app.Application
import android.util.Log
import com.anggrayudi.storage.SimpleStorageHelper

class MyApp : Application() {
    companion object {
        private var instance : MyApp? = null
        var storageHelper : SimpleStorageHelper? = null
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
    }
}