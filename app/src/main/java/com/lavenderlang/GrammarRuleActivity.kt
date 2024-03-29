package com.lavenderlang

import android.app.Activity
import android.os.Bundle

class GrammarRuleActivity: Activity() {
    companion object{
        var id_lang: Int = 0
        var id_rule: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_activity)
    }
}