package com.lavenderlang.ui.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.FragmentLoginBinding
import com.lavenderlang.domain.auth.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lavenderlang.databinding.ActivityMain2Binding
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.blockingView.setOnClickListener { }

        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.loginButton.setOnClickListener {

            if (binding.inputLogin2.text.toString().isEmpty())
                    Snackbar.make(binding.root, "Введите логин!", Snackbar.LENGTH_SHORT).show()
            if (binding.inputPassword2.text.toString().isEmpty())
                    Snackbar.make(binding.root, "Введите пароль!", Snackbar.LENGTH_SHORT).show()
            else {

                lifecycleScope.launch {
                    viewModel.login(
                        binding.inputLogin2.text.toString(),
                        binding.inputPassword2.text.toString()
                    ).collect { value ->
                        binding.apply {
                            when (value) {

                                State.LOADING -> {
                                    blockingView.visibility = View.VISIBLE
                                    progressBar.visibility = View.VISIBLE
                                }

                                State.SUCCESS -> {
                                    // store nextLanguageId in shared preferences
                                    runBlocking(Dispatchers.IO) {
                                        requireContext().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE).edit()
                                            .putInt("nextLanguageId",
                                                try {
                                                    LanguageRepositoryImpl().getMaxId() + 1}
                                                catch (e : Exception) {0}
                                            ).apply()
                                    }

                                    progressBar.setVisibilityAfterHide(View.GONE)
                                    progressBar.hide()

                                    val anim = ObjectAnimator.ofFloat(
                                        blockingView,
                                        "alpha",
                                        0.5f,
                                        0f
                                    )

                                    anim.duration = 500
                                    anim.start()

                                    withContext(Dispatchers.IO) {
                                        Thread.sleep(500)
                                    }

                                    blockingView.visibility = View.GONE

                                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                                }

                                else -> {

                                    Snackbar.make(binding.root, "Неверный логин или пароль!", Snackbar.LENGTH_SHORT)
                                        .show()

                                    progressBar.setVisibilityAfterHide(View.GONE)
                                    progressBar.hide()

                                    val anim = ObjectAnimator.ofFloat(
                                        blockingView,
                                        "alpha",
                                        0.5f,
                                        0f
                                    )

                                    anim.duration = 500
                                    anim.start()

                                    withContext(Dispatchers.IO) {
                                        Thread.sleep(500)
                                    }

                                    blockingView.visibility = View.GONE
                                }

                            }
                        }
                    }
                }
            }

        }


        return binding.root
    }
}