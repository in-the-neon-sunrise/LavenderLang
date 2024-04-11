package com.lavenderlang

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.lavenderlang.GrammarActivity.Companion.SAVE_ACTION
import com.lavenderlang.GrammarActivity.Companion.clever_id_lang
import com.lavenderlang.GrammarActivity.Companion.grammarDao
import com.lavenderlang.GrammarActivity.Companion.id_lang
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.language.GrammarEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.service.*


class GrammarActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var clever_id_lang = languages.keys.toMutableList()[id_lang]
        val grammarDao = GrammarDaoImpl()
        var grammar: GrammarEntity = languages[id_lang]!!.grammar
        val SAVE_ACTION = "com.lavenderlang.SAVE_ACTION"
    }


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
            val intent = Intent(this@GrammarActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }

        /*val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            val intent = Intent(SAVE_ACTION)
            sendBroadcast(intent)
        }*/
    }

    override fun onStart() {
        super.onStart()
        //how it was started?
        when (val lang = intent.getIntExtra("lang", -1)) {
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

        //list of genders
        val listGender : ListView = findViewById(R.id.listViewGender)
        val adapterGender: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        val buttonNewGender: Button = findViewById(R.id.buttonNewGender)
        buttonNewGender.setOnClickListener {
            val newGender = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.GENDER]?: 0, Attributes.GENDER)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newGender)
            updateAdapter(0)
        }

        //list of numbers
        val listNumber : ListView = findViewById(R.id.listViewNumber)
        val adapterNumber: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()

        val buttonNewNumber: Button = findViewById(R.id.buttonNewNumber)
        buttonNewNumber.setOnClickListener {
            val newNumber = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.NUMBER]?: 0, Attributes.NUMBER)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newNumber)
            updateAdapter(1)
        }

        //list of cases
        val listCases : ListView = findViewById(R.id.listViewCase)
        val adapterCases: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
        listCases.adapter = adapterCases
        adapterCases.notifyDataSetChanged()

        val buttonNewCase: Button = findViewById(R.id.buttonNewCase)
        buttonNewCase.setOnClickListener {
            val newCase = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.CASE]?: 0, Attributes.CASE)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newCase)
            updateAdapter(2)
        }

        //list of times
        val listTimes : ListView = findViewById(R.id.listViewTime)
        val adapterTimes: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
        listTimes.adapter = adapterTimes
        adapterTimes.notifyDataSetChanged()

        val buttonNewTime: Button = findViewById(R.id.buttonNewTime)
        buttonNewTime.setOnClickListener {
            val newTime = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.TIME]?: 0, Attributes.TIME)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newTime)
            updateAdapter(3)
        }

        //list of persons
        val listPersons : ListView = findViewById(R.id.listViewPerson)
        val adapterPersons: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
        listPersons.adapter = adapterPersons
        adapterPersons.notifyDataSetChanged()

        val buttonNewPerson: Button = findViewById(R.id.buttonNewPerson)
        buttonNewPerson.setOnClickListener {
            val newPerson = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.PERSON]?: 0, Attributes.PERSON)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newPerson)
            updateAdapter(4)
        }

        //list of moods
        val listMoods : ListView = findViewById(R.id.listViewMood)
        val adapterMoods: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
        listMoods.adapter = adapterMoods
        adapterMoods.notifyDataSetChanged()

        val buttonNewMood: Button = findViewById(R.id.buttonNewMood)
        buttonNewMood.setOnClickListener {
            val newMood = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.MOOD]?: 0, Attributes.MOOD)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newMood)
            updateAdapter(5)
        }

        //list of types
        val listTypes : ListView = findViewById(R.id.listViewType)
        val adapterTypes: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsType.values.toMutableList(), 6)
        listTypes.adapter = adapterTypes
        adapterTypes.notifyDataSetChanged()

        val buttonNewType: Button = findViewById(R.id.buttonNewType)
        buttonNewType.setOnClickListener {
            val newType = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.TYPE]?: 0, Attributes.TYPE)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newType)
            updateAdapter(6)
        }

        //list of voices
        val listVoices : ListView = findViewById(R.id.listViewVoice)
        val adapterVoices: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
        listVoices.adapter = adapterVoices
        adapterVoices.notifyDataSetChanged()

        val buttonNewVoice: Button = findViewById(R.id.buttonNewVoice)
        buttonNewVoice.setOnClickListener {
            val newVoice = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.VOICE]?: 0, Attributes.VOICE)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newVoice)
            updateAdapter(7)
        }

        //list of voices
        val listDegreeOfComparison : ListView = findViewById(R.id.listViewDegreeOfComparison)
        val adapterDegreeOfComparison: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
        listDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()

        val buttonNewDegreeOfComparison: Button = findViewById(R.id.buttonNewDegreeOfComparison)
        buttonNewDegreeOfComparison.setOnClickListener {
            val newDegreeOfComparison = CharacteristicEntity(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.DEGREE_OF_COMPARISON]?: 0, Attributes.DEGREE_OF_COMPARISON)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newDegreeOfComparison)
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
        val adapterGrammarRules: ArrayAdapter<GrammarRuleEntity> = GrammarRuleAdapter(this, languages[clever_id_lang]!!.grammar.grammarRules.toMutableList())
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

    override fun onDestroy() {
        super.onDestroy()
    }
    fun updateAdapter(idCharacteristic: Int) {
        when(idCharacteristic){
            0->{
                val listCharacteristic : ListView = findViewById(R.id.listViewGender)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            1->{
                val listCharacteristic : ListView = findViewById(R.id.listViewNumber)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            2->{
                val listCharacteristic : ListView = findViewById(R.id.listViewCase)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            3->{
                val listCharacteristic : ListView = findViewById(R.id.listViewTime)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            4->{
                val listCharacteristic : ListView = findViewById(R.id.listViewPerson)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            5->{
                val listCharacteristic : ListView = findViewById(R.id.listViewMood)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            6->{
                val listCharacteristic : ListView = findViewById(R.id.listViewType)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsType.values.toMutableList(), 6)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            7->{
                val listCharacteristic : ListView = findViewById(R.id.listViewVoice)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
            else->{
                val listCharacteristic : ListView = findViewById(R.id.listViewDegreeOfComparison)
                val adapterCharacteristicEntity: ArrayAdapter<CharacteristicEntity> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
                listCharacteristic.adapter = adapterCharacteristicEntity
                adapterCharacteristicEntity.notifyDataSetChanged()
            }
        }
    }
}
private class AttributeAdapter(context: Context, listOfAttributes: MutableList<CharacteristicEntity>,
                               idListAttribute: Int) :
    ArrayAdapter<CharacteristicEntity>(context, R.layout.characteristic_line_activity, listOfAttributes) {
    var idAttribute = idListAttribute


    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: CharacteristicEntity? = getItem(positionAttribute)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
        }

        //edittext is visible
        var editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
        (editTextName as TextView).text = attribute!!.name

        //spinner is working
        val spinnerRus: Spinner = newView.findViewById(R.id.spinnerRusAttribute)
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

        //button save is working
        val saveReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                if (intent.action == SAVE_ACTION) {
                    when(idAttribute){
                        0->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.GENDER, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;

                        }
                        1->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.NUMBER, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        2->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.CASE, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        3->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.TIME, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        4->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.PERSON, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        5->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.MOOD, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        6-> {
                            grammarDao.updateOption(
                                languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(
                                    id_lang,
                                    languages[id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.TYPE, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        7->{grammarDao.updateOption(languages[id_lang]!!.grammar,
                            languages[id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].characteristicId,
                            CharacteristicEntity(id_lang,
                                languages[id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].characteristicId,
                                Attributes.VOICE, editTextName.text.toString(),
                                languages[id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                        else->{
                            grammarDao.updateOption(languages[id_lang]!!.grammar,
                                languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].characteristicId,
                                CharacteristicEntity(id_lang,
                                    languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].characteristicId,
                                    Attributes.DEGREE_OF_COMPARISON, editTextName.text.toString(),
                                    languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId))

                            languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId=spinnerRus.selectedItemPosition;
                        }
                    }
                }
            }
        }
        val filter = IntentFilter(SAVE_ACTION)
        context.registerReceiver(saveReceiver, filter, Context.RECEIVER_NOT_EXPORTED)


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
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                1->{
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                2->{
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                3->{
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                4-> {
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                5->{
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                6-> {
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                7->{
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute])
                    (context as GrammarActivity).updateAdapter(idAttribute)
                }
                else-> {
                    grammarDao.deleteOption(languages[clever_id_lang]!!.grammar,
                        languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute])
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

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView!!.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = grammarRule.toString()
        changeableAttributes.text = grammarRule.toString()

        return newView
    }
}