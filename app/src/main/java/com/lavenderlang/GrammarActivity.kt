package com.lavenderlang

import android.app.Activity
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
import com.lavenderlang.GrammarActivity.Companion.clever_id_lang
import com.lavenderlang.GrammarActivity.Companion.grammarDao
import com.lavenderlang.GrammarActivity.Companion.id_lang
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.Characteristic
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.service.*


class GrammarActivity: AppCompatActivity() {
    companion object{
        var id_lang: Int = 0
        var clever_id_lang = languages.keys.toMutableList()[id_lang]
        val grammarDao = GrammarDaoImpl()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(this@GrammarActivity, LanguageActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@GrammarActivity, InformationActivity::class.java)
            intent.putExtra("lang", id_lang)
            startActivity(intent)
        }


    }
    override fun onResume() {
        super.onResume()
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

        //list of genders
        val listGender : ListView = findViewById(R.id.listViewGender)
        val adapterGender: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()

        val buttonNewGender: Button = findViewById(R.id.buttonNewGender)
        buttonNewGender.setOnClickListener {
            val newGender = Characteristic(languages.keys.toMutableList()[clever_id_lang], languages[clever_id_lang]!!.grammar.nextIds[Attributes.GENDER]?: 0, Attributes.GENDER)
            grammarDao.addOption(languages[clever_id_lang]!!.grammar, newGender)
            Toast.makeText(this, languages[clever_id_lang]!!.grammar.nextIds.toString(), Toast.LENGTH_LONG).show()
            adapterGender.notifyDataSetChanged()
        }

        //list of numbers
        val listNumber : ListView = findViewById(R.id.listViewNumber)
        val adapterNumber: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()
        //list of cases
        val listCases : ListView = findViewById(R.id.listViewCase)
        val adapterCases: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
        listCases.adapter = adapterCases
        adapterCases.notifyDataSetChanged()
        //list of times
        val listTimes : ListView = findViewById(R.id.listViewTime)
        val adapterTimes: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
        listTimes.adapter = adapterTimes
        adapterTimes.notifyDataSetChanged()
        //list of persons
        val listPersons : ListView = findViewById(R.id.listViewPerson)
        val adapterPersons: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
        listPersons.adapter = adapterPersons
        adapterPersons.notifyDataSetChanged()
        //list of moods
        val listMoods : ListView = findViewById(R.id.listViewMood)
        val adapterMoods: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
        listMoods.adapter = adapterMoods
        adapterMoods.notifyDataSetChanged()
        //list of types
        val listTypes : ListView = findViewById(R.id.listViewType)
        val adapterTypes: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsType.values.toMutableList(), 6)
        listTypes.adapter = adapterTypes
        adapterTypes.notifyDataSetChanged()
        //list of voices
        val listVoices : ListView = findViewById(R.id.listViewVoice)
        val adapterVoices: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
        listVoices.adapter = adapterVoices
        adapterVoices.notifyDataSetChanged()
        //list of voices
        val listDegreeOfComparison : ListView = findViewById(R.id.listViewDegreeOfComparison)
        val adapterDegreeOfComparison: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
        listDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()


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
    fun updateAdapter(idCharacteristic: Int) {
        when(idCharacteristic){
            0->{
                val listCharacteristic : ListView = findViewById(R.id.listViewGender)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            1->{
                val listCharacteristic : ListView = findViewById(R.id.listViewNumber)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            2->{
                val listCharacteristic : ListView = findViewById(R.id.listViewCase)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            3->{
                val listCharacteristic : ListView = findViewById(R.id.listViewTime)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            4->{
                val listCharacteristic : ListView = findViewById(R.id.listViewPerson)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            5->{
                val listCharacteristic : ListView = findViewById(R.id.listViewMood)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            6->{
                val listCharacteristic : ListView = findViewById(R.id.listViewType)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsType.values.toMutableList(), 6)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            7->{
                val listCharacteristic : ListView = findViewById(R.id.listViewVoice)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
            else->{
                val listCharacteristic : ListView = findViewById(R.id.listViewDegreeOfComparison)
                val adapterCharacteristic: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
                listCharacteristic.adapter = adapterCharacteristic
                adapterCharacteristic.notifyDataSetChanged()
            }
        }
    }
}
private class AttributeAdapter(context: Context, listOfAttributes: MutableList<Characteristic>, idListAttribute: Int) :
    ArrayAdapter<Characteristic>(context, R.layout.characteristic_line_activity, listOfAttributes) {
    var idAttribute = idListAttribute

    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: Characteristic? = getItem(positionAttribute)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
        }

        //edittext is visible
        var editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
        (editTextName as TextView).text = attribute!!.name

        /*editTextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                when(idAttribute){
                    0->{
                        /*grammarDao.updateOption(languages[GrammarActivity.id_lang]!!.grammar,
                            languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].characteristicId,
                            Characteristic(id_lang, languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].characteristicId, Attributes.GENDER, editTextName.text.toString(), languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId*/
                        if(editTextName.text==s) languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList()[attribute.characteristicId].name=s.toString()
                    }
                    1->{
                        languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    2->{
                        languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    3->{
                        languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    4->{
                        languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    5->{
                        languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    6->{
                        languages[clever_id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    7->{
                        languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                    else->{
                        languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].name=editTextName.text.toString()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })*/

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

        spinnerRus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                when(idAttribute){
                    0->{
                        languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    1->{
                        languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    2->{
                        languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    3->{
                        languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    4->{
                        languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    5->{
                        languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    6->{
                        languages[clever_id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    7->{
                        languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    else->{
                        languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                when(idAttribute){
                    0->{
                        languages[clever_id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    1->{
                        languages[clever_id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    2->{
                        languages[clever_id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    3->{
                        languages[clever_id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    4->{
                        languages[clever_id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    5->{
                        languages[clever_id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    6->{
                        languages[clever_id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    7->{
                        languages[clever_id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    else->{
                        languages[clever_id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId=0;
                    }
                }
            }
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