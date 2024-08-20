package com.lavenderlang.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.ActivityMain2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class MainActivity2: AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var navController: NavController
    private var currentFragmentId: Int = R.id.mainFragment
    private  var isCustom = false
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

        // store nextLanguageId in shared preferences
        runBlocking(Dispatchers.IO) {
            getSharedPreferences("pref", MODE_PRIVATE).edit()
                .putInt("nextLanguageId",
                    try {LanguageRepositoryImpl().getMaxId() + 1}
                catch (e : Exception) {0}
                ).apply()
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
                R.id.main -> {
                    Log.d("BottomNav", "Navigating to mainFragment")
                    if(navController.currentDestination?.id != R.id.mainFragment) navController.navigate(R.id.mainFragment)
                }
                R.id.language -> {
                    Log.d("BottomNav", "Navigating to languageFragment")
                    if(navController.currentDestination?.id != R.id.languageFragment) navController.navigate(R.id.languageFragment)
                }
                R.id.translator -> {
                    Log.d("BottomNav", "Navigating to translatorFragment")
                    if(navController.currentDestination?.id != R.id.translatorFragment) navController.navigate(R.id.translatorFragment)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

                when (destination.id) {
                    R.id.loginFragment, R.id.signupFragment, R.id.startingFragment -> {
                        navView.visibility = View.GONE
                        binding.topAppBar.visibility = View.GONE
                    }

                    R.id.mainFragment -> {
                        navView.visibility = View.VISIBLE
                        binding.topAppBar.visibility = View.GONE
                        navView.setSelectedItemId(R.id.main)
                    }

                    R.id.languageFragment -> {
                        navView.visibility = View.VISIBLE
                        binding.topAppBar.visibility = View.VISIBLE
                        navView.setSelectedItemId(R.id.language)
                    }

                    R.id.translatorFragment -> {
                        navView.visibility = View.VISIBLE
                        binding.topAppBar.visibility = View.VISIBLE
                        navView.setSelectedItemId(R.id.translator)
                    }

                    else -> {
                        navView.visibility = View.VISIBLE
                        binding.topAppBar.visibility = View.VISIBLE
                    }
                }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            if(navController.currentDestination?.id != R.id.instructionFragment) navController.navigate(R.id.instructionFragment)
            true
        }

        currentFragmentId = getSharedPreferences(
            "pref", MODE_PRIVATE).getInt("Fragment", R.id.mainFragment)
        getSharedPreferences("pref", MODE_PRIVATE).edit().putInt("Fragment", R.id.mainFragment).apply()

        if (isNetworkAvailable(this)) {
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("cur frag", currentFragmentId.toString())
                    Log.d("real cur", navController.currentDestination?.id.toString())
                    Log.d("ids", "${R.id.mainFragment} ${R.id.startingFragment}")

                    if (currentFragmentId != navController.currentDestination?.id) {
                        navController.navigate(currentFragmentId)
                    }
                } else {
                    navController.navigate(R.id.loginFragment)
                }
            } ?: navController.navigate(R.id.loginFragment)
        } else {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
        }

        setContentView(binding.root)
    }

    // Обработка навигации вверх
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}