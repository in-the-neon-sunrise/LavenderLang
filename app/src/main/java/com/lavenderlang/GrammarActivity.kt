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
import com.lavenderlang.backend.*


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
        val adapterGender: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesGender, 0)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()
        //list of numbers
        val listNumber : ListView = findViewById(R.id.listViewNumber)
        val adapterNumber: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesNumber, 1)
        listNumber.adapter = adapterNumber
        adapterNumber.notifyDataSetChanged()
        //list of cases
        val listCases : ListView = findViewById(R.id.listViewCase)
        val adapterCases: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesCase, 2)
        listCases.adapter = adapterCases
        adapterCases.notifyDataSetChanged()
        //list of times
        val listTimes : ListView = findViewById(R.id.listViewTime)
        val adapterTimes: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesTime, 3)
        listTimes.adapter = adapterTimes
        adapterTimes.notifyDataSetChanged()
        //list of persons
        val listPersons : ListView = findViewById(R.id.listViewPerson)
        val adapterPersons: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesPerson, 4)
        listPersons.adapter = adapterPersons
        adapterPersons.notifyDataSetChanged()
        //list of moods
        val listMoods : ListView = findViewById(R.id.listViewMood)
        val adapterMoods: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesMood, 5)
        listMoods.adapter = adapterMoods
        adapterMoods.notifyDataSetChanged()
        //list of types
        val listTypes : ListView = findViewById(R.id.listViewType)
        val adapterTypes: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesType, 6)
        listTypes.adapter = adapterTypes
        adapterTypes.notifyDataSetChanged()
        //list of voices
        val listVoices : ListView = findViewById(R.id.listViewVoice)
        val adapterVoices: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesVoice, 7)
        listVoices.adapter = adapterVoices
        adapterVoices.notifyDataSetChanged()
        //list of voices
        val listDegreeOfComparison : ListView = findViewById(R.id.listViewDegreeOfComparison)
        val adapterDegreeOfComparison: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesDegreeOfComparison, 8)
        listDegreeOfComparison.adapter = adapterDegreeOfComparison
        adapterDegreeOfComparison.notifyDataSetChanged()


        //button new grammar rule listener
        val buttonNewRule: Button = findViewById(R.id.buttonNewGrammarRule)
        buttonNewRule.setOnClickListener {
            val intent = Intent(this@GrammarActivity, GrammarRuleActivity::class.java)
            intent.putExtra("lang", -1)
            intent.putExtra("grammarRule", -1)
            startActivity(intent)
        }

        //list of rules
        //вот здесь нужно стринги поменять на нормальные правила
        val listGrammarRules : ListView = findViewById(R.id.listViewGrammarRules)
        val adapterGrammarRules: ArrayAdapter<String> = GrammarRuleAdapter(this, Languages.rules)
        listGrammarRules.adapter = adapterGrammarRules
        adapterGrammarRules.notifyDataSetChanged()
    }
}
private class AttributeAdapter(context: Context, listOfAttributes: MutableList<Attribute>, idListAttribute: Int) :
    ArrayAdapter<Attribute>(context, R.layout.characteristic_line_activity, listOfAttributes) {
    var idAttribute = idListAttribute

    override fun getView(positionAttribute: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val attribute: Attribute? = getItem(positionAttribute)
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
        if(attribute.rusId<0) spinnerRus.setSelection(0)
        else spinnerRus.setSelection(attribute.rusId)

        spinnerRus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                when(idAttribute){
                    0->{
                        Languages.attributesGender[positionAttribute].rusId=positionSpinner;
                    }
                    1->{
                        Languages.attributesNumber[positionAttribute].rusId=positionSpinner;
                    }
                    2->{
                        Languages.attributesCase[positionAttribute].rusId=positionSpinner;
                    }
                    3->{
                        Languages.attributesTime[positionAttribute].rusId=positionSpinner;
                    }
                    4->{
                        Languages.attributesPerson[positionAttribute].rusId=positionSpinner;
                    }
                    5->{
                        Languages.attributesMood[positionAttribute].rusId=positionSpinner;
                    }
                    6->{
                        Languages.attributesType[positionAttribute].rusId=positionSpinner;
                    }
                    7->{
                        Languages.attributesVoice[positionAttribute].rusId=positionSpinner;
                    }
                    else->{
                        Languages.attributesDegreeOfComparison[positionAttribute].rusId=positionSpinner;
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                when(idAttribute){
                    0->{
                        Languages.attributesGender[positionAttribute].rusId=-1;
                    }
                    1->{
                        Languages.attributesNumber[positionAttribute].rusId=-1;
                    }
                    2->{
                        Languages.attributesCase[positionAttribute].rusId=-1;
                    }
                    3->{
                        Languages.attributesTime[positionAttribute].rusId=-1;
                    }
                    4->{
                        Languages.attributesPerson[positionAttribute].rusId=-1;
                    }
                    5->{
                        Languages.attributesMood[positionAttribute].rusId=-1;
                    }
                    6->{
                        Languages.attributesType[positionAttribute].rusId=-1;
                    }
                    7->{
                        Languages.attributesVoice[positionAttribute].rusId=-1;
                    }
                    else->{
                        Languages.attributesDegreeOfComparison[positionAttribute].rusId=-1;
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

private class GrammarRuleAdapter(context: Context, listOfRules: MutableList<String>) :
    ArrayAdapter<String>(context, R.layout.grammar_rule_line_activity, listOfRules) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val grammarRule: String? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.grammar_rule_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView!!.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = grammarRule
        changeableAttributes.text = grammarRule



        return newView
    }
}