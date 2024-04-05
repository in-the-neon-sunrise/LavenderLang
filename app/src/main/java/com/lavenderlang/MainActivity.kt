package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.entity.language.LanguageEntity


var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0


class MainActivity : AppCompatActivity() {
    companion object {
        private var instance : MainActivity? = null
        fun getInstance() : MainActivity {
            if (instance == null) throw Exception("MainActivity is not created")
            return instance!!
        }
        fun setInstance(mainActivity: MainActivity) {
            if (instance == null) instance = mainActivity
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        setInstance(this)

        Log.d("meowmeow", languages.keys.toString())


        if (languages.isEmpty()) LanguageDaoImpl().getLanguagesFromDB(this)

        if (!Python.isStarted()) Python.start(AndroidPlatform(this))





        /*if (languages.isEmpty()) {
            LanguageDaoImpl().createLanguage("Пример языка", "Пример :>", this)
            val dict = DictionaryDaoImpl()
            val word1 = NounEntity(
                0,
                "aaa",
                "кошечка",
                immutableAttrs = mutableMapOf(Attributes.GENDER to 1),
                partOfSpeech = PartOfSpeech.NOUN
            )
            val word2 = VerbEntity(
                0,
                "bbb",
                "заплакать",
                partOfSpeech = PartOfSpeech.VERB
            )
            val word3 = VerbEntity(
                0,
                "ccc",
                "красивый",
                partOfSpeech = PartOfSpeech.ADJECTIVE
            )
            languages[0]!!.vowels = "a"
            languages[0]!!.consonants = "b c d"
            dict.addWord(languages[0]!!.dictionary, word1, this)
            dict.addWord(languages[0]!!.dictionary, word2, this)
            dict.addWord(languages[0]!!.dictionary, word3, this)
            dict.addWord(
                languages[0]!!.dictionary, AdverbEntity(
                    0,
                    "ddd",
                    "красиво"
                ), this
            )

            val grammarHandler = GrammarDaoImpl()
            val rule = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.NOUN, mutableMapOf(Attributes.GENDER to arrayListOf(1))
                ), mutableMapOf(Attributes.NUMBER to 1),
                TransformationEntity(0, 1, "", "b")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule, this)
            val rule1 = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.VERB, mutableMapOf()
                ), mutableMapOf(Attributes.NUMBER to 1),
                TransformationEntity(0, 1, "", "d")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule1, this)
            val rule2 = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.ADVERB, mutableMapOf()
                ), mutableMapOf(Attributes.DEGREE_OF_COMPARISON to 1),
                TransformationEntity(0, 0, "", "d")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule2, this)

            val TAG = "meowmeow"
            grammarHandler.addOption(
                languages[0]!!.grammar, CharacteristicEntity(
                    0,
                    languages[0]!!.grammar.nextIds[Attributes.GENDER]!!,
                    Attributes.GENDER,
                    "полосатые деревья",
                    2
                )
            )
            grammarHandler.addOption(
                languages[0]!!.grammar, CharacteristicEntity(
                    0,
                    languages[0]!!.grammar.nextIds[Attributes.GENDER]!!,
                    Attributes.GENDER,
                    "деревья в клеточку",
                    2
                )
            )
            WritingDaoImpl().addCapitalizedPartOfSpeech(languages[0]!!, PartOfSpeech.NOUN, this)
            PunctuationDaoImpl().updatePunctuationSymbol(languages[0]!!, 0, "MEOW", this);
        }*/




        Log.d("meowmeow", "onCreate")
        //LanguageDaoImpl().downloadLanguageJSON(languages[0]!!, this)

        //button new lang listener
        val buttonNewLang: Button = findViewById(R.id.buttonNewLang)
        buttonNewLang.setOnClickListener {
            val intent = Intent(this@MainActivity, LanguageActivity::class.java)
            intent.putExtra("lang", -1)
            startActivity(intent)
        }

        //go to information
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener {
            val intent = Intent(this@MainActivity, InformationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()


        val listLanguages: ListView = findViewById(R.id.listLanguages)
        val adapter: ArrayAdapter<LanguageEntity> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, languages.values.toList())
        listLanguages.adapter = adapter
        adapter.notifyDataSetChanged()
        listLanguages.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val intent = Intent(this@MainActivity, LanguageActivity::class.java)
                Log.d("meowmeow", "pos $position real ${languages.values.toList()[position].languageId}")
                intent.putExtra("lang", languages.values.toList()[position].languageId)
                startActivity(intent)
            }
    }
}