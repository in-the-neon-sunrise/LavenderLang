package com.lavenderlang.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.PunctuationDao
import com.lavenderlang.backend.dao.language.PunctuationDaoImpl
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.databinding.FragmentLanguageBinding
import com.lavenderlang.databinding.FragmentPunctuationBinding
import com.lavenderlang.frontend.MyApp

class PunctuationFragment : Fragment() {
    private lateinit var binding: FragmentPunctuationBinding
    companion object{
        var idLang: Int = 0
        val punctuationDao: PunctuationDao = PunctuationDaoImpl()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPunctuationBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 5)
            findNavController().navigate(
                R.id.action_punctuationFragment_to_instructionFragment,
                argsToSend
            )
        }
        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_punctuationFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_punctuationFragment_to_languageFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            findNavController().navigate(R.id.action_punctuationFragment_to_translatorFragment)
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_punctuationFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }

        // set symbols
        binding.editTextConlangSymbol1.setText(MyApp.language!!.puncSymbols.values.toMutableList()[0])
        binding.editTextConlangSymbol2.setText(MyApp.language!!.puncSymbols.values.toMutableList()[1])
        binding.editTextConlangSymbol3.setText(MyApp.language!!.puncSymbols.values.toMutableList()[2])
        binding.editTextConlangSymbol4.setText(MyApp.language!!.puncSymbols.values.toMutableList()[3])
        binding.editTextConlangSymbol5.setText(MyApp.language!!.puncSymbols.values.toMutableList()[4])
        binding.editTextConlangSymbol6.setText(MyApp.language!!.puncSymbols.values.toMutableList()[5])
        binding.editTextConlangSymbol7.setText(MyApp.language!!.puncSymbols.values.toMutableList()[6])
        binding.editTextConlangSymbol8.setText(MyApp.language!!.puncSymbols.values.toMutableList()[7])
        binding.editTextConlangSymbol9.setText(MyApp.language!!.puncSymbols.values.toMutableList()[8])
        binding.editTextConlangSymbol10.setText(MyApp.language!!.puncSymbols.values.toMutableList()[9])
        binding.editTextConlangSymbol11.setText(MyApp.language!!.puncSymbols.values.toMutableList()[10])
        binding.editTextConlangSymbol12.setText(MyApp.language!!.puncSymbols.values.toMutableList()[11])
        binding.editTextConlangSymbol13.setText(MyApp.language!!.puncSymbols.values.toMutableList()[12])
        binding.editTextConlangSymbol14.setText(MyApp.language!!.puncSymbols.values.toMutableList()[13])
        binding.editTextConlangSymbol15.setText(MyApp.language!!.puncSymbols.values.toMutableList()[14])
        binding.editTextConlangSymbol16.setText(MyApp.language!!.puncSymbols.values.toMutableList()[15])
        binding.editTextConlangSymbol17.setText(MyApp.language!!.puncSymbols.values.toMutableList()[16])
        binding.editTextConlangSymbol18.setText(MyApp.language!!.puncSymbols.values.toMutableList()[17])
        binding.editTextConlangSymbol19.setText(MyApp.language!!.puncSymbols.values.toMutableList()[18])


        // save symbols
        binding.buttonSavePunctuation.setOnClickListener {
            try{
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 0, binding.editTextConlangSymbol1.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 1, binding.editTextConlangSymbol2.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 2, binding.editTextConlangSymbol3.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 3, binding.editTextConlangSymbol4.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 4, binding.editTextConlangSymbol5.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 5, binding.editTextConlangSymbol6.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 6, binding.editTextConlangSymbol7.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 7, binding.editTextConlangSymbol8.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 8, binding.editTextConlangSymbol9.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 9, binding.editTextConlangSymbol10.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 10, binding.editTextConlangSymbol11.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 11, binding.editTextConlangSymbol12.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 12, binding.editTextConlangSymbol13.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 13, binding.editTextConlangSymbol14.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 14, binding.editTextConlangSymbol15.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 15, binding.editTextConlangSymbol16.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 16, binding.editTextConlangSymbol17.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 17, binding.editTextConlangSymbol18.text.toString())
                punctuationDao.updatePunctuationSymbol(MyApp.language!!, 18, binding.editTextConlangSymbol19.text.toString())
            }catch (e: ForbiddenSymbolsException){
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}