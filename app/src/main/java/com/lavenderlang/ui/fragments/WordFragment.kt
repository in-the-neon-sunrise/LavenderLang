package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.DictionaryDao
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.data.PythonHandlerImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.model.word.NounEntity
import com.lavenderlang.databinding.FragmentWordBinding
import com.lavenderlang.domain.model.word.AdjectiveEntity
import com.lavenderlang.domain.model.word.AdverbEntity
import com.lavenderlang.domain.model.word.FuncPartEntity
import com.lavenderlang.domain.model.word.NumeralEntity
import com.lavenderlang.domain.model.word.ParticipleEntity
import com.lavenderlang.domain.model.word.PronounEntity
import com.lavenderlang.domain.model.word.VerbEntity
import com.lavenderlang.domain.model.word.VerbParticipleEntity
import com.lavenderlang.domain.usecase.dictionary.AddWordUseCase
import com.lavenderlang.domain.usecase.dictionary.DeleteWordUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WordFragment : Fragment() {
    private lateinit var binding: FragmentWordBinding
    companion object{
        var idLang: Int = -1
        var idWord: Int = -1

        var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf()
        var idPartOfSpeech: Int = 0
        var partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN
        var flagIsFirst:Boolean = true

        val dictionaryDao: DictionaryDao = DictionaryDaoImpl()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWordBinding.inflate(inflater, container, false)

        //top navigation menu

        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 7)
            findNavController().navigate(
                R.id.action_wordFragment_to_instructionFragment,
                argsToSend
            )
        }

        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_wordFragment_to_languageFragment)
            }
            else -> {
                idLang = lang
            }
        }

        // get word from argsToSend
        val word = arguments?.getInt("word", -1) ?: -1
        when (word) {
            -1 -> {
                idWord = MyApp.language!!.dictionary.dict.size
                MyApp.language!!.dictionary.dict.add(NounEntity(
                    MyApp.language!!.languageId, "", "-"))
                lifecycleScope.launch(Dispatchers.IO) {
                    AddWordUseCase.execute(
                        MyApp.language!!,
                        NounEntity(MyApp.language!!.languageId, "", "-"),
                        LanguageRepositoryImpl(), PythonHandlerImpl())
                }
                Log.d("create word", MyApp.language!!.dictionary.dict[idWord].word)
            }
            else -> {
                Log.d("old word", MyApp.language!!.dictionary.dict[word].word)
                idWord = word
            }
        }

        flagIsFirst =true


        Log.d("full", MyApp.language!!.dictionary.fullDict.toString())

        binding.editConlangWord.editText?.setText(MyApp.language!!.dictionary.dict[idWord].word)
        binding.editRussianWord.editText?.setText(MyApp.language!!.dictionary.dict[idWord].translation)

        partOfSpeech = MyApp.language!!.dictionary.dict[idWord].partOfSpeech
        idPartOfSpeech = when (partOfSpeech){
            PartOfSpeech.NOUN-> 0
            PartOfSpeech.VERB-> 1
            PartOfSpeech.ADJECTIVE-> 2
            PartOfSpeech.ADVERB-> 3
            PartOfSpeech.PARTICIPLE-> 4
            PartOfSpeech.VERB_PARTICIPLE-> 5
            PartOfSpeech.PRONOUN-> 6
            PartOfSpeech.NUMERAL-> 7
            PartOfSpeech.FUNC_PART-> 8
        }
        immutableAttrs = MyApp.language!!.dictionary.dict[idWord].immutableAttrs

        setSpinners()
        updateSpinners()
        setPartOfSpeechListener()

        updateWordForms()
        binding.buttonCreateWords.setOnClickListener {
            updateNewWords()
        }

        binding.buttonSave.setOnClickListener {
            partOfSpeech = when (idPartOfSpeech) {
                0 -> PartOfSpeech.NOUN
                1 -> PartOfSpeech.VERB
                2 -> PartOfSpeech.ADJECTIVE
                3 -> PartOfSpeech.ADVERB
                4 -> PartOfSpeech.PARTICIPLE
                5 -> PartOfSpeech.VERB_PARTICIPLE
                6 -> PartOfSpeech.PRONOUN
                7 -> PartOfSpeech.NUMERAL
                8 -> PartOfSpeech.FUNC_PART
                else -> PartOfSpeech.NOUN
            }
            updateAttrs()
            val oldWordEntity = MyApp.language!!.dictionary.dict[idWord]
            val newWord = binding.editConlangWord.editText?.text.toString()

            for (letter in newWord) {
                if (!MyApp.language!!.vowels.contains(letter.lowercase()) &&
                    !MyApp.language!!.consonants.contains(letter.lowercase())
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Буква $letter не находится в алфавите языка!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            val newTranslation = binding.editRussianWord.editText?.text.toString()
            val newImmutableAttrs = immutableAttrs
            val newPartOfSpeech = partOfSpeech
            val newWordEntity = when (newPartOfSpeech) {
                PartOfSpeech.NOUN -> NounEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.VERB -> VerbEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.ADJECTIVE -> AdjectiveEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.ADVERB -> AdverbEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.PARTICIPLE -> ParticipleEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.VERB_PARTICIPLE -> VerbParticipleEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.PRONOUN -> PronounEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.NUMERAL -> NumeralEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )

                PartOfSpeech.FUNC_PART -> FuncPartEntity(
                    oldWordEntity.languageId,
                    newWord,
                    newTranslation,
                    immutableAttrs = newImmutableAttrs
                )
            }

            MyApp.language!!.dictionary.dict.remove(oldWordEntity)
            if (!MyApp.language!!.dictionary.dict.contains(newWordEntity)) {
                MyApp.language!!.dictionary.dict.add(newWordEntity)
            }
            Log.d("WordFrag upd", MyApp.language!!.dictionary.dict.toString())

            lifecycleScope.launch(Dispatchers.IO) {
                DeleteWordUseCase.execute(
                    MyApp.language!!.dictionary,
                    oldWordEntity,
                    LanguageRepositoryImpl(), PythonHandlerImpl())
                AddWordUseCase.execute(
                    MyApp.language!!,
                    newWordEntity,
                    LanguageRepositoryImpl(), PythonHandlerImpl())
            }
        }


        binding.buttonDelete.setOnClickListener {
            val oldWordEntity = MyApp.language!!.dictionary.dict[idWord]
            MyApp.language!!.dictionary.dict.removeAt(idWord)
            lifecycleScope.launch(Dispatchers.IO) {
                DeleteWordUseCase.execute(
                    MyApp.language!!.dictionary,
                    oldWordEntity,
                    LanguageRepositoryImpl(), PythonHandlerImpl())
            }
            Log.d("WordFrag del", MyApp.language!!.dictionary.dict.toString())
            findNavController().popBackStack()
        }
        return binding.root
    }
    private fun setSpinners() {
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Служебное слово"))
        binding.spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        val genderNames = MyApp.language!!.grammar.varsGender.values.map { it.name }
        val genderAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderNames)
        binding.spinnerGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()

        val typeNames = MyApp.language!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeNames)
        binding.spinnerType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = MyApp.language!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, voiceNames)
        binding.spinnerVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()
    }
    fun updateSpinners(){
        binding.spinnerPartOfSpeech.setSelection(idPartOfSpeech)
        when(idPartOfSpeech) {
            0 -> {
                binding.spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                binding.spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
                binding.spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2-> {}
            3->{}
            4->{
                binding.spinnerType.setSelection(immutableAttrs[Attributes.TYPE]?: 0)
                binding.spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                binding.spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                binding.spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
    private fun setPartOfSpeechListener(){
        binding.spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    1->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    2->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    3->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    4->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    5->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.VISIBLE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    6->{
                        binding.spinnerGender.visibility= View.VISIBLE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    7->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    else->{
                        binding.spinnerGender.visibility= View.GONE
                        binding.spinnerType.visibility= View.GONE
                        binding.spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }
    private fun updateAttrs() {
        when(idPartOfSpeech) {
            0->{
                immutableAttrs[Attributes.GENDER]=binding.spinnerGender.selectedItemPosition
            }
            1->{
                immutableAttrs[Attributes.TYPE]=binding.spinnerType.selectedItemPosition
                immutableAttrs[Attributes.VOICE]=binding.spinnerVoice.selectedItemPosition
            }
            2->{}
            3->{}
            4->{
                immutableAttrs[Attributes.TYPE]=binding.spinnerType.selectedItemPosition
                immutableAttrs[Attributes.VOICE]=binding.spinnerVoice.selectedItemPosition
            }
            5->{
                immutableAttrs[Attributes.TYPE]=binding.spinnerType.selectedItemPosition
            }
            6->{
                immutableAttrs[Attributes.GENDER]=binding.spinnerGender.selectedItemPosition
            }
            7->{}
            else->{}
        }
    }
    private fun updateWordForms() {
        val wordForms = dictionaryDao.getWordForms(MyApp.language!!.dictionary, MyApp.language!!.dictionary.dict[idWord].word)
        Log.d("wordForms", wordForms.toString())
        val adapterWordForms: ArrayAdapter<IWordEntity> = WordAdapter(requireContext(), wordForms)
        binding.listWordForms.adapter = adapterWordForms
        adapterWordForms.notifyDataSetChanged()
    }

    fun updateNewWords(){
        //list of new words
        val list: MutableList<Pair<String, IWordEntity>> = dictionaryDao.createWordsFromExisting(
            MyApp.language!!.dictionary, MyApp.language!!.dictionary.dict[idWord]).toMutableList()
        val adapter: ArrayAdapter<Pair<String, IWordEntity>> = NewWordAdapter(requireContext(), list)
        binding.listViewNewWords.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}
private class NewWordAdapter(context: Context, listOfWords: MutableList<Pair<String, IWordEntity>>) :
    ArrayAdapter<Pair<String, IWordEntity>>(context, R.layout.new_word_line_activity, listOfWords) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val description: String = getItem(position)!!.first
        val word: IWordEntity = getItem(position)!!.second
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.new_word_line_activity, null)
        }

        //textview is visible
        val textViewDescription: TextView = newView!!.findViewById(R.id.textViewDescription)
        val textViewConlangWord: TextView = newView.findViewById(R.id.textViewConlangWord)
        val editTextRussianWord: EditText = newView.findViewById(R.id.editTextRussianWord)
        val buttonSave: Button = newView.findViewById(R.id.buttonSave)

        textViewDescription.text = description
        textViewConlangWord.text = word.word
        editTextRussianWord.setText(word.translation)
        buttonSave.tag = position
        buttonSave.setOnClickListener {
            if (position == buttonSave.tag) {
                val language = MyApp.language!!
                for (letter in word.word) {
                    if (!language.vowels.contains(letter.lowercase()) &&
                        !language.consonants.contains(letter.lowercase())) {
                        Toast.makeText(context, "Буква $letter не находится в алфавите языка!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                language.dictionary.dict.add(word)
                // fixme: global scope :<
                GlobalScope.launch(Dispatchers.IO) {
                        AddWordUseCase.execute(
                            language, word,
                            LanguageRepositoryImpl(), PythonHandlerImpl())

                    }
            }
        }

        return newView
    }
}