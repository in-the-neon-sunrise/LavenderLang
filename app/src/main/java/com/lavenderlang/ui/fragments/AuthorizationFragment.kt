package com.lavenderlang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lavenderlang.backend.dao.language.WritingDao
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.databinding.FragmentAuthorizationBinding
import com.lavenderlang.databinding.FragmentWritingBinding
import com.lavenderlang.ui.MyApp

class AuthorizationFragment: Fragment(){
    private lateinit var binding: FragmentAuthorizationBinding
    companion object {
        var idLang: Int = 0
        var optionIsLogIn = true
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        binding.editTextTextEmail.visibility = View.GONE
        binding.buttonChangeOption.setOnClickListener{
            if(optionIsLogIn){
                optionIsLogIn = false
                binding.editTextTextEmail.visibility = View.VISIBLE
                binding.buttonBig.text = "Регистрация"
                binding.buttonChangeOption.text = "Есть аккаунт? Войти"
            }
            else{
                optionIsLogIn = true
                binding.editTextTextEmail.visibility = View.GONE
                binding.buttonBig.text = "Вход"
                binding.buttonChangeOption.text = "Нет аккаунта? Зарегистрироваться"
            }
        }
        binding.buttonBig.setOnClickListener {
            if(optionIsLogIn){
                //binding.editTextUserName.text
                //binding.editTextPassword.text
                //проверить и перейти
                //MyApp.
            }
            else{
                //проверить и перейти
            }
        }
        
        return binding.root
    }
}