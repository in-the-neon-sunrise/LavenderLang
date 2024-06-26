package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl

class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SplashScreenActivity", "onCreate")
        if (nextLanguageId == -1) LanguageDaoImpl().getLanguagesFromDB()

        setContentView(R.layout.splash_screen_activity)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("done", true)
            startActivity(intent)
            finish()
        }, 3000)
    }
}