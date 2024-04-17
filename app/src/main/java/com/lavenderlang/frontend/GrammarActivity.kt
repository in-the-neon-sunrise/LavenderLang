package com.lavenderlang.frontend

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.rule.GrammarRuleDao
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.language.GrammarEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.service.*


class GrammarActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        val grammarDao = GrammarDaoImpl()
        var grammar: GrammarEntity = languages[this.id_lang]!!.grammar

        var gender:MutableList<CharacteristicEntity> = grammar.varsGender.values.toMutableList()
        var number:MutableList<CharacteristicEntity> = grammar.varsNumber.values.toMutableList()
        var case:MutableList<CharacteristicEntity> = grammar.varsCase.values.toMutableList()
        var time:MutableList<CharacteristicEntity> = grammar.varsTime.values.toMutableList()
        var person:MutableList<CharacteristicEntity> = grammar.varsPerson.values.toMutableList()
        var mood:MutableList<CharacteristicEntity> = grammar.varsMood.values.toMutableList()
        var type:MutableList<CharacteristicEntity> = grammar.varsType.values.toMutableList()
        var voice:MutableList<CharacteristicEntity> = grammar.varsVoice.values.toMutableList()
        var degreeOfComparison:MutableList<CharacteristicEntity> = grammar.varsDegreeOfComparison.values.toMutableList()

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@GrammarActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            var name: String
            var rusId: Int
            Toast.makeText(this, genderNames.toString(), Toast.LENGTH_SHORT).show()
            for(i in 0..<languages[id_lang]!!.grammar.varsGender.values.size) {
                name = genderNames[i]
                rusId= genderRusIds[i]
                grammarDao.updateOption(
                    languages[id_lang]!!.grammar, i,
                    CharacteristicEntity(i, Attributes.GENDER, name, rusId))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", id_lang)) {
            -1 -> {
                val intent = Intent(this@GrammarActivity, LanguageActivity::class.java)
                startActivity(intent)
            }

            else -> {
                id_lang = lang
            }
        }
    }
    override fun onResume() {
        super.onResume()

        gender = languages[id_lang]!!.grammar.varsGender.values.toMutableList()
        number = languages[id_lang]!!.grammar.varsNumber.values.toMutableList()
        case = languages[id_lang]!!.grammar.varsCase.values.toMutableList()
        time = languages[id_lang]!!.grammar.varsTime.values.toMutableList()
        person = languages[id_lang]!!.grammar.varsPerson.values.toMutableList()
        mood = languages[id_lang]!!.grammar.varsMood.values.toMutableList()
        type = languages[id_lang]!!.grammar.varsType.values.toMutableList()
        voice = languages[id_lang]!!.grammar.varsVoice.values.toMutableList()
        degreeOfComparison = languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()

        genderNames = languages[id_lang]!!.grammar.varsGender.values.toMutableList().map { it.name }.toMutableList()
        numberNames = languages[id_lang]!!.grammar.varsNumber.values.toMutableList().map { it.name }.toMutableList()
        caseNames = languages[id_lang]!!.grammar.varsCase.values.toMutableList().map { it.name }.toMutableList()
        timeNames = languages[id_lang]!!.grammar.varsTime.values.toMutableList().map { it.name }.toMutableList()
        personNames = languages[id_lang]!!.grammar.varsPerson.values.toMutableList().map { it.name }.toMutableList()
        moodNames = languages[id_lang]!!.grammar.varsMood.values.toMutableList().map { it.name }.toMutableList()
        typeNames = languages[id_lang]!!.grammar.varsType.values.toMutableList().map { it.name }.toMutableList()
        voiceNames = languages[id_lang]!!.grammar.varsVoice.values.toMutableList().map { it.name }.toMutableList()
        degreeOfComparisonNames = languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList().map { it.name }.toMutableList()

        genderRusIds = languages[id_lang]!!.grammar.varsGender.values.toMutableList().map { it.russianId }.toMutableList()
        numberRusIds = languages[id_lang]!!.grammar.varsNumber.values.toMutableList().map { it.russianId }.toMutableList()
        caseRusIds = languages[id_lang]!!.grammar.varsCase.values.toMutableList().map { it.russianId }.toMutableList()
        timeRusIds = languages[id_lang]!!.grammar.varsTime.values.toMutableList().map { it.russianId }.toMutableList()
        personRusIds = languages[id_lang]!!.grammar.varsPerson.values.toMutableList().map { it.russianId }.toMutableList()
        moodRusIds = languages[id_lang]!!.grammar.varsMood.values.toMutableList().map { it.russianId }.toMutableList()
        typeRusIds = languages[id_lang]!!.grammar.varsType.values.toMutableList().map { it.russianId }.toMutableList()
        voiceRusIds = languages[id_lang]!!.grammar.varsVoice.values.toMutableList().map { it.russianId }.toMutableList()
        degreeOfComparisonRusIds = languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList().map { it.russianId }.toMutableList()

        //list of genders
        val listGender : ListView = findViewById(R.id.listViewGender)
        adapterGender = AttributeAdapter(this, languages[id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        val buttonNewGender: Button = findViewById(R.id.buttonNewGender)
        buttonNewGender.setOnClickListener {
            val newGender = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.GENDER]?: 0, Attributes.GENDER)
            grammarDao.addOption(languages[id_lang]!!.grammar, newGender)
            genderNames.add("")
            genderRusIds.add(0)
            updateAdapter(0)
        }

        //list of numbers
        val listNumber : ListView = findViewById(R.id.listViewNumber)
        adapterNumber = AttributeAdapter(this, languages[id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()

        val buttonNewNumber: Button = findViewById(R.id.buttonNewNumber)
        buttonNewNumber.setOnClickListener {
            val newNumber = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.NUMBER]?: 0, Attributes.NUMBER)
            grammarDao.addOption(languages[id_lang]!!.grammar, newNumber)
            numberNames.add("")
            numberRusIds.add(0)
            updateAdapter(1)
        }

        //list of cases
        val listCase : ListView = findViewById(R.id.listViewCase)
        adapterCase = AttributeAdapter(this, languages[id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
        listCase.adapter = adapterCase
        adapterCase.notifyDataSetChanged()

        val buttonNewCase: Button = findViewById(R.id.buttonNewCase)
        buttonNewCase.setOnClickListener {
            val newCase = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.CASE]?: 0, Attributes.CASE)
            grammarDao.addOption(languages[id_lang]!!.grammar, newCase)
            caseNames.add("")
            caseRusIds.add(0)
            updateAdapter(2)
        }

        //list of times
        val listTime : ListView = findViewById(R.id.listViewTime)
        adapterTime = AttributeAdapter(this, languages[id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
        listTime.adapter = adapterTime
        adapterTime.notifyDataSetChanged()

        val buttonNewTime: Button = findViewById(R.id.buttonNewTime)
        buttonNewTime.setOnClickListener {
            val newTime = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.TIME]?: 0, Attributes.TIME)
            grammarDao.addOption(languages[id_lang]!!.grammar, newTime)
            timeNames.add("")
            timeRusIds.add(0)
            updateAdapter(3)
        }

        //list of persons
        val listPerson : ListView = findViewById(R.id.listViewPerson)
        adapterPerson = AttributeAdapter(this, languages[id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
        listPerson.adapter = adapterPerson
        adapterPerson.notifyDataSetChanged()

        val buttonNewPerson: Button = findViewById(R.id.buttonNewPerson)
        buttonNewPerson.setOnClickListener {
            val newPerson = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.PERSON]?: 0, Attributes.PERSON)
            grammarDao.addOption(languages[id_lang]!!.grammar, newPerson)
            personNames.add("")
            personRusIds.add(0)
            updateAdapter(4)
        }

        //list of moods
        val listMood : ListView = findViewById(R.id.listViewMood)
        adapterMood = AttributeAdapter(this, languages[id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
        listMood.adapter = adapterMood
        adapterMood.notifyDataSetChanged()

        val buttonNewMood: Button = findViewById(R.id.buttonNewMood)
        buttonNewMood.setOnClickListener {
            val newMood = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.MOOD]?: 0, Attributes.MOOD)
            grammarDao.addOption(languages[id_lang]!!.grammar, newMood)
            moodNames.add("")
            moodRusIds.add(0)
            updateAdapter(5)
        }

        //list of types
        val listType : ListView = findViewById(R.id.listViewType)
        adapterType = AttributeAdapter(this, languages[id_lang]!!.grammar.varsType.values.toMutableList(), 6)
        listType.adapter = adapterType
        adapterType.notifyDataSetChanged()

        val buttonNewType: Button = findViewById(R.id.buttonNewType)
        buttonNewType.setOnClickListener {
            val newType = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.TYPE]?: 0, Attributes.TYPE)
            grammarDao.addOption(languages[id_lang]!!.grammar, newType)
            typeNames.add("")
            typeRusIds.add(0)
            updateAdapter(6)
        }

        //list of voices
        val listVoice : ListView = findViewById(R.id.listViewVoice)
        adapterVoice = AttributeAdapter(this, languages[id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
        listVoice.adapter = adapterVoice
        adapterVoice.notifyDataSetChanged()

        val buttonNewVoice: Button = findViewById(R.id.buttonNewVoice)
        buttonNewVoice.setOnClickListener {
            val newVoice = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.VOICE]?: 0, Attributes.VOICE)
            grammarDao.addOption(languages[id_lang]!!.grammar, newVoice)
            voiceNames.add("")
            voiceRusIds.add(0)
            updateAdapter(7)
        }

        //list of voices
        val listDegreeOfComparison : ListView = findViewById(R.id.listViewDegreeOfComparison)
        adapterDegreeOfComparison = AttributeAdapter(this, languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
        listDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()

        val buttonNewDegreeOfComparison: Button = findViewById(R.id.buttonNewDegreeOfComparison)
        buttonNewDegreeOfComparison.setOnClickListener {
            val newDegreeOfComparison = CharacteristicEntity(languages[id_lang]!!.grammar.nextIds[Attributes.DEGREE_OF_COMPARISON]?: 0, Attributes.DEGREE_OF_COMPARISON)
            grammarDao.addOption(languages[id_lang]!!.grammar, newDegreeOfComparison)
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
        val listGrammarRules : ListView = findViewById(R.id.listViewGrammarRules)
        val adapterGrammarRules: ArrayAdapter<GrammarRuleEntity> = GrammarRuleAdapter(this, languages[id_lang]!!.grammar.grammarRules.toMutableList())
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
        when(idCharacteristic){
            0->{
                val listCharacteristic : ListView = findViewById(R.id.listViewGender)
                adapterGender = AttributeAdapter(this, languages[id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
                listCharacteristic.adapter = adapterGender
                adapterGender.notifyDataSetChanged()
            }
            1->{
                val listCharacteristic : ListView = findViewById(R.id.listViewNumber)
                adapterNumber = AttributeAdapter(this, languages[id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
                listCharacteristic.adapter = adapterNumber
                adapterNumber.notifyDataSetChanged()
            }
            2->{
                val listCharacteristic : ListView = findViewById(R.id.listViewCase)
                adapterCase = AttributeAdapter(this, languages[id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
                listCharacteristic.adapter = adapterCase
                adapterCase.notifyDataSetChanged()
            }
            3->{
                val listCharacteristic : ListView = findViewById(R.id.listViewTime)
                adapterTime = AttributeAdapter(this, languages[id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
                listCharacteristic.adapter = adapterTime
                adapterTime.notifyDataSetChanged()
            }
            4->{
                val listCharacteristic : ListView = findViewById(R.id.listViewPerson)
                adapterPerson = AttributeAdapter(this, languages[id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
                listCharacteristic.adapter = adapterPerson
                adapterPerson.notifyDataSetChanged()
            }
            5->{
                val listCharacteristic : ListView = findViewById(R.id.listViewMood)
                adapterMood = AttributeAdapter(this, languages[id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
                listCharacteristic.adapter = adapterMood
                adapterMood.notifyDataSetChanged()
            }
            6->{
                val listCharacteristic : ListView = findViewById(R.id.listViewType)
                adapterType = AttributeAdapter(this, languages[id_lang]!!.grammar.varsType.values.toMutableList(), 6)
                listCharacteristic.adapter = adapterType
                adapterType.notifyDataSetChanged()
            }
            7->{
                val listCharacteristic : ListView = findViewById(R.id.listViewVoice)
                adapterVoice = AttributeAdapter(this, languages[id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
                listCharacteristic.adapter = adapterVoice
                adapterVoice.notifyDataSetChanged()
            }
            else->{
                val listCharacteristic : ListView = findViewById(R.id.listViewDegreeOfComparison)
                adapterDegreeOfComparison = AttributeAdapter(this, languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
                listCharacteristic.adapter = adapterDegreeOfComparison
                adapterDegreeOfComparison.notifyDataSetChanged()
            }
        }
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        setResult(RESULT_OK, data)
        super.finish()
    }
}
private class AttributeAdapter(context: Context, listOfAttributes: MutableList<CharacteristicEntity>,
                               idListAttribute: Int) :
    ArrayAdapter<CharacteristicEntity>(context,
        R.layout.characteristic_line_activity, listOfAttributes) {

    var idAttribute = idListAttribute

    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: CharacteristicEntity? = getItem(positionAttribute)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
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
                if(GrammarActivity.isFirst)return
                val updatedText = s.toString()
                val editTextTag = editTextName.tag
                if (positionAttribute == editTextTag as Int) {
                    when (idAttribute) {
                        0 -> {

                            GrammarActivity.genderNames[positionAttribute] = updatedText
                        }

                        1 -> {
                            if (GrammarActivity.numberNames[positionAttribute] != updatedText) {
                                GrammarActivity.numberNames[positionAttribute] = updatedText
                            }
                        }

                        2 -> {
                            if (GrammarActivity.caseNames[positionAttribute] != updatedText) {
                                GrammarActivity.caseNames[positionAttribute] = updatedText
                            }
                        }

                        3 -> {
                            if (GrammarActivity.timeNames[positionAttribute] != updatedText) {
                                GrammarActivity.timeNames[positionAttribute] = updatedText
                            }
                        }

                        4 -> {
                            if (GrammarActivity.personNames[positionAttribute] != updatedText) {
                                GrammarActivity.personNames[positionAttribute] = updatedText
                            }
                        }

                        5 -> {
                            if (GrammarActivity.moodNames[positionAttribute] != updatedText) {
                                GrammarActivity.moodNames[positionAttribute] = updatedText
                            }
                        }

                        6 -> {
                            if (GrammarActivity.typeNames[positionAttribute] != updatedText) {
                                GrammarActivity.typeNames[positionAttribute] = updatedText
                            }
                        }

                        7 -> {
                            if (GrammarActivity.voiceNames[positionAttribute] != updatedText) {
                                GrammarActivity.voiceNames[positionAttribute] = updatedText
                            }
                        }

                        else -> {
                            if (GrammarActivity.degreeOfComparisonNames[positionAttribute] != updatedText) {
                                GrammarActivity.degreeOfComparisonNames[positionAttribute] = updatedText
                            }
                        }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //spinner is working
        val spinnerAdapter: ArrayAdapter<String>
        when(idAttribute){
            0->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusGender)
            }
            1->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusNumber)
            }
            2->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusCase)
            }
            3->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusTime)
            }
            4->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusPerson)
            }
            5->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusMood)
            }
            6->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusType)
            }
            7->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusVoice)
            }
            else->{
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusDegreeOfComparison)
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
        if(positionAttribute==0){
            buttonDel.visibility=View.GONE
        }
        else{
            buttonDel.visibility=View.VISIBLE
        }
        val grammarDao = GrammarDaoImpl()
        buttonDel.setOnClickListener {
            when(idAttribute){
                0->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute])
                    GrammarActivity.genderNames.removeAt(positionAttribute)
                    GrammarActivity.genderRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                1->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute])
                    GrammarActivity.numberNames.removeAt(positionAttribute)
                    GrammarActivity.numberRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                2->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute])
                    GrammarActivity.caseNames.removeAt(positionAttribute)
                    GrammarActivity.caseRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                3->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute])
                    GrammarActivity.timeNames.removeAt(positionAttribute)
                    GrammarActivity.timeRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                4-> {
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute])
                    GrammarActivity.personNames.removeAt(positionAttribute)
                    GrammarActivity.personRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                5->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute])
                    GrammarActivity.moodNames.removeAt(positionAttribute)
                    GrammarActivity.moodRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                6-> {
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute])
                    GrammarActivity.typeNames.removeAt(positionAttribute)
                    GrammarActivity.typeRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                7->{
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute])
                    GrammarActivity.voiceNames.removeAt(positionAttribute)
                    GrammarActivity.voiceRusIds.removeAt(positionAttribute)
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                else-> {
                    grammarDao.deleteOption(
                        languages[GrammarActivity.id_lang]!!.grammar,
                        languages[GrammarActivity.id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute])
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
            newView = LayoutInflater.from(context).inflate(R.layout.grammar_rule_line_activity, null)
        }

        val grammarRuleDao: GrammarRuleDao = GrammarRuleDaoImpl()
        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = grammarRuleDao.getOrigInfo(grammarRule!!)
        changeableAttributes.text = grammarRuleDao.getResultInfo(grammarRule)

        return newView
    }
}