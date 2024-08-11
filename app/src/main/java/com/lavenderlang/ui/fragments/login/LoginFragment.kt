package com.lavenderlang.ui.fragments.login

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
import com.lavenderlang.databinding.FragmentLoginBinding
import com.lavenderlang.domain.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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