package com.lavenderlang.ui.fragments.signup

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lavenderlang.R
import com.lavenderlang.databinding.FragmentSignupBinding
import com.lavenderlang.domain.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupFragment : Fragment() {

    lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        // viewModel = MainViewModel()  -  НЕ СМЕЙ
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        binding.blockingView.setOnClickListener { }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.signupButton.setOnClickListener {

            lifecycleScope.launch {
                viewModel.register(
                    binding.inputLogin.text.toString(),
                    binding.inputPassword.text.toString()
                ).collect { value ->
                    binding.apply {
                        when (value) {

                            State.LOADING -> {
                                blockingView.visibility = View.VISIBLE
                                progressBar.visibility = View.VISIBLE
                            }

                            State.SUCCESS -> {

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

                                findNavController().navigate(R.id.action_signupFragment_to_mainFragment)
                            }

                            State.ERROR -> {

                                Snackbar.make(binding.root, "ERROR!", Snackbar.LENGTH_SHORT).show()

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

        return binding.root
    }
}