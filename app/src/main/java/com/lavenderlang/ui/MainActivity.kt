package com.lavenderlang.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import kotlinx.coroutines.runBlocking


class MainActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkTheme = getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false)
        Log.d("Theme", "start. $isDarkTheme")
        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(if (isDarkTheme) R.style.AppTheme_Night else R.style.AppTheme_Day)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        MyApp.lifecycleScope = lifecycleScope

        if (MyApp.nextLanguageId == -1) {
            runBlocking { LanguageDaoImpl().getLanguagesFromDB() }
            if (MyApp.nextLanguageId == -1) MyApp.nextLanguageId = 0
        }
    }

    // Обработка навигации вверх
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}