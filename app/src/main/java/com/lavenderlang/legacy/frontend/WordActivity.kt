package com.lavenderlang.legacy.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.appcompat.app.AppCompatDelegate
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.DictionaryDao
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.LanguageDao
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.dao.word.WordDao
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.model.word.NounEntity
import com.lavenderlang.domain.exception.ForbiddenSymbolsException
import com.lavenderlang.ui.MyApp

class WordActivity : AppCompatActivity() {
    companion object{
        var id_lang: Int = -1
        var id_word: Int = -1

        var immutableAttrs: MutableMap<Attributes, Int> = mutableMapOf()
        var idPartOfSpeech: Int = 0
        var partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN
        var flagIsFirst:Boolean = true

        val wordDao: WordDao = WordDaoImpl()
        val languageDao: LanguageDao = LanguageDaoImpl()
        val dictionaryDao: DictionaryDao = DictionaryDaoImpl()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("huh", "oncreate")
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_activity)
        if(getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //top navigation menu
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            finish()
        }
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(this@WordActivity, InstructionActivity::class.java)
            intent.putExtra("lang", id_lang)
            intent.putExtra("block", 7)
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
        when (val lang = intent.getIntExtra("lang", -1)) {
            -1 -> {
                val intent = Intent(this@WordActivity, LanguageActivity::class.java)
                startActivity(intent)
            }
            else -> {
                id_lang = lang
            }
        }

        var word = intent.getIntExtra("word", -1)
        when (word) {
            -1 -> {
                id_word = MyApp.language!!.dictionary.dict.size
                dictionaryDao.addWord(MyApp.language!!.dictionary, NounEntity(id_lang, "", "-"))
                Log.d("create word", MyApp.language!!.dictionary.dict[id_word].word)
            }
            else -> {
                Log.d("old word", MyApp.language!!.dictionary.dict[word].word)
                id_word = word
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        flagIsFirst =true
        Log.d("huh", "onresume")

        var editConlangWord: EditText = findViewById(R.id.editConlangWord)
        var editRussianWord: EditText = findViewById(R.id.editRussianWord)

        Log.d("full", MyApp.language!!.dictionary.fullDict.toString())

        editConlangWord.setText(MyApp.language!!.dictionary.dict[id_word].word)
        editRussianWord.setText(MyApp.language!!.dictionary.dict[id_word].translation)

        partOfSpeech = MyApp.language!!.dictionary.dict[id_word].partOfSpeech
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
        immutableAttrs = MyApp.language!!.dictionary.dict[id_word].immutableAttrs

        setSpinners()
        updateSpinners()
        setPartOfSpeechListener()

        updateWordForms()
        val buttonCreateWords: Button = findViewById(R.id.buttonCreateWords)
        buttonCreateWords.setOnClickListener {
            updateNewWords()
        }

        val buttonUpdate: Button = findViewById(R.id.buttonSave)
        buttonUpdate.setOnClickListener {
            partOfSpeech = when(idPartOfSpeech){
                0-> PartOfSpeech.NOUN
                1-> PartOfSpeech.VERB
                2-> PartOfSpeech.ADJECTIVE
                3-> PartOfSpeech.ADVERB
                4-> PartOfSpeech.PARTICIPLE
                5-> PartOfSpeech.VERB_PARTICIPLE
                6-> PartOfSpeech.PRONOUN
                7-> PartOfSpeech.NUMERAL
                8-> PartOfSpeech.FUNC_PART
                else-> PartOfSpeech.NOUN
            }
            updateAttrs()
            try {
                wordDao.updateWord(
                    MyApp.language!!.dictionary.dict[id_word], editConlangWord.text.toString(),
                    editRussianWord.text.toString(), immutableAttrs, partOfSpeech
                )
            } catch (e: ForbiddenSymbolsException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        val buttonDelete: Button = findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            dictionaryDao.deleteWord(MyApp.language!!.dictionary, MyApp.language!!.dictionary.dict[id_word])
            finish()
        }
    }
    fun setSpinners() {
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner=findViewById(R.id.spinnerGender)
        val spinnerType: Spinner=findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner=findViewById(R.id.spinnerVoice)

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf(
            "Существительное",
            "Глагол",
            "Прилагательное",
            "Наречие",
            "Причастие",
            "Деепричастие",
            "Местоимение",
            "Числительное",
            "Служебное слово"))
        spinnerPartOfSpeech.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        val genderNames = MyApp.language!!.grammar.varsGender.values.map { it.name }
        var genderAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderNames)
        spinnerGender.adapter = genderAdapter
        genderAdapter.notifyDataSetChanged()

        val typeNames = MyApp.language!!.grammar.varsType.values.map { it.name }
        val typeAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, typeNames)
        spinnerType.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        val voiceNames = MyApp.language!!.grammar.varsVoice.values.map { it.name }
        val voiceAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, voiceNames)
        spinnerVoice.adapter = voiceAdapter
        voiceAdapter.notifyDataSetChanged()
    }
    fun updateSpinners(){
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner =findViewById(R.id.spinnerGender)
        val spinnerType: Spinner =findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner =findViewById(R.id.spinnerVoice)

        spinnerPartOfSpeech.setSelection(idPartOfSpeech)
        when(idPartOfSpeech) {
            0 -> {
                spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)
            }
            1->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
                spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)
            }
            2-> {}
            3->{}
            4->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE]?: 0)
                spinnerVoice.setSelection(immutableAttrs[Attributes.VOICE] ?: 0)
            }
            5->{
                spinnerType.setSelection(immutableAttrs[Attributes.TYPE] ?: 0)
            }
            6->{
                spinnerGender.setSelection(immutableAttrs[Attributes.GENDER] ?: 0)
            }
            7->{}
            else->{}
        }
    }
    fun setPartOfSpeechListener(){
        val spinnerPartOfSpeech: Spinner = findViewById(R.id.spinnerPartOfSpeech)

        val spinnerGender: Spinner =findViewById(R.id.spinnerGender)
        val spinnerType: Spinner =findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner =findViewById(R.id.spinnerVoice)

        spinnerPartOfSpeech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentSpinner: AdapterView<*>?,
                itemSpinner: View?,
                positionSpinner: Int,
                idSpinner: Long
            ) {
                when(positionSpinner){
                    0->{
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    1->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    2->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst){
                            flagIsFirst =false
                            return
                        }
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                    }
                    3->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    4->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.VISIBLE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    5->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.VISIBLE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    6->{
                        spinnerGender.visibility= View.VISIBLE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    7->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

                        if(positionSpinner == idPartOfSpeech || flagIsFirst) return
                        idPartOfSpeech = positionSpinner
                        immutableAttrs = mutableMapOf()
                        updateSpinners()
                        flagIsFirst =false
                    }
                    else->{
                        spinnerGender.visibility= View.GONE
                        spinnerType.visibility= View.GONE
                        spinnerVoice.visibility= View.GONE

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
    fun updateAttrs(){
        val spinnerGender: Spinner =findViewById(R.id.spinnerGender)
        val spinnerType: Spinner =findViewById(R.id.spinnerType)
        val spinnerVoice: Spinner =findViewById(R.id.spinnerVoice)

        when(idPartOfSpeech) {
            0->{
                immutableAttrs[Attributes.GENDER]=spinnerGender.selectedItemPosition
            }
            1->{
                immutableAttrs[Attributes.TYPE]=spinnerType.selectedItemPosition
                immutableAttrs[Attributes.VOICE]=spinnerVoice.selectedItemPosition
            }
            2->{}
            3->{}
            4->{
                immutableAttrs[Attributes.TYPE]=spinnerType.selectedItemPosition
                immutableAttrs[Attributes.VOICE]=spinnerVoice.selectedItemPosition
            }
            5->{
                immutableAttrs[Attributes.TYPE]=spinnerType.selectedItemPosition
            }
            6->{
                immutableAttrs[Attributes.GENDER]=spinnerGender.selectedItemPosition
            }
            7->{}
            else->{}
        }
    }
    fun updateWordForms(){
        val wordForms = dictionaryDao.getWordForms(MyApp.language!!.dictionary, MyApp.language!!.dictionary.dict[id_word].word)
        Log.d("wordForms", wordForms.toString())
        val listWordForms : ListView = findViewById(R.id.listWordForms)
        val adapterWordForms: ArrayAdapter<IWordEntity> = WordAdapter(this, wordForms)
        listWordForms.adapter = adapterWordForms
        adapterWordForms.notifyDataSetChanged()
    }

    fun updateNewWords(){
        //list of new words
        val listViewNewWords : ListView = findViewById(R.id.listViewNewWords)
        val list: MutableList<Pair<String, IWordEntity>> = dictionaryDao.createWordsFromExisting(
            MyApp.language!!.dictionary, MyApp.language!!.dictionary.dict[id_word]).toMutableList()
        val adapter: ArrayAdapter<Pair<String, IWordEntity>> = NewWordAdapter(this, list)
        listViewNewWords.adapter = adapter
        adapter.notifyDataSetChanged()
    }
    override fun finish(){
        val data = Intent()
        data.putExtra("lang", id_lang)
        data.putExtra("word", id_word)
        setResult(RESULT_OK, data)
        super.finish()
    }
}

private class NewWordAdapter(context: Context, listOfWords: MutableList<Pair<String, IWordEntity>>) :
    ArrayAdapter<Pair<String, IWordEntity>>(context, R.layout.new_word_line_activity, listOfWords) {
    companion object{
        val wordFormationRuleDao: WordFormationRuleDao = WordFormationRuleDaoImpl()
        val dictionaryDao: DictionaryDao = DictionaryDaoImpl()
    }
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
            if(position == buttonSave.tag){
                try {
                    dictionaryDao.addWord(MyApp.language!!.dictionary, word)
                } catch (e: ForbiddenSymbolsException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return newView
    }
}