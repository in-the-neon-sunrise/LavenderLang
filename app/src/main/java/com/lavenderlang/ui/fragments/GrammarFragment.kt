package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.frontend.languages

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
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDao
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.language.GrammarEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.databinding.FragmentGrammarBinding
import com.lavenderlang.backend.service.*

class GrammarFragment : Fragment() {
    private lateinit var binding: FragmentGrammarBinding
    companion object {
        var idLang: Int = -1
        val grammarDao = GrammarDaoImpl()

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
        binding = FragmentGrammarBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            argsToSend.putInt("block", 8)
            findNavController().navigate(
                R.id.action_dictionaryFragment_to_instructionFragment,
                argsToSend
            )
        }
        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_grammarFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            findNavController().navigate(
                R.id.action_grammarFragment_to_languageFragment,
                argsToSend
            )
        }

        binding.buttonTranslator.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            findNavController().navigate(
                R.id.action_grammarFragment_to_translatorFragment,
                argsToSend
            )
        }

        binding.buttonSave.setOnClickListener {
            var name: String
            var rusId: Int
            for ((i, el) in languages[idLang]!!.grammar.varsGender.values.withIndex()) {
                name = genderNames[i]
                rusId = genderRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.GENDER, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsNumber.values.withIndex()) {
                name = numberNames[i]
                rusId = numberRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.NUMBER, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsCase.values.withIndex()) {
                name = caseNames[i]
                rusId = caseRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.CASE, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsTime.values.withIndex()) {
                name = timeNames[i]
                rusId = timeRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TIME, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsPerson.values.withIndex()) {
                name = personNames[i]
                rusId = personRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.PERSON, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsMood.values.withIndex()) {
                name = moodNames[i]
                rusId = moodRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.MOOD, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsType.values.withIndex()) {
                name = typeNames[i]
                rusId = typeRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TYPE, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsVoice.values.withIndex()) {
                name = voiceNames[i]
                rusId = voiceRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.VOICE, name, rusId)
                )
            }
            for ((i, el) in languages[idLang]!!.grammar.varsDegreeOfComparison.values.withIndex()) {
                name = degreeOfComparisonNames[i]
                rusId = degreeOfComparisonRusIds[i]
                grammarDao.updateOption(
                    languages[idLang]!!.grammar, el.characteristicId,
                    CharacteristicEntity(
                        el.characteristicId,
                        Attributes.DEGREE_OF_COMPARISON,
                        name,
                        rusId
                    )
                )
            }
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                Log.d("GrammarFragment", "goodbye!")
                findNavController().navigate(R.id.action_grammarFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
                grammar = languages[idLang]!!.grammar

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
        gender = languages[idLang]!!.grammar.varsGender.values.toMutableList()
        number = languages[idLang]!!.grammar.varsNumber.values.toMutableList()
        case = languages[idLang]!!.grammar.varsCase.values.toMutableList()
        time = languages[idLang]!!.grammar.varsTime.values.toMutableList()
        person = languages[idLang]!!.grammar.varsPerson.values.toMutableList()
        mood = languages[idLang]!!.grammar.varsMood.values.toMutableList()
        type = languages[idLang]!!.grammar.varsType.values.toMutableList()
        voice = languages[idLang]!!.grammar.varsVoice.values.toMutableList()
        degreeOfComparison =
            languages[idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList()

        genderNames = languages[idLang]!!.grammar.varsGender.values.toMutableList().map { it.name }
            .toMutableList()
        numberNames = languages[idLang]!!.grammar.varsNumber.values.toMutableList().map { it.name }
            .toMutableList()
        caseNames = languages[idLang]!!.grammar.varsCase.values.toMutableList().map { it.name }
            .toMutableList()
        timeNames = languages[idLang]!!.grammar.varsTime.values.toMutableList().map { it.name }
            .toMutableList()
        personNames = languages[idLang]!!.grammar.varsPerson.values.toMutableList().map { it.name }
            .toMutableList()
        moodNames = languages[idLang]!!.grammar.varsMood.values.toMutableList().map { it.name }
            .toMutableList()
        typeNames = languages[idLang]!!.grammar.varsType.values.toMutableList().map { it.name }
            .toMutableList()
        voiceNames = languages[idLang]!!.grammar.varsVoice.values.toMutableList().map { it.name }
            .toMutableList()
        degreeOfComparisonNames =
            languages[idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.name }.toMutableList()

        genderRusIds =
            languages[idLang]!!.grammar.varsGender.values.toMutableList().map { it.russianId }
                .toMutableList()
        numberRusIds =
            languages[idLang]!!.grammar.varsNumber.values.toMutableList().map { it.russianId }
                .toMutableList()
        caseRusIds =
            languages[idLang]!!.grammar.varsCase.values.toMutableList().map { it.russianId }
                .toMutableList()
        timeRusIds =
            languages[idLang]!!.grammar.varsTime.values.toMutableList().map { it.russianId }
                .toMutableList()
        personRusIds =
            languages[idLang]!!.grammar.varsPerson.values.toMutableList().map { it.russianId }
                .toMutableList()
        moodRusIds =
            languages[idLang]!!.grammar.varsMood.values.toMutableList().map { it.russianId }
                .toMutableList()
        typeRusIds =
            languages[idLang]!!.grammar.varsType.values.toMutableList().map { it.russianId }
                .toMutableList()
        voiceRusIds =
            languages[idLang]!!.grammar.varsVoice.values.toMutableList().map { it.russianId }
                .toMutableList()
        degreeOfComparisonRusIds =
            languages[idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.russianId }.toMutableList()

        //list of genders
        adapterGender = AttributeAdapter(
            requireContext(),
            languages[idLang]!!.grammar.varsGender.values.toMutableList(),
            0
        )
        binding.listViewGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        binding.buttonNewGender.setOnClickListener {
            val newGender = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.GENDER] ?: 0,
                Attributes.GENDER
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newGender)
            genderNames.add("")
            genderRusIds.add(0)
            updateAdapter(0)
        }

        //list of numbers
        adapterNumber = AttributeAdapter(
            requireContext(),
            languages[idLang]!!.grammar.varsNumber.values.toMutableList(),
            1
        )
        binding.listViewNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()

        binding.buttonNewNumber.setOnClickListener {
            val newNumber = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.NUMBER] ?: 0,
                Attributes.NUMBER
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newNumber)
            numberNames.add("")
            numberRusIds.add(0)
            updateAdapter(1)
        }

        //list of cases
        adapterCase =
            AttributeAdapter(requireContext(), languages[idLang]!!.grammar.varsCase.values.toMutableList(), 2)
        binding.listViewCase.adapter = adapterCase
        adapterCase.notifyDataSetChanged()

        binding.buttonNewCase.setOnClickListener {
            val newCase = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.CASE] ?: 0,
                Attributes.CASE
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newCase)
            caseNames.add("")
            caseRusIds.add(0)
            updateAdapter(2)
        }

        //list of times
        adapterTime =
            AttributeAdapter(requireContext(), languages[idLang]!!.grammar.varsTime.values.toMutableList(), 3)
        binding.listViewTime.adapter = adapterTime
        adapterTime.notifyDataSetChanged()

        binding.buttonNewTime.setOnClickListener {
            val newTime = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.TIME] ?: 0,
                Attributes.TIME
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newTime)
            timeNames.add("")
            timeRusIds.add(0)
            updateAdapter(3)
        }

        //list of persons
        adapterPerson = AttributeAdapter(
            requireContext(),
            languages[idLang]!!.grammar.varsPerson.values.toMutableList(),
            4
        )
        binding.listViewPerson.adapter = adapterPerson
        adapterPerson.notifyDataSetChanged()

        binding.buttonNewPerson.setOnClickListener {
            val newPerson = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.PERSON] ?: 0,
                Attributes.PERSON
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newPerson)
            personNames.add("")
            personRusIds.add(0)
            updateAdapter(4)
        }

        //list of moods
        adapterMood =
            AttributeAdapter(requireContext(), languages[idLang]!!.grammar.varsMood.values.toMutableList(), 5)
        binding.listViewMood.adapter = adapterMood
        adapterMood.notifyDataSetChanged()

        binding.buttonNewMood.setOnClickListener {
            val newMood = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.MOOD] ?: 0,
                Attributes.MOOD
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newMood)
            moodNames.add("")
            moodRusIds.add(0)
            updateAdapter(5)
        }

        //list of types
        adapterType =
            AttributeAdapter(requireContext(), languages[idLang]!!.grammar.varsType.values.toMutableList(), 6)
        binding.listViewType.adapter = adapterType
        adapterType.notifyDataSetChanged()

        binding.buttonNewType.setOnClickListener {
            val newType = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.TYPE] ?: 0,
                Attributes.TYPE
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newType)
            typeNames.add("")
            typeRusIds.add(0)
            updateAdapter(6)
        }

        //list of voices
        adapterVoice =
            AttributeAdapter(requireContext(), languages[idLang]!!.grammar.varsVoice.values.toMutableList(), 7)
        binding.listViewVoice.adapter = adapterVoice
        adapterVoice.notifyDataSetChanged()

        binding.buttonNewVoice.setOnClickListener {
            val newVoice = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.VOICE] ?: 0,
                Attributes.VOICE
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newVoice)
            voiceNames.add("")
            voiceRusIds.add(0)
            updateAdapter(7)
        }

        //list of voices
        adapterDegreeOfComparison = AttributeAdapter(
            requireContext(),
            languages[idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList(),
            8
        )
        binding.listViewDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()

        binding.buttonNewDegreeOfComparison.setOnClickListener {
            val newDegreeOfComparison = CharacteristicEntity(
                languages[idLang]!!.grammar.nextIds[Attributes.DEGREE_OF_COMPARISON] ?: 0,
                Attributes.DEGREE_OF_COMPARISON
            )
            grammarDao.addOption(languages[idLang]!!.grammar, newDegreeOfComparison)
            degreeOfComparisonNames.add("")
            degreeOfComparisonRusIds.add(0)
            updateAdapter(8)
        }


        //button new grammar rule listener
        binding.buttonNewGrammarRule.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("lang", idLang)
            argsToSend.putInt("grammarRule", -1)
            findNavController().navigate(
                R.id.action_grammarFragment_to_grammarRuleFragment,
                argsToSend
            )
        }

        //list of rules
        val adapterGrammarRules: ArrayAdapter<GrammarRuleEntity> =
            GrammarRuleAdapter(requireContext(), languages[idLang]!!.grammar.grammarRules.toMutableList())
        binding.listViewGrammarRules.adapter = adapterGrammarRules
        adapterGrammarRules.notifyDataSetChanged()

        //click listener
        binding.listViewGrammarRules.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val argsToSend = Bundle()
                argsToSend.putInt("lang", idLang)
                argsToSend.putInt("grammarRule", position)
                findNavController().navigate(
                    R.id.action_grammarFragment_to_grammarRuleFragment,
                    argsToSend
                )
            }

        return binding.root
    }
    fun updateAdapter(idCharacteristic: Int) {
        when (idCharacteristic) {
            0 -> {
                adapterGender = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsGender.values.toMutableList(),
                    0
                )
                binding.listViewGender.adapter = adapterGender
                adapterGender.notifyDataSetChanged()
            }

            1 -> {
                adapterNumber = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsNumber.values.toMutableList(),
                    1
                )
                binding.listViewNumber.adapter = adapterNumber
                adapterNumber.notifyDataSetChanged()
            }

            2 -> {
                adapterCase = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsCase.values.toMutableList(),
                    2
                )
                binding.listViewCase.adapter = adapterCase
                adapterCase.notifyDataSetChanged()
            }

            3 -> {
                adapterTime = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsTime.values.toMutableList(),
                    3
                )
                binding.listViewTime.adapter = adapterTime
                adapterTime.notifyDataSetChanged()
            }

            4 -> {
                adapterPerson = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsPerson.values.toMutableList(),
                    4
                )
                binding.listViewPerson.adapter = adapterPerson
                adapterPerson.notifyDataSetChanged()
            }

            5 -> {
                adapterMood = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsMood.values.toMutableList(),
                    5
                )
                binding.listViewMood.adapter = adapterMood
                adapterMood.notifyDataSetChanged()
            }

            6 -> {
                adapterType = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsType.values.toMutableList(),
                    6
                )
                binding.listViewType.adapter = adapterType
                adapterType.notifyDataSetChanged()
            }

            7 -> {
                adapterVoice = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsVoice.values.toMutableList(),
                    7
                )
                binding.listViewVoice.adapter = adapterVoice
                adapterVoice.notifyDataSetChanged()
            }

            else -> {
                adapterDegreeOfComparison = AttributeAdapter(
                    requireContext(),
                    languages[idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList(),
                    8
                )
                binding.listViewDegreeOfComparison.adapter = adapterDegreeOfComparison
                adapterDegreeOfComparison.notifyDataSetChanged()
            }
        }
    }
}

class AttributeAdapter(
    context: Context, listOfAttributes: MutableList<CharacteristicEntity>,
    idListAttribute: Int
) :
    ArrayAdapter<CharacteristicEntity>(
        context,
        R.layout.characteristic_line_activity, listOfAttributes
    ) {

    var idAttribute = idListAttribute

    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: CharacteristicEntity? = getItem(positionAttribute)
        if (newView == null) {
            newView =
                LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
        }

        GrammarFragment.isFirst = true
        var editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
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

                            GrammarFragment.genderNames[positionAttribute] = updatedText
                        }

                        1 -> {
                            GrammarFragment.numberNames[positionAttribute] = updatedText
                        }

                        2 -> {
                            GrammarFragment.caseNames[positionAttribute] = updatedText
                        }

                        3 -> {
                            GrammarFragment.timeNames[positionAttribute] = updatedText
                        }

                        4 -> {
                            GrammarFragment.personNames[positionAttribute] = updatedText
                        }

                        5 -> {
                            GrammarFragment.moodNames[positionAttribute] = updatedText
                        }

                        6 -> {
                            GrammarFragment.typeNames[positionAttribute] = updatedText
                        }

                        7 -> {
                            GrammarFragment.voiceNames[positionAttribute] = updatedText
                        }

                        else -> {
                            GrammarFragment.degreeOfComparisonNames[positionAttribute] = updatedText
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
                        GrammarFragment.genderRusIds[positionAttribute] = position
                    }

                    1 -> {
                        GrammarFragment.numberRusIds[positionAttribute] = position
                    }

                    2 -> {
                        GrammarFragment.caseRusIds[positionAttribute] = position
                    }

                    3 -> {
                        GrammarFragment.timeRusIds[positionAttribute] = position
                    }

                    4 -> {
                        GrammarFragment.personRusIds[positionAttribute] = position
                    }

                    5 -> {
                        GrammarFragment.moodRusIds[positionAttribute] = position
                    }

                    6 -> {
                        GrammarFragment.typeRusIds[positionAttribute] = position
                    }

                    7 -> {
                        GrammarFragment.voiceRusIds[positionAttribute] = position
                    }

                    else -> {
                        GrammarFragment.degreeOfComparisonRusIds[positionAttribute] = position
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //button del is working
        val buttonDel: Button = newView.findViewById(R.id.buttonDel)
        if (positionAttribute == 0) {
            buttonDel.visibility = View.GONE
        } else {
            buttonDel.visibility = View.VISIBLE
        }
        val grammarDao = GrammarDaoImpl()
        buttonDel.setOnClickListener {
            when (idAttribute) {
                0 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsGender.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.genderNames.removeAt(positionAttribute)
                    GrammarFragment.genderRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                1 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.numberNames.removeAt(positionAttribute)
                    GrammarFragment.numberRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                2 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsCase.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.caseNames.removeAt(positionAttribute)
                    GrammarFragment.caseRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                3 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsTime.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.timeNames.removeAt(positionAttribute)
                    GrammarFragment.timeRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                4 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.personNames.removeAt(positionAttribute)
                    GrammarFragment.personRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                5 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsMood.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.moodNames.removeAt(positionAttribute)
                    GrammarFragment.moodRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                6 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsType.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.typeNames.removeAt(positionAttribute)
                    GrammarFragment.typeRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                7 -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.voiceNames.removeAt(positionAttribute)
                    GrammarFragment.voiceRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }

                else -> {
                    grammarDao.deleteOption(
                        languages[GrammarFragment.idLang]!!.grammar,
                        languages[GrammarFragment.idLang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute]
                    )
                    GrammarFragment.degreeOfComparisonNames.removeAt(positionAttribute)
                    GrammarFragment.degreeOfComparisonRusIds.removeAt(positionAttribute)
                    (context as GrammarFragment).updateAdapter(idAttribute)
                }
            }
        }


        return newView
    }
}

private class GrammarRuleAdapter(context: Context, listOfRules: MutableList<GrammarRuleEntity>) :
    ArrayAdapter<GrammarRuleEntity>(context, R.layout.grammar_rule_line_activity, listOfRules) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val grammarRule: GrammarRuleEntity? = getItem(position)
        if (newView == null) {
            newView =
                LayoutInflater.from(context).inflate(R.layout.grammar_rule_line_activity, null)
        }

        val grammarRuleDao: GrammarRuleDao = GrammarRuleDaoImpl()
        //textview is visible
        val unchangeableAttributes: TextView =
            newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = grammarRuleDao.getOrigInfo(grammarRule!!)
        changeableAttributes.text = grammarRuleDao.getResultInfo(grammarRule)

        return newView
    }
}