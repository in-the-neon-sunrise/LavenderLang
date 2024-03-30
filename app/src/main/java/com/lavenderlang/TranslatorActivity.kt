package com.lavenderlang

import android.app.Activity
import android.os.Bundle

class TranslatorActivity : Activity() {
    companion object{
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translator_activity)
    }
}