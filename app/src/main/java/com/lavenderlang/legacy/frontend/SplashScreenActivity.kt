package com.lavenderlang.legacy.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.runBlocking

class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SplashScreenActivity", "onCreate ${MyApp.nextLanguageId}")
        if (MyApp.nextLanguageId == -1) {
            Log.d("SplashScreenActivity", "getting languages from DB")
            runBlocking {
                LanguageDaoImpl().getLanguagesFromDB()
            }
            if (MyApp.nextLanguageId == -1) MyApp.nextLanguageId = 0
        }

        setContentView(R.layout.splash_screen_activity)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}