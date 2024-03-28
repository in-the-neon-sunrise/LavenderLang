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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.lavenderlang.GrammarActivity.Companion.id_lang
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
        val adapterGender: ArrayAdapter<Attribute> = AttributeAdapter(this, Languages.attributesGender)
        listGender.adapter = adapterGender
        adapterGender.notifyDataSetChanged()
    }
}
private class AttributeAdapter(context: Context, listOfAttributes: MutableList<Attribute>) :
    ArrayAdapter<Attribute>(context, R.layout.characteristic_line_activity, listOfAttributes) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newView = convertView
        val attribute: Attribute? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.characteristic_line_activity, null)
        }

        //edittext is visible
        val editTextName: EditText = newView!!.findViewById(R.id.editTextNameAttribute)
        (editTextName as TextView).text = attribute!!.name

        //spinner is working
        val spinnerRus: Spinner = newView.findViewById(R.id.spinnerRusAttribute)
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1, rusGender)
        spinnerRus.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        if(attribute.rusId<0) spinnerRus.setSelection(0)
        else spinnerRus.setSelection(attribute.rusId)

        spinnerRus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentSpinner: AdapterView<*>?, itemSpinner: View?, positionSpinner: Int, idSpinner: Long) {
                Languages.attributesGender[position].rusId=positionSpinner;
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Languages.attributesGender[position].rusId=-1;
            }
        }

        //checkbox is working
        val checkBoxInf: CheckBox = newView.findViewById(R.id.checkBoxInf)
        if(attribute.isInf) checkBoxInf.isChecked = true

        checkBoxInf.setOnCheckedChangeListener { buttonView, isChecked ->
            attribute.isInf=isChecked
            Languages.attributesGender[position].isInf = isChecked
            Attribute.changeOthers(position, Languages.attributesGender)
        }

        return newView
    }
}