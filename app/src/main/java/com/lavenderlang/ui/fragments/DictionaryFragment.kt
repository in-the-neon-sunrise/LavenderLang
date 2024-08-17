package com.lavenderlang.ui.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.DictionaryDao
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.DictionaryEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.databinding.FragmentDictionaryBinding
import com.lavenderlang.ui.MyApp

class DictionaryFragment : Fragment() {
    private lateinit var binding: FragmentDictionaryBinding
    companion object {
        var idLang: Int = 0
        val dictionaryDao: DictionaryDao = DictionaryDaoImpl()
        var sort: Int = 0
        var filter: Int = 0
        lateinit var dictionary: DictionaryEntity
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("block", 7)
            findNavController().navigate(
                R.id.action_dictionaryFragment_to_instructionFragment,
                argsToSend
            )
        }

        binding.buttonNewWord.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("word", -1)
            findNavController().navigate(R.id.action_dictionaryFragment_to_wordFragment, argsToSend)
        }
        //how it was started?
        when (val lang =
            requireContext().getSharedPreferences("pref", MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_dictionaryFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }

        dictionary = MyApp.language!!.dictionary

        var flag = mutableListOf("по конлангу", "по переводу")
        var adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, flag)
        binding.spinnerSort.adapter = adapter
        adapter.notifyDataSetChanged()

        flag = mutableListOf(
            "всё",
            "существительные",
            "глаголы",
            "прилагательные",
            "наречия",
            "причастия",
            "деепричастия",
            "местоимения",
            "числительные",
            "служебные части речи"
        )
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, flag)
        binding.spinnerFilter.adapter = adapter
        adapter.notifyDataSetChanged()

        allWords()

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                sort = position
                allWords()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                filter = position
                allWords()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        return binding.root
    }

    fun allWords() {
        var list = dictionary.dict.toList()
        if (sort == 0) {
            when (filter) {
                0 -> list = dictionaryDao.sortDictByWord(dictionary)
                1 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.NOUN)
                2 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.VERB)
                3 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.ADJECTIVE)
                4 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.ADVERB)
                5 -> list =
                    dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.PARTICIPLE)

                6 -> list =
                    dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.VERB_PARTICIPLE)

                7 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.PRONOUN)
                8 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.NUMERAL)
                9 -> list = dictionaryDao.sortDictByWordFiltered(dictionary, PartOfSpeech.FUNC_PART)
            }
        } else {
            when (filter) {
                0 -> list = dictionaryDao.sortDictByTranslation(dictionary)
                1 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.NOUN)

                2 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.VERB)

                3 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.ADJECTIVE)

                4 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.ADVERB)

                5 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.PARTICIPLE)

                6 -> list = dictionaryDao.sortDictByTranslationFiltered(
                    dictionary,
                    PartOfSpeech.VERB_PARTICIPLE
                )

                7 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.PRONOUN)

                8 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.NUMERAL)

                9 -> list =
                    dictionaryDao.sortDictByTranslationFiltered(dictionary, PartOfSpeech.FUNC_PART)
            }
        }

        val adapter: ArrayAdapter<IWordEntity> = WordAdapter(requireContext(), list.toMutableList())
        binding.listWords.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.listWords.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                Log.d("dict", "${MyApp.language!!.dictionary.fullDict}")
                val id: Int =
                    dictionary.dict.indexOfFirst { it.word == list[position].word }
                val argsToSend = Bundle()
                Log.d("id", "${list[position].word}, $id")
                argsToSend.putInt("word", id)
                findNavController().navigate(R.id.action_dictionaryFragment_to_wordFragment, argsToSend)
            }
    }
}

class WordAdapter(context: Context, listOfWords: MutableList<IWordEntity>) :
    ArrayAdapter<IWordEntity>(context, R.layout.word_line_activity, listOfWords) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newView = convertView
        val word: IWordEntity? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.word_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewDescription)
        val changeableAttributes: TextView = newView.findViewById(R.id.textViewConlangWord)
        unchangeableAttributes.text = word?.word
        changeableAttributes.text = word?.translation

        return newView
    }
}