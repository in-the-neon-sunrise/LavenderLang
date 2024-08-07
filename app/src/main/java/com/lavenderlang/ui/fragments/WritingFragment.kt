package com.lavenderlang.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.WritingDao
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.databinding.FragmentLanguageBinding
import com.lavenderlang.databinding.FragmentWritingBinding
import com.lavenderlang.frontend.InstructionActivity
import com.lavenderlang.frontend.LanguageActivity
import com.lavenderlang.frontend.MainActivity
import com.lavenderlang.frontend.TranslatorActivity
import com.lavenderlang.frontend.WritingActivity

class WritingFragment : Fragment() {
    private lateinit var binding: FragmentWritingBinding
    companion object {
        var idLang: Int = 0
        val writingDao: WritingDao = WritingDaoImpl()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWritingBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            argsToSend.putInt("block", 4)
            findNavController().navigate(
                R.id.action_writingFragment_to_instructionFragment,
                argsToSend
            )
        }
        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_writingFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_writingFragment_to_languageFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            findNavController().navigate(
                R.id.action_writingFragment_to_translatorFragment,
                argsToSend
            )
        }
        //how it was started?
        when (val lang =
            requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_writingFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }



        return binding.root
    }
}