package com.lavenderlang.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.anggrayudi.storage.SimpleStorageHelper
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.language.PunctuationDaoImpl
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.help.TransformationEntity
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.rule.WordFormationRuleEntity
import com.lavenderlang.backend.entity.word.AdjectiveEntity
import com.lavenderlang.backend.entity.word.AdverbEntity
import com.lavenderlang.backend.entity.word.NounEntity
import com.lavenderlang.backend.entity.word.VerbEntity
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException

var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0
var isDark: Boolean = false

class MainActivity : AppCompatActivity() {
    lateinit var storageHelper: SimpleStorageHelper

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

        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        isDark=getSharedPreferences("Theme", Context.MODE_PRIVATE).getBoolean("isDark", false)
        if(isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setInstance(this)
        storageHelper = SimpleStorageHelper(this)


        if (!Python.isStarted()) Python.start(AndroidPlatform(this))





        /*val languageHandler = LanguageDaoImpl()
        languageHandler.createLanguage("Пример языка", "Пример :>")
        Log.d("lang created", languages.toString())

        val writingHandler = WritingDaoImpl()
        writingHandler.changeVowels(languages[0]!!, "a")
        Log.d("vowels", languages[0]!!.vowels)
        writingHandler.changeConsonants(languages[0]!!, "b c d")
        Log.d("consonants", languages[0]!!.consonants)
        WritingDaoImpl().addCapitalizedPartOfSpeech(languages[0]!!, PartOfSpeech.NOUN)
        Log.d("capitalized", languages[0]!!.capitalizedPartsOfSpeech.toString())

        val punctuationHandler = PunctuationDaoImpl()
        punctuationHandler.updatePunctuationSymbol(languages[0]!!, 0, "MEOW")
        Log.d("punctuation", languages[0]!!.puncSymbols.toString())

        val dictionaryHandler = DictionaryDaoImpl()
        val word1 = NounEntity(
            0,
            "aaa",
            "кошечка",
            immutableAttrs = mutableMapOf(Attributes.GENDER to 1)
        )
        val word2 = VerbEntity(
            0,
            "bbb",
            "заплакать"
        )
        val word3 = AdjectiveEntity(
            0,
            "ccc",
            "красивый"
        )
        val word4 = AdverbEntity(
            0,
            "forbiddensymbols",
            "красиво"
        )
        dictionaryHandler.addWord(languages[0]!!.dictionary, word1)
        dictionaryHandler.addWord(languages[0]!!.dictionary, word2)
        dictionaryHandler.addWord(languages[0]!!.dictionary, word3)
        try {
            dictionaryHandler.addWord(languages[0]!!.dictionary, word4)
        } catch (e: ForbiddenSymbolsException) {
            Log.d("forbidden symbols", e.message.toString())
            sleep(1000)
        }
        Log.d("dictionary", languages[0]!!.dictionary.dict.toString())

        val grammarHandler = GrammarDaoImpl()
        val rule1 = GrammarRuleEntity(0,
            MascEntity(
                PartOfSpeech.NOUN, mutableMapOf(Attributes.GENDER to 1)
            ),
            mutableMapOf(Attributes.NUMBER to 1, Attributes.CASE to 0),
            TransformationEntity(0, 1, "", "b")
        )
        grammarHandler.addGrammarRule(languages[0]!!.grammar, rule1)
        val rule2 = GrammarRuleEntity(
            0, MascEntity(
                PartOfSpeech.VERB, mutableMapOf(Attributes.TYPE to 0, Attributes.VOICE to 0)
            ), mutableMapOf(
                Attributes.NUMBER to 1,
                Attributes.PERSON to 0,
                Attributes.TIME to 0,
                Attributes.GENDER to 0,
                Attributes.MOOD to 0
            ),
            TransformationEntity(0, 1, "", "d")
        )
        grammarHandler.addGrammarRule(languages[0]!!.grammar, rule2)
        val rule3 = GrammarRuleEntity(
            0, MascEntity(PartOfSpeech.ADVERB),
            mutableMapOf(Attributes.DEGREE_OF_COMPARISON to 1),
            TransformationEntity(0, 0, "", "d")
        )
        grammarHandler.addGrammarRule(languages[0]!!.grammar, rule3)
        val rule4 = GrammarRuleEntity(
            0, MascEntity(PartOfSpeech.ADVERB),
            mutableMapOf(Attributes.DEGREE_OF_COMPARISON to 1),
            TransformationEntity(0, 0, "", "f")
        )
        try {
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule4)
        } catch (e: ForbiddenSymbolsException) {
            Log.d("forbidden symbols", e.message.toString())
        }
        Log.d("grammar rules", languages[0]!!.grammar.grammarRules.toString())

        grammarHandler.addOption(
            languages[0]!!.grammar, CharacteristicEntity(
                languages[0]!!.grammar.nextIds[Attributes.GENDER]!!,
                Attributes.GENDER,
                "полосатые деревья",
                2
            )
        )
        grammarHandler.addOption(
            languages[0]!!.grammar, CharacteristicEntity(
                languages[0]!!.grammar.nextIds[Attributes.GENDER]!!,
                Attributes.GENDER,
                "деревья в клеточку",
                2
            )
        )
        Log.d("vars gender", languages[0]!!.grammar.varsGender.toString())

        val rule5 = WordFormationRuleEntity(
            0, MascEntity(
                PartOfSpeech.NOUN, mutableMapOf(Attributes.GENDER to 1), "a{3}"
            ), mutableMapOf(), TransformationEntity(
                0, 1, "", "bb"),
            "Превращает существительное в наречие", PartOfSpeech.ADVERB
        )
        grammarHandler.addWordFormationRule(languages[0]!!.grammar, rule5)
        Log.d("word formation rules", languages[0]!!.grammar.wordFormationRules.toString())

        Log.d("created from",
            dictionaryHandler.createWordsFromExisting(languages[0]!!.dictionary, word1).toString())

        Log.d("filtered dict",
            dictionaryHandler.filterDictByPartOfSpeech(
                languages[0]!!.dictionary, PartOfSpeech.NOUN
            ).toString()
        )*/











        //button new lang listener
        val buttonNewLang: Button = findViewById(R.id.buttonNewLang)
        buttonNewLang.setOnClickListener {
            val intent = Intent(this@MainActivity, LanguageActivity::class.java)
            intent.putExtra("lang", -1)
            startActivity(intent)
        }

        //go to load language
        val buttonFromFile: Button = findViewById(R.id.buttonFromFile)
        buttonFromFile.setOnClickListener {
            val intent = Intent(this@MainActivity, LoadLanguageActivity::class.java)
            startActivity(intent)
        }

        //go to information
        val buttonInformation: Button = findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener {
            val intent = Intent(this@MainActivity, InformationActivity::class.java)
            startActivity(intent)
        }
        //bottom navigation menu
        val buttonTranslator: Button = findViewById(R.id.buttonTranslator)
        buttonTranslator.setOnClickListener {
            val intent = Intent(this, TranslatorActivity::class.java)
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
                intent.putExtra("lang", languages.values.toList()[position].languageId)
                startActivity(intent)
            }
    }
}