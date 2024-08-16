package com.lavenderlang.legacy.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDao
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.backend.service.*
import com.lavenderlang.domain.rusCase
import com.lavenderlang.domain.rusDegreeOfComparison
import com.lavenderlang.domain.rusGender
import com.lavenderlang.domain.rusMood
import com.lavenderlang.domain.rusNumber
import com.lavenderlang.domain.rusPerson
import com.lavenderlang.domain.rusTime
import com.lavenderlang.domain.rusType
import com.lavenderlang.domain.rusVoice


class GrammarActivity : AppCompatActivity() {
    companion object {
        var id_lang: Int = -1
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_activity)
        if (getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener {
            val intent = Intent(this@GrammarActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 8)
            startActivity(intent)
        }
        //bottom navigation menu
        val buttonHome: Button = findViewById(R.id.buttonHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonLanguage: Button = findViewById(R.id.buttonLanguage)
        buttonLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        //how it was started?
        when (val lang = intent.getIntExtra("lang", id_lang)) {
            -1 -> {
                val intent = Intent(this@GrammarActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
                grammar = Serializer.getInstance().deserializeGrammar(
                    LanguageRepository().getLanguage(this, id_lang).grammar)

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

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            var name: String
            var rusId: Int
            for ((i, el) in grammar.varsGender.values.withIndex()) {
                name = genderNames[i]
                rusId = genderRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.GENDER, name, rusId)
                )
            }
            for ((i, el) in grammar.varsNumber.values.withIndex()) {
                name = numberNames[i]
                rusId = numberRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.NUMBER, name, rusId)
                )
            }
            for ((i, el) in grammar.varsCase.values.withIndex()) {
                name = caseNames[i]
                rusId = caseRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.CASE, name, rusId)
                )
            }
            for ((i, el) in grammar.varsTime.values.withIndex()) {
                name = timeNames[i]
                rusId = timeRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TIME, name, rusId)
                )
            }
            for ((i, el) in grammar.varsPerson.values.withIndex()) {
                name = personNames[i]
                rusId = personRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.PERSON, name, rusId)
                )
            }
            for ((i, el) in grammar.varsMood.values.withIndex()) {
                name = moodNames[i]
                rusId = moodRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.MOOD, name, rusId)
                )
            }
            for ((i, el) in grammar.varsType.values.withIndex()) {
                name = typeNames[i]
                rusId = typeRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.TYPE, name, rusId)
                )
            }
            for ((i, el) in grammar.varsVoice.values.withIndex()) {
                name = voiceNames[i]
                rusId = voiceRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(el.characteristicId, Attributes.VOICE, name, rusId)
                )
            }
            for ((i, el) in grammar.varsDegreeOfComparison.values.withIndex()) {
                name = degreeOfComparisonNames[i]
                rusId = degreeOfComparisonRusIds[i]
                grammarDao.updateOption(
                    grammar, el.characteristicId,
                    CharacteristicEntity(
                        el.characteristicId,
                        Attributes.DEGREE_OF_COMPARISON,
                        name,
                        rusId
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        gender = grammar.varsGender.values.toMutableList()
        number = grammar.varsNumber.values.toMutableList()
        case = grammar.varsCase.values.toMutableList()
        time = grammar.varsTime.values.toMutableList()
        person = grammar.varsPerson.values.toMutableList()
        mood = grammar.varsMood.values.toMutableList()
        type = grammar.varsType.values.toMutableList()
        voice = grammar.varsVoice.values.toMutableList()
        degreeOfComparison = grammar.varsDegreeOfComparison.values.toMutableList()

        genderNames = grammar.varsGender.values.toMutableList().map { it.name }
            .toMutableList()
        numberNames = grammar.varsNumber.values.toMutableList().map { it.name }
            .toMutableList()
        caseNames = grammar.varsCase.values.toMutableList().map { it.name }
            .toMutableList()
        timeNames = grammar.varsTime.values.toMutableList().map { it.name }
            .toMutableList()
        personNames = grammar.varsPerson.values.toMutableList().map { it.name }
            .toMutableList()
        moodNames = grammar.varsMood.values.toMutableList().map { it.name }
            .toMutableList()
        typeNames = grammar.varsType.values.toMutableList().map { it.name }
            .toMutableList()
        voiceNames = grammar.varsVoice.values.toMutableList().map { it.name }
            .toMutableList()
        degreeOfComparisonNames = grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.name }.toMutableList()

        genderRusIds = grammar.varsGender.values.toMutableList().map { it.russianId }
                .toMutableList()
        numberRusIds = grammar.varsNumber.values.toMutableList().map { it.russianId }
                .toMutableList()
        caseRusIds = grammar.varsCase.values.toMutableList().map { it.russianId }
                .toMutableList()
        timeRusIds = grammar.varsTime.values.toMutableList().map { it.russianId }
                .toMutableList()
        personRusIds = grammar.varsPerson.values.toMutableList().map { it.russianId }
                .toMutableList()
        moodRusIds = grammar.varsMood.values.toMutableList().map { it.russianId }
                .toMutableList()
        typeRusIds = grammar.varsType.values.toMutableList().map { it.russianId }
                .toMutableList()
        voiceRusIds = grammar.varsVoice.values.toMutableList().map { it.russianId }
                .toMutableList()
        degreeOfComparisonRusIds = grammar.varsDegreeOfComparison.values.toMutableList()
                .map { it.russianId }.toMutableList()

        //list of genders
        val listGender: ListView = findViewById(R.id.listViewGender)
        adapterGender = AttributeAdapter(
            this,
            grammar.varsGender.values.toMutableList(),
            0
        )
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        val buttonNewGender: Button = findViewById(R.id.buttonNewGender)
        buttonNewGender.setOnClickListener {
            val newGender = CharacteristicEntity(
                grammar.nextIds[Attributes.GENDER] ?: 0,
                Attributes.GENDER
            )
            grammarDao.addOption(grammar, newGender)
            genderNames.add("")
            genderRusIds.add(0)
            updateAdapter(0)
        }

        //list of numbers
        val listNumber: ListView = findViewById(R.id.listViewNumber)
        adapterNumber = AttributeAdapter(
            this,
            grammar.varsNumber.values.toMutableList(),
            1
        )
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()

        val buttonNewNumber: Button = findViewById(R.id.buttonNewNumber)
        buttonNewNumber.setOnClickListener {
            val newNumber = CharacteristicEntity(
                grammar.nextIds[Attributes.NUMBER] ?: 0,
                Attributes.NUMBER
            )
            grammarDao.addOption(grammar, newNumber)
            numberNames.add("")
            numberRusIds.add(0)
            updateAdapter(1)
        }

        //list of cases
        val listCase: ListView = findViewById(R.id.listViewCase)
        adapterCase =
            AttributeAdapter(this, grammar.varsCase.values.toMutableList(), 2)
        listCase.adapter = adapterCase
        adapterCase.notifyDataSetChanged()

        val buttonNewCase: Button = findViewById(R.id.buttonNewCase)
        buttonNewCase.setOnClickListener {
            val newCase = CharacteristicEntity(
                grammar.nextIds[Attributes.CASE] ?: 0,
                Attributes.CASE
            )
            grammarDao.addOption(grammar, newCase)
            caseNames.add("")
            caseRusIds.add(0)
            updateAdapter(2)
        }

        //list of times
        val listTime: ListView = findViewById(R.id.listViewTime)
        adapterTime =
            AttributeAdapter(this, grammar.varsTime.values.toMutableList(), 3)
        listTime.adapter = adapterTime
        adapterTime.notifyDataSetChanged()

        val buttonNewTime: Button = findViewById(R.id.buttonNewTime)
        buttonNewTime.setOnClickListener {
            val newTime = CharacteristicEntity(
                grammar.nextIds[Attributes.TIME] ?: 0,
                Attributes.TIME
            )
            grammarDao.addOption(grammar, newTime)
            timeNames.add("")
            timeRusIds.add(0)
            updateAdapter(3)
        }

        //list of persons
        val listPerson: ListView = findViewById(R.id.listViewPerson)
        adapterPerson = AttributeAdapter(
            this,
            grammar.varsPerson.values.toMutableList(),
            4
        )
        listPerson.adapter = adapterPerson
        adapterPerson.notifyDataSetChanged()

        val buttonNewPerson: Button = findViewById(R.id.buttonNewPerson)
        buttonNewPerson.setOnClickListener {
            val newPerson = CharacteristicEntity(
                grammar.nextIds[Attributes.PERSON] ?: 0,
                Attributes.PERSON
            )
            grammarDao.addOption(grammar, newPerson)
            personNames.add("")
            personRusIds.add(0)
            updateAdapter(4)
        }

        //list of moods
        val listMood: ListView = findViewById(R.id.listViewMood)
        adapterMood =
            AttributeAdapter(this, grammar.varsMood.values.toMutableList(), 5)
        listMood.adapter = adapterMood
        adapterMood.notifyDataSetChanged()

        val buttonNewMood: Button = findViewById(R.id.buttonNewMood)
        buttonNewMood.setOnClickListener {
            val newMood = CharacteristicEntity(
                grammar.nextIds[Attributes.MOOD] ?: 0,
                Attributes.MOOD
            )
            grammarDao.addOption(grammar, newMood)
            moodNames.add("")
            moodRusIds.add(0)
            updateAdapter(5)
        }

        //list of types
        val listType: ListView = findViewById(R.id.listViewType)
        adapterType =
            AttributeAdapter(this, grammar.varsType.values.toMutableList(), 6)
        listType.adapter = adapterType
        adapterType.notifyDataSetChanged()

        val buttonNewType: Button = findViewById(R.id.buttonNewType)
        buttonNewType.setOnClickListener {
            val newType = CharacteristicEntity(
                grammar.nextIds[Attributes.TYPE] ?: 0,
                Attributes.TYPE
            )
            grammarDao.addOption(grammar, newType)
            typeNames.add("")
            typeRusIds.add(0)
            updateAdapter(6)
        }

        //list of voices
        val listVoice: ListView = findViewById(R.id.listViewVoice)
        adapterVoice =
            AttributeAdapter(this, grammar.varsVoice.values.toMutableList(), 7)
        listVoice.adapter = adapterVoice
        adapterVoice.notifyDataSetChanged()

        val buttonNewVoice: Button = findViewById(R.id.buttonNewVoice)
        buttonNewVoice.setOnClickListener {
            val newVoice = CharacteristicEntity(
                grammar.nextIds[Attributes.VOICE] ?: 0,
                Attributes.VOICE
            )
            grammarDao.addOption(grammar, newVoice)
            voiceNames.add("")
            voiceRusIds.add(0)
            updateAdapter(7)
        }

        //list of voices
        val listDegreeOfComparison: ListView = findViewById(R.id.listViewDegreeOfComparison)
        adapterDegreeOfComparison = AttributeAdapter(
            this,
            grammar.varsDegreeOfComparison.values.toMutableList(),
            8
        )
        listDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()

        val buttonNewDegreeOfComparison: Button = findViewById(R.id.buttonNewDegreeOfComparison)
        buttonNewDegreeOfComparison.setOnClickListener {
            val newDegreeOfComparison = CharacteristicEntity(
                grammar.nextIds[Attributes.DEGREE_OF_COMPARISON] ?: 0,
                Attributes.DEGREE_OF_COMPARISON
            )
            grammarDao.addOption(grammar, newDegreeOfComparison)
            degreeOfComparisonNames.add("")
            degreeOfComparisonRusIds.add(0)
            updateAdapter(8)
        }


        //button new grammar rule listener
        val buttonNewRule: Button = findViewById(R.id.buttonNewGrammarRule)
        buttonNewRule.setOnClickListener {
            val intent = Intent(this@GrammarActivity, GrammarRuleActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("grammarRule", -1)
            startActivity(intent)
        }

        //list of rules
        val listGrammarRules: ListView = findViewById(R.id.listViewGrammarRules)
        val adapterGrammarRules: ArrayAdapter<GrammarRuleEntity> =
            GrammarRuleAdapter(this, grammar.grammarRules.toMutableList())
        listGrammarRules.adapter = adapterGrammarRules
        adapterGrammarRules.notifyDataSetChanged()

        //click listener
        listGrammarRules.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this@GrammarActivity, GrammarRuleActivity::class.java)
                intent.putExtra("lang", id_lang)
                intent.putExtra("grammarRule", position)

                startActivity(intent)
            }
    }

    fun updateAdapter(idCharacteristic: Int) {
        when (idCharacteristic) {
            0 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewGender)
                adapterGender = AttributeAdapter(
                    this,
                    grammar.varsGender.values.toMutableList(),
                    0
                )
                listCharacteristic.adapter = adapterGender
                adapterGender.notifyDataSetChanged()
            }

            1 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewNumber)
                adapterNumber = AttributeAdapter(
                    this,
                    grammar.varsNumber.values.toMutableList(),
                    1
                )
                listCharacteristic.adapter = adapterNumber
                adapterNumber.notifyDataSetChanged()
            }

            2 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewCase)
                adapterCase = AttributeAdapter(
                    this,
                    grammar.varsCase.values.toMutableList(),
                    2
                )
                listCharacteristic.adapter = adapterCase
                adapterCase.notifyDataSetChanged()
            }

            3 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewTime)
                adapterTime = AttributeAdapter(
                    this,
                    grammar.varsTime.values.toMutableList(),
                    3
                )
                listCharacteristic.adapter = adapterTime
                adapterTime.notifyDataSetChanged()
            }

            4 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewPerson)
                adapterPerson = AttributeAdapter(
                    this,
                    grammar.varsPerson.values.toMutableList(),
                    4
                )
                listCharacteristic.adapter = adapterPerson
                adapterPerson.notifyDataSetChanged()
            }

            5 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewMood)
                adapterMood = AttributeAdapter(
                    this,
                    grammar.varsMood.values.toMutableList(),
                    5
                )
                listCharacteristic.adapter = adapterMood
                adapterMood.notifyDataSetChanged()
            }

            6 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewType)
                adapterType = AttributeAdapter(
                    this,
                    grammar.varsType.values.toMutableList(),
                    6
                )
                listCharacteristic.adapter = adapterType
                adapterType.notifyDataSetChanged()
            }

            7 -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewVoice)
                adapterVoice = AttributeAdapter(
                    this,
                    grammar.varsVoice.values.toMutableList(),
                    7
                )
                listCharacteristic.adapter = adapterVoice
                adapterVoice.notifyDataSetChanged()
            }

            else -> {
                val listCharacteristic: ListView = findViewById(R.id.listViewDegreeOfComparison)
                adapterDegreeOfComparison = AttributeAdapter(
                    this,
                    grammar.varsDegreeOfComparison.values.toMutableList(),
                    8
                )
                listCharacteristic.adapter = adapterDegreeOfComparison
                adapterDegreeOfComparison.notifyDataSetChanged()
            }
        }
    }

    override fun finish() {
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}

private class AttributeAdapter(
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

        GrammarActivity.isFirst = true
        var editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
        val spinnerRus: Spinner = newView.findViewById(R.id.spinnerRusAttribute)

        //edittext is visible
        (editTextName as TextView).text = attribute!!.name
        editTextName.tag = positionAttribute
        GrammarActivity.isFirst = false

        editTextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (GrammarActivity.isFirst) return
                val updatedText = s.toString()
                val editTextTag = editTextName.tag
                if (positionAttribute == editTextTag as Int) {
                    when (idAttribute) {
                        0 -> {

                            GrammarActivity.genderNames[positionAttribute] = updatedText
                        }

                        1 -> {
                            GrammarActivity.numberNames[positionAttribute] = updatedText
                        }

                        2 -> {
                            GrammarActivity.caseNames[positionAttribute] = updatedText
                        }

                        3 -> {
                            GrammarActivity.timeNames[positionAttribute] = updatedText
                        }

                        4 -> {
                            GrammarActivity.personNames[positionAttribute] = updatedText
                        }

                        5 -> {
                            GrammarActivity.moodNames[positionAttribute] = updatedText
                        }

                        6 -> {
                            GrammarActivity.typeNames[positionAttribute] = updatedText
                        }

                        7 -> {
                            GrammarActivity.voiceNames[positionAttribute] = updatedText
                        }

                        else -> {
                            GrammarActivity.degreeOfComparisonNames[positionAttribute] = updatedText
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
                        GrammarActivity.genderRusIds[positionAttribute] = position
                    }

                    1 -> {
                        GrammarActivity.numberRusIds[positionAttribute] = position
                    }

                    2 -> {
                        GrammarActivity.caseRusIds[positionAttribute] = position
                    }

                    3 -> {
                        GrammarActivity.timeRusIds[positionAttribute] = position
                    }

                    4 -> {
                        GrammarActivity.personRusIds[positionAttribute] = position
                    }

                    5 -> {
                        GrammarActivity.moodRusIds[positionAttribute] = position
                    }

                    6 -> {
                        GrammarActivity.typeRusIds[positionAttribute] = position
                    }

                    7 -> {
                        GrammarActivity.voiceRusIds[positionAttribute] = position
                    }

                    else -> {
                        GrammarActivity.degreeOfComparisonRusIds[positionAttribute] = position
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
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsGender.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.genderNames.removeAt(positionAttribute)
                    GrammarActivity.genderRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                1 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsNumber.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.numberNames.removeAt(positionAttribute)
                    GrammarActivity.numberRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                2 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsCase.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.caseNames.removeAt(positionAttribute)
                    GrammarActivity.caseRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                3 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsTime.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.timeNames.removeAt(positionAttribute)
                    GrammarActivity.timeRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                4 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsPerson.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.personNames.removeAt(positionAttribute)
                    GrammarActivity.personRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                5 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsMood.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.moodNames.removeAt(positionAttribute)
                    GrammarActivity.moodRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                6 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsType.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.typeNames.removeAt(positionAttribute)
                    GrammarActivity.typeRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                7 -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsVoice.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.voiceNames.removeAt(positionAttribute)
                    GrammarActivity.voiceRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }

                else -> {
                    grammarDao.deleteOption(
                        GrammarActivity.grammar,
                        GrammarActivity.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute]
                    )
                    GrammarActivity.degreeOfComparisonNames.removeAt(positionAttribute)
                    GrammarActivity.degreeOfComparisonRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
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