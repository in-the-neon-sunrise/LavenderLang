package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.databinding.FragmentPunctuationBinding
import com.lavenderlang.domain.usecase.UpdatePuncSymbolsUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PunctuationFragment : Fragment() {
    private lateinit var binding: FragmentPunctuationBinding
    companion object{
        var idLang: Int = 0
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
        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("block", 5)
            findNavController().navigate(
                R.id.action_punctuationFragment_to_instructionFragment,
                argsToSend
            )
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_punctuationFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }

        // set symbols
        binding.editTextConlangSymbol1.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[0])
        binding.editTextConlangSymbol2.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[1])
        binding.editTextConlangSymbol3.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[2])
        binding.editTextConlangSymbol4.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[3])
        binding.editTextConlangSymbol5.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[4])
        binding.editTextConlangSymbol6.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[5])
        binding.editTextConlangSymbol7.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[6])
        binding.editTextConlangSymbol8.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[7])
        binding.editTextConlangSymbol9.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[8])
        binding.editTextConlangSymbol10.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[9])
        binding.editTextConlangSymbol11.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[10])
        binding.editTextConlangSymbol12.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[11])
        binding.editTextConlangSymbol13.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[12])
        binding.editTextConlangSymbol14.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[13])
        binding.editTextConlangSymbol15.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[14])
        binding.editTextConlangSymbol16.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[15])
        binding.editTextConlangSymbol17.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[16])
        binding.editTextConlangSymbol18.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[17])
        binding.editTextConlangSymbol19.editText?.setText(MyApp.language!!.puncSymbols.values.toMutableList()[18])


        // save symbols
        binding.buttonSavePunctuation.setOnClickListener {
            var i = 0
            for (editText in listOf(
                binding.editTextConlangSymbol1,
                binding.editTextConlangSymbol2,
                binding.editTextConlangSymbol3,
                binding.editTextConlangSymbol4,
                binding.editTextConlangSymbol5,
                binding.editTextConlangSymbol6,
                binding.editTextConlangSymbol7,
                binding.editTextConlangSymbol8,
                binding.editTextConlangSymbol9,
                binding.editTextConlangSymbol10,
                binding.editTextConlangSymbol11,
                binding.editTextConlangSymbol12,
                binding.editTextConlangSymbol13,
                binding.editTextConlangSymbol14,
                binding.editTextConlangSymbol15,
                binding.editTextConlangSymbol16,
                binding.editTextConlangSymbol17,
                binding.editTextConlangSymbol18,
                binding.editTextConlangSymbol19
            )) {
                val newSymbol = editText.editText?.text.toString()
                Log.d("newSymbol", newSymbol)

                for (letter in newSymbol) {
                    if (MyApp.language!!.vowels.contains(letter.lowercase()) ||
                        MyApp.language!!.consonants.contains(letter.lowercase())
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Буква $letter уже находится в алфавите языка!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
                MyApp.language!!.puncSymbols[MyApp.language!!.puncSymbols.keys.toList()[i]] =
                    newSymbol
                i += 1
            }
            lifecycleScope.launch(Dispatchers.IO) {
                UpdatePuncSymbolsUseCase.execute(
                    MyApp.language!!.languageId, MyApp.language!!.puncSymbols, LanguageRepositoryImpl()
                )
            }
        }
        return binding.root
    }
}