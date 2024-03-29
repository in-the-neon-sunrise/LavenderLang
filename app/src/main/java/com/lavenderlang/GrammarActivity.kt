package com.lavenderlang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import com.lavenderlang.backend.entity.help.Characteristic
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.service.*
import java.util.SortedSet


class GrammarActivity : Activity() {
    companion object{
        var id_lang: Int = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grammar_activity)

        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            this.finish()
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
        val adapterGender: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsGender.values.toMutableList(), 0)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()
        //list of numbers
        val listNumber : ListView = findViewById(R.id.listViewNumber)
        val adapterNumber: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsNumber.values.toMutableList(), 1)
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()
        //list of cases
        val listCases : ListView = findViewById(R.id.listViewCase)
        val adapterCases: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsCase.values.toMutableList(), 2)
        listCases.adapter = adapterCases
        adapterCases.notifyDataSetChanged()
        //list of times
        val listTimes : ListView = findViewById(R.id.listViewTime)
        val adapterTimes: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsTime.values.toMutableList(), 3)
        listTimes.adapter = adapterTimes
        adapterTimes.notifyDataSetChanged()
        //list of persons
        val listPersons : ListView = findViewById(R.id.listViewPerson)
        val adapterPersons: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsPerson.values.toMutableList(), 4)
        listPersons.adapter = adapterPersons
        adapterPersons.notifyDataSetChanged()
        //list of moods
        val listMoods : ListView = findViewById(R.id.listViewMood)
        val adapterMoods: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsMood.values.toMutableList(), 5)
        listMoods.adapter = adapterMoods
        adapterMoods.notifyDataSetChanged()
        //list of types
        val listTypes : ListView = findViewById(R.id.listViewType)
        val adapterTypes: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsType.values.toMutableList(), 6)
        listTypes.adapter = adapterTypes
        adapterTypes.notifyDataSetChanged()
        //list of voices
        val listVoices : ListView = findViewById(R.id.listViewVoice)
        val adapterVoices: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsVoice.values.toMutableList(), 7)
        listVoices.adapter = adapterVoices
        adapterVoices.notifyDataSetChanged()
        //list of voices
        val listDegreeOfComparison : ListView = findViewById(R.id.listViewDegreeOfComparison)
        val adapterDegreeOfComparison: ArrayAdapter<Characteristic> = AttributeAdapter(this, languages[id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList(), 8)
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
        //вот здесь нужно стринги поменять на нормальные правила
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
        val editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
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

        spinnerRus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                when(idAttribute){
                    0->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    1->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    2->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    3->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    4->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    5->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    6->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    7->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }
                    else->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId=positionSpinner;
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                when(idAttribute){
                    0->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsGender.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    1->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsNumber.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    2->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsCase.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    3->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsTime.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    4->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsPerson.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    5->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsMood.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    6->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsType.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    7->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsVoice.values.toMutableList()[positionAttribute].russianId=0;
                    }
                    else->{
                        languages[GrammarActivity.id_lang]!!.grammar.varsDegreeOfComparison.values.toMutableList()[positionAttribute].russianId=0;
                    }
                }
            }
        }

        //checkbox is working
        val radioButtonInf: RadioButton = newView.findViewById(R.id.radioButtonInf)
        //radioButtonInf.isChecked = false
        if(positionAttribute == 0) radioButtonInf.setChecked(false)
        if(Languages.idGenderInf == positionAttribute) radioButtonInf.setChecked(true)

        radioButtonInf.setOnCheckedChangeListener { buttonView, isChecked ->
            //Languages.attributesGender[positionAttrubute].isInf = isChecked
            if (isChecked) Languages.idGenderInf = positionAttribute;
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