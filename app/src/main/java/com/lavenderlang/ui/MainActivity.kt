package com.lavenderlang.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import kotlinx.coroutines.runBlocking
import com.lavenderlang.databinding.ActivityMain2Binding
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import kotlin.math.log


class MainActivity2: AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkTheme = getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false)
        Log.d("Theme", "start. $isDarkTheme")
        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(if (isDarkTheme) R.style.AppTheme_Night else R.style.AppTheme_Day)

        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)

        MyApp.lifecycleScope = lifecycleScope

        if (MyApp.nextLanguageId == -1) {
            runBlocking { LanguageDaoImpl().getLanguagesFromDB() }
            if (MyApp.nextLanguageId == -1) MyApp.nextLanguageId = 0
        }

        val navView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)!! as NavHostFragment

        AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.languageFragment,
                R.id.translatorFragment
            )
        )
        navController = navHostFragment.navController

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainFragment -> {
                    Log.d("BottomNav", "Navigating to mainFragment")
                    navController.navigate(R.id.mainFragment)
                }
                R.id.languageFragment -> {
                    Log.d("BottomNav", "Navigating to languageFragment")
                    navController.navigate(R.id.languageFragment)
                }
                R.id.translatorFragment -> {
                    Log.d("BottomNav", "Navigating to translatorFragment")
                    navController.navigate(R.id.translatorFragment)
                }
            }
            true
        }
        //navView.setupWithNavController(navController)
        setContentView(binding.root)
    }

    // Обработка навигации вверх
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}