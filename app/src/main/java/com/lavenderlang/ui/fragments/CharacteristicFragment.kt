package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.lavenderlang.domain.model.help.Attributes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.databinding.FragmentCharacteristicBinding
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.databinding.FragmentGrammarBinding
import com.lavenderlang.domain.getOrigInfo
import com.lavenderlang.domain.getResultInfo
import com.lavenderlang.domain.rusCase
import com.lavenderlang.domain.rusDegreeOfComparison
import com.lavenderlang.domain.rusGender
import com.lavenderlang.domain.rusMood
import com.lavenderlang.domain.rusNumber
import com.lavenderlang.domain.rusPerson
import com.lavenderlang.domain.rusTime
import com.lavenderlang.domain.rusType
import com.lavenderlang.domain.rusVoice
import com.lavenderlang.domain.usecase.grammar.AddOptionUseCase
import com.lavenderlang.domain.usecase.grammar.DeleteOptionUseCase
import com.lavenderlang.domain.usecase.grammar.UpdateOptionUseCase
import com.lavenderlang.domain.usecase.update.UpdateGrammarUseCase
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CharacteristicFragment : Fragment() {
    private lateinit var binding: FragmentCharacteristicBinding

    companion object {
        var idLang: Int = -1

        lateinit var grammar: GrammarEntity

        lateinit var gender: MutableList<CharacteristicEntity>
        lateinit var number: MutableList<CharacteristicEntity>
        lateinit var case: MutableList<CharacteristicEntity>
        lateinit var time: MutableList<CharacteristicEntity>
        lateinit var person: MutableList<CharacteristicEntity>
        lateinit var mood: MutableList<CharacteristicEntity>
        lateinit var type: MutableList<CharacteristicEntity>
        lateinit var voice: MutableList<CharacteristicEntity>
        lateinit var degreeOfComparison: MutableList<CharacteristicEntity>

        var genderNames: MutableList<String> = mutableListOf()
        var genderRusIds: MutableList<Int> = mutableListOf()
        var numberNames: MutableList<String> = mutableListOf()
        var numberRusIds: MutableList<Int> = mutableListOf()
        var caseNames: MutableList<String> = mutableListOf()
        var caseRusIds: MutableList<Int> = mutableListOf()
        var timeNames: MutableList<String> = mutableListOf()
        var timeRusIds: MutableList<Int> = mutableListOf()
        var personNames: MutableList<String> = mutableListOf()
        var personRusIds: MutableList<Int> = mutableListOf()
        var moodNames: MutableList<String> = mutableListOf()
        var moodRusIds: MutableList<Int> = mutableListOf()
        var typeNames: MutableList<String> = mutableListOf()
        var typeRusIds: MutableList<Int> = mutableListOf()
        var voiceNames: MutableList<String> = mutableListOf()
        var voiceRusIds: MutableList<Int> = mutableListOf()
        var degreeOfComparisonNames: MutableList<String> = mutableListOf()
        var degreeOfComparisonRusIds: MutableList<Int> = mutableListOf()

        var isFirst: Boolean = false
    }

    private lateinit var adapterGender: AttributeAdapter
    private lateinit var adapterNumber: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterCase: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterTime: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterPerson: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterMood: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterType: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterVoice: ArrayAdapter<CharacteristicEntity>
    private lateinit var adapterDegreeOfComparison: ArrayAdapter<CharacteristicEntity>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacteristicBinding.inflate(inflater, container, false)


        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("block", 8)
            findNavController().navigate(
                R.id.action_dictionaryFragment_to_instructionFragment,
                argsToSend
            )
        }

        binding.buttonSave.setOnClickListener {
            var name: String
            var rusId: Int
            for ((i, el) in MyApp.language!!.grammar.varsGender.values.withIndex()) {
                name = genderNames[i]
                rusId = genderRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.GENDER, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsNumber.values.withIndex()) {
                name = numberNames[i]
                rusId = numberRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.NUMBER, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsCase.values.withIndex()) {
                name = caseNames[i]
                rusId = caseRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.CASE, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsTime.values.withIndex()) {
                name = timeNames[i]
                rusId = timeRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TIME, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsPerson.values.withIndex()) {
                name = personNames[i]
                rusId = personRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.PERSON, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsMood.values.withIndex()) {
                name = moodNames[i]
                rusId = moodRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.MOOD, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsType.values.withIndex()) {
                name = typeNames[i]
                rusId = typeRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TYPE, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsVoice.values.withIndex()) {
                name = voiceNames[i]
                rusId = voiceRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.VOICE, name, rusId)
                )
            }
            for ((i, el) in MyApp.language!!.grammar.varsDegreeOfComparison.values.withIndex()) {
                name = degreeOfComparisonNames[i]
                rusId = degreeOfComparisonRusIds[i]
                UpdateOptionUseCase.execute(
                    MyApp.language!!.grammar, el.characteristicId,
                    CharacteristicEntity(
                        el.characteristicId,
                        Attributes.DEGREE_OF_COMPARISON,
                        name,
                        rusId
                    )
                )
            }
            lifecycleScope.launch(Dispatchers.IO) {
                UpdateGrammarUseCase.execute(MyApp.language!!.grammar, LanguageRepositoryImpl())
            }
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getInt("lang", -1)) {
            -1 -> {
                Log.d("GrammarFragment", "goodbye!")
                findNavController().navigate(R.id.action_grammarFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
                grammar = MyApp.language!!.grammar

                gender = grammar.varsGender.values.toMutableList()
                number = grammar.varsNumber.values.toMutableList()
                case = grammar.varsCase.values.toMutableList()
                time = grammar.varsTime.values.toMutableList()
                person = grammar.varsPerson.values.toMutableList()
                mood = grammar.varsMood.values.toMutableList()
                type = grammar.varsType.values.toMutableList()
                voice = grammar.varsVoice.values.toMutableList()
                degreeOfComparison = grammar.varsDegreeOfComparison.values.toMutableList()
            }
        }
        Log.d("GrammarFragment", "idLang = $idLang")
        gender = MyApp.language!!.grammar.varsGender.values.toMutableList()
        number = MyApp.language!!.grammar.varsNumber.values.toMutableList()
        case = MyApp.language!!.grammar.varsCase.values.toMutableList()
        time = MyApp.language!!.grammar.varsTime.values.toMutableList()
        person = MyApp.language!!.grammar.varsPerson.values.toMutableList()
        mood = MyApp.language!!.grammar.varsMood.values.toMutableList()
        type = MyApp.language!!.grammar.varsType.values.toMutableList()
        voice = MyApp.language!!.grammar.varsVoice.values.toMutableList()
        degreeOfComparison =
            MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList()

        genderNames = MyApp.language!!.grammar.varsGender.values.toMutableList().map { it.name }
            .toMutableList()
        numberNames = MyApp.language!!.grammar.varsNumber.values.toMutableList().map { it.name }
            .toMutableList()
        caseNames = MyApp.language!!.grammar.varsCase.values.toMutableList().map { it.name }
            .toMutableList()
        timeNames = MyApp.language!!.grammar.varsTime.values.toMutableList().map { it.name }
            .toMutableList()
        personNames = MyApp.language!!.grammar.varsPerson.values.toMutableList().map { it.name }
            .toMutableList()
        moodNames = MyApp.language!!.grammar.varsMood.values.toMutableList().map { it.name }
            .toMutableList()
        typeNames = MyApp.language!!.grammar.varsType.values.toMutableList().map { it.name }
            .toMutableList()
        voiceNames = MyApp.language!!.grammar.varsVoice.values.toMutableList().map { it.name }
            .toMutableList()
        degreeOfComparisonNames =
            MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.name }.toMutableList()

        genderRusIds =
            MyApp.language!!.grammar.varsGender.values.toMutableList().map { it.russianId }
                .toMutableList()
        numberRusIds =
            MyApp.language!!.grammar.varsNumber.values.toMutableList().map { it.russianId }
                .toMutableList()
        caseRusIds =
            MyApp.language!!.grammar.varsCase.values.toMutableList().map { it.russianId }
                .toMutableList()
        timeRusIds =
            MyApp.language!!.grammar.varsTime.values.toMutableList().map { it.russianId }
                .toMutableList()
        personRusIds =
            MyApp.language!!.grammar.varsPerson.values.toMutableList().map { it.russianId }
                .toMutableList()
        moodRusIds =
            MyApp.language!!.grammar.varsMood.values.toMutableList().map { it.russianId }
                .toMutableList()
        typeRusIds =
            MyApp.language!!.grammar.varsType.values.toMutableList().map { it.russianId }
                .toMutableList()
        voiceRusIds =
            MyApp.language!!.grammar.varsVoice.values.toMutableList().map { it.russianId }
                .toMutableList()
        degreeOfComparisonRusIds =
            MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.russianId }.toMutableList()

        //list of genders
        adapterGender = AttributeAdapter(
            this,
            MyApp.language!!.grammar.varsGender.values.toMutableList(),
            0
        )
        binding.listViewGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        binding.buttonNewGender.setOnClickListener {
            val newGender = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.GENDER] ?: 0,
                Attributes.GENDER
            )
            AddOptionUseCase.execute(newGender, MyApp.language!!.grammar)
            genderNames.add("")
            genderRusIds.add(0)
            updateAdapter(0)
        }

        //list of numbers
        adapterNumber = AttributeAdapter(
            this,
            MyApp.language!!.grammar.varsNumber.values.toMutableList(),
            1
        )
        binding.listViewNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()

        binding.buttonNewNumber.setOnClickListener {
            val newNumber = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.NUMBER] ?: 0,
                Attributes.NUMBER
            )
            AddOptionUseCase.execute(newNumber, MyApp.language!!.grammar)
            numberNames.add("")
            numberRusIds.add(0)
            updateAdapter(1)
        }

        //list of cases
        adapterCase =
            AttributeAdapter(
                this,
                MyApp.language!!.grammar.varsCase.values.toMutableList(),
                2
            )
        binding.listViewCase.adapter = adapterCase
        adapterCase.notifyDataSetChanged()

        binding.buttonNewCase.setOnClickListener {
            val newCase = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.CASE] ?: 0,
                Attributes.CASE
            )
            AddOptionUseCase.execute(newCase, MyApp.language!!.grammar)
            caseNames.add("")
            caseRusIds.add(0)
            updateAdapter(2)
        }

        //list of times
        adapterTime =
            AttributeAdapter(
                this,
                MyApp.language!!.grammar.varsTime.values.toMutableList(),
                3
            )
        binding.listViewTime.adapter = adapterTime
        adapterTime.notifyDataSetChanged()

        binding.buttonNewTime.setOnClickListener {
            val newTime = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.TIME] ?: 0,
                Attributes.TIME
            )
            AddOptionUseCase.execute(newTime, MyApp.language!!.grammar)
            timeNames.add("")
            timeRusIds.add(0)
            updateAdapter(3)
        }

        //list of persons
        adapterPerson = AttributeAdapter(
            this,
            MyApp.language!!.grammar.varsPerson.values.toMutableList(),
            4
        )
        binding.listViewPerson.adapter = adapterPerson
        adapterPerson.notifyDataSetChanged()

        binding.buttonNewPerson.setOnClickListener {
            val newPerson = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.PERSON] ?: 0,
                Attributes.PERSON
            )
            AddOptionUseCase.execute(newPerson, MyApp.language!!.grammar)
            personNames.add("")
            personRusIds.add(0)
            updateAdapter(4)
        }

        //list of moods
        adapterMood =
            AttributeAdapter(
                this,
                MyApp.language!!.grammar.varsMood.values.toMutableList(),
                5
            )
        binding.listViewMood.adapter = adapterMood
        adapterMood.notifyDataSetChanged()

        binding.buttonNewMood.setOnClickListener {
            val newMood = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.MOOD] ?: 0,
                Attributes.MOOD
            )
            AddOptionUseCase.execute(newMood, MyApp.language!!.grammar)
            moodNames.add("")
            moodRusIds.add(0)
            updateAdapter(5)
        }

        //list of types
        adapterType =
            AttributeAdapter(
                this,
                MyApp.language!!.grammar.varsType.values.toMutableList(),
                6
            )
        binding.listViewType.adapter = adapterType
        adapterType.notifyDataSetChanged()

        binding.buttonNewType.setOnClickListener {
            val newType = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.TYPE] ?: 0,
                Attributes.TYPE
            )
            AddOptionUseCase.execute(newType, MyApp.language!!.grammar)
            typeNames.add("")
            typeRusIds.add(0)
            updateAdapter(6)
        }

        //list of voices
        adapterVoice =
            AttributeAdapter(
                this,
                MyApp.language!!.grammar.varsVoice.values.toMutableList(),
                7
            )
        binding.listViewVoice.adapter = adapterVoice
        adapterVoice.notifyDataSetChanged()

        binding.buttonNewVoice.setOnClickListener {
            val newVoice = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.VOICE] ?: 0,
                Attributes.VOICE
            )
            AddOptionUseCase.execute(newVoice, MyApp.language!!.grammar)
            voiceNames.add("")
            voiceRusIds.add(0)
            updateAdapter(7)
        }

        //list of voices
        adapterDegreeOfComparison = AttributeAdapter(
            this,
            MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList(),
            8
        )
        binding.listViewDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()

        binding.buttonNewDegreeOfComparison.setOnClickListener {
            val newDegreeOfComparison = CharacteristicEntity(
                MyApp.language!!.grammar.nextIds[Attributes.DEGREE_OF_COMPARISON] ?: 0,
                Attributes.DEGREE_OF_COMPARISON
            )
            AddOptionUseCase.execute(
                newDegreeOfComparison,
                MyApp.language!!.grammar
            )
            degreeOfComparisonNames.add("")
            degreeOfComparisonRusIds.add(0)
            updateAdapter(8)
        }

        return binding.root
    }

    fun updateAdapter(idCharacteristic: Int) {
        when (idCharacteristic) {
            0 -> {
                adapterGender = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsGender.values.toMutableList(),
                    0
                )
                binding.listViewGender.adapter = adapterGender
                adapterGender.notifyDataSetChanged()
            }

            1 -> {
                adapterNumber = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsNumber.values.toMutableList(),
                    1
                )
                binding.listViewNumber.adapter = adapterNumber
                adapterNumber.notifyDataSetChanged()
            }

            2 -> {
                adapterCase = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsCase.values.toMutableList(),
                    2
                )
                binding.listViewCase.adapter = adapterCase
                adapterCase.notifyDataSetChanged()
            }

            3 -> {
                adapterTime = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsTime.values.toMutableList(),
                    3
                )
                binding.listViewTime.adapter = adapterTime
                adapterTime.notifyDataSetChanged()
            }

            4 -> {
                adapterPerson = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsPerson.values.toMutableList(),
                    4
                )
                binding.listViewPerson.adapter = adapterPerson
                adapterPerson.notifyDataSetChanged()
            }

            5 -> {
                adapterMood = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsMood.values.toMutableList(),
                    5
                )
                binding.listViewMood.adapter = adapterMood
                adapterMood.notifyDataSetChanged()
            }

            6 -> {
                adapterType = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsType.values.toMutableList(),
                    6
                )
                binding.listViewType.adapter = adapterType
                adapterType.notifyDataSetChanged()
            }

            7 -> {
                adapterVoice = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsVoice.values.toMutableList(),
                    7
                )
                binding.listViewVoice.adapter = adapterVoice
                adapterVoice.notifyDataSetChanged()
            }

            else -> {
                adapterDegreeOfComparison = AttributeAdapter(
                    this,
                    MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList(),
                    8
                )
                binding.listViewDegreeOfComparison.adapter = adapterDegreeOfComparison
                adapterDegreeOfComparison.notifyDataSetChanged()
            }
        }
    }
}

class AttributeAdapter(
    context: CharacteristicFragment, listOfAttributes: MutableList<CharacteristicEntity>,
    idListAttribute: Int
) :
    ArrayAdapter<CharacteristicEntity>(
        context.requireContext(),
        R.layout.characteristic_line_activity, listOfAttributes
    ) {

    var idAttribute = idListAttribute
    var contextC = context
    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: CharacteristicEntity? = getItem(positionAttribute)
        if (newView == null) {
            newView =
                LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
        }

        GrammarFragment.isFirst = true
        var editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute1)
        val spinnerRus: Spinner = newView.findViewById(R.id.spinnerRusAttribute)

        //edittext is visible
        (editTextName as TextView).text = attribute!!.name
        editTextName.tag = positionAttribute
        GrammarFragment.isFirst = false

        editTextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (GrammarFragment.isFirst) return
                val updatedText = s.toString()
                val editTextTag = editTextName.tag
                if (positionAttribute == editTextTag as Int) {
                    when (idAttribute) {
                        0 -> {
                            CharacteristicFragment.genderNames[positionAttribute] = updatedText
                        }

                        1 -> {
                            CharacteristicFragment.numberNames[positionAttribute] = updatedText
                        }

                        2 -> {
                            CharacteristicFragment.caseNames[positionAttribute] = updatedText
                        }

                        3 -> {
                            CharacteristicFragment.timeNames[positionAttribute] = updatedText
                        }

                        4 -> {
                            CharacteristicFragment.personNames[positionAttribute] = updatedText
                        }

                        5 -> {
                            CharacteristicFragment.moodNames[positionAttribute] = updatedText
                        }

                        6 -> {
                            CharacteristicFragment.typeNames[positionAttribute] = updatedText
                        }

                        7 -> {
                            CharacteristicFragment.voiceNames[positionAttribute] = updatedText
                        }

                        else -> {
                            CharacteristicFragment.degreeOfComparisonNames[positionAttribute] = updatedText
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //spinner is working
        val spinnerAdapter: ArrayAdapter<String>
        when (idAttribute) {
            0 -> {
                spinnerAdapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, rusGender)
            }

            1 -> {
                spinnerAdapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, rusNumber)
            }

            2 -> {
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusCase)
            }

            3 -> {
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusTime)
            }

            4 -> {
                spinnerAdapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, rusPerson)
            }

            5 -> {
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusMood)
            }

            6 -> {
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusType)
            }

            7 -> {
                spinnerAdapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, rusVoice)
            }

            else -> {
                spinnerAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    rusDegreeOfComparison
                )
            }
        }
        spinnerRus.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        spinnerRus.setSelection(attribute.russianId)

        spinnerRus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (idAttribute) {
                    0 -> {
                        CharacteristicFragment.genderRusIds[positionAttribute] = position
                    }

                    1 -> {
                        CharacteristicFragment.numberRusIds[positionAttribute] = position
                    }

                    2 -> {
                        CharacteristicFragment.caseRusIds[positionAttribute] = position
                    }

                    3 -> {
                        CharacteristicFragment.timeRusIds[positionAttribute] = position
                    }

                    4 -> {
                        CharacteristicFragment.personRusIds[positionAttribute] = position
                    }

                    5 -> {
                        CharacteristicFragment.moodRusIds[positionAttribute] = position
                    }

                    6 -> {
                        CharacteristicFragment.typeRusIds[positionAttribute] = position
                    }

                    7 -> {
                        CharacteristicFragment.voiceRusIds[positionAttribute] = position
                    }

                    else -> {
                        CharacteristicFragment.degreeOfComparisonRusIds[positionAttribute] = position
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //button del is working
        val buttonDel: Button = newView.findViewById(R.id.buttonDel)
        if (positionAttribute == 0) {
            buttonDel.visibility = View.INVISIBLE
        } else {
            buttonDel.visibility = View.VISIBLE
        }
        buttonDel.setOnClickListener {
            when (idAttribute) {
                0 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsGender.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.genderNames.removeAt(positionAttribute)
                    CharacteristicFragment.genderRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                1 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsNumber.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.numberNames.removeAt(positionAttribute)
                    CharacteristicFragment.numberRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                2 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsCase.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.caseNames.removeAt(positionAttribute)
                    CharacteristicFragment.caseRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                3 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsTime.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.timeNames.removeAt(positionAttribute)
                    CharacteristicFragment.timeRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                4 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsPerson.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.personNames.removeAt(positionAttribute)
                    CharacteristicFragment.personRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                5 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsMood.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.moodNames.removeAt(positionAttribute)
                    CharacteristicFragment.moodRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                6 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsType.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.typeNames.removeAt(positionAttribute)
                    CharacteristicFragment.typeRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                7 -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsVoice.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.voiceNames.removeAt(positionAttribute)
                    CharacteristicFragment.voiceRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }

                else -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        DeleteOptionUseCase.execute(
                            MyApp.language!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute],
                            MyApp.language!!
                        )
                    }
                    CharacteristicFragment.degreeOfComparisonNames.removeAt(positionAttribute)
                    CharacteristicFragment.degreeOfComparisonRusIds.removeAt(positionAttribute)
                    contextC.updateAdapter(idAttribute)
                }
            }
        }


        return newView
    }
}