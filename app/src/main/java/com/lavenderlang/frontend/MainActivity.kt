package com.lavenderlang.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.anggrayudi.storage.SimpleStorageHelper
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.R
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //activity creation

        Log.d("MainActivity", "onCreate")

        if (MyApp.nextLanguageId == -1) {
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
        }
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        if(getSharedPreferences("pref", MODE_PRIVATE).getBoolean("Theme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        MyApp.storageHelper = SimpleStorageHelper(this)
        if (!Python.isStarted()) Python.start(AndroidPlatform(MyApp.getInstance().applicationContext))
        //if (MyApp.mainActivityContext == null) MyApp.mainActivityContext = this




        /*val languageHandler = LanguageDaoImpl()
        languageHandler.createLanguage("Пример языка", "Пример :>")
        Log.e("lang created", languages.toString())

        val writingHandler = WritingDaoImpl()
        writingHandler.changeVowels(languages[0]!!, "a")
        Log.e("vowels", languages[0]!!.vowels)
        writingHandler.changeConsonants(languages[0]!!, "b c d")
        Log.e("consonants", languages[0]!!.consonants)
        WritingDaoImpl().addCapitalizedPartOfSpeech(languages[0]!!, PartOfSpeech.NOUN)
        Log.e("capitalized", languages[0]!!.capitalizedPartsOfSpeech.toString())

        val punctuationHandler = PunctuationDaoImpl()
        punctuationHandler.updatePunctuationSymbol(languages[0]!!, 0, "MEOW")
        Log.e("punctuation", languages[0]!!.puncSymbols.toString())

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
            Log.e("forbidden symbols", e.message.toString())
        }
        for (word in languages[0]!!.dictionary.dict) {
            Log.e(word.word, word.toString())
        }

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
            Log.e("forbidden symbols", e.message.toString())
        }
        Log.e("grammar rules", languages[0]!!.grammar.grammarRules.toString())
        val grammarRuleHandler = GrammarRuleDaoImpl()
        Log.e("grammar transform",
            grammarRuleHandler.grammarTransformByRule(rule1, word1).toString())
        grammarRuleHandler.updateRule(rule1, rule1.masc,
            TransformationEntity(0, 1, "", "bb"),
            mutableMapOf(Attributes.NUMBER to 1, Attributes.CASE to 1))
        sleep(2000)
        Log.e("full dict 0", languages[0]!!.dictionary.fullDict["aaa кошечка"].toString())
        grammarHandler.deleteGrammarRule(languages[0]!!.grammar,
            languages[0]!!.grammar.grammarRules[0])
        Log.e("grammar rules", languages[0]!!.grammar.grammarRules.toString())

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
        Log.e("vars gender", languages[0]!!.grammar.varsGender.toString())

        val rule5 = WordFormationRuleEntity(
            0, MascEntity(
                PartOfSpeech.NOUN, mutableMapOf(Attributes.GENDER to 1), "a{3}"
            ), mutableMapOf(), TransformationEntity(
                0, 1, "", "bb"),
            "Превращает существительное в наречие", PartOfSpeech.ADVERB
        )
        grammarHandler.addWordFormationRule(languages[0]!!.grammar, rule5)
        Log.e("word formation rules", languages[0]!!.grammar.wordFormationRules.toString())

        Log.e("created from",
            dictionaryHandler.createWordsFromExisting(languages[0]!!.dictionary, word1).toString())

        Log.e("filtered dict",
            dictionaryHandler.filterDictByPartOfSpeech(
                languages[0]!!.dictionary, PartOfSpeech.NOUN
            ).toString()
        )

        val translatorHandler = TranslatorDaoImpl()
        Log.e("text to conlang", translatorHandler.translateTextToConlang(languages[0]!!,
            "кошечки заплакали в неизвестности."))
        Log.e("text from conlang",
            translatorHandler.translateTextFromConlang(languages[0]!!, "Aaa bbbMEOW"))

        val translatorHelper = TranslatorHelperDaoImpl()
        Log.e("word to conlang",
            translatorHelper.translateWordToConlang(languages[0]!!, "кошечка"))
        try {
            translatorHelper.translateWordToConlang(languages[0]!!, "неизвестность")
        } catch (e: WordNotFoundException) {
            Log.e("word not found", e.message.toString())
        }


        dictionaryHandler.deleteWord(languages[0]!!.dictionary, word1)
        for (word in languages[0]!!.dictionary.dict) {
            Log.e(word.word, word.toString())
        }*/















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
        Log.d("MainActivity", "onResume")
        super.onResume()

        val languages = runBlocking {
            LanguageDaoImpl().getLanguagesFromDB()
        }

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

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("MainActivity", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy")
        super.onDestroy()
    }
}