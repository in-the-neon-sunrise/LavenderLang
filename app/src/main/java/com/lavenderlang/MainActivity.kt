package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.appcompat.app.AppCompatActivity
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageType
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.backend.dao.language.DictionaryDaoImpl
import com.lavenderlang.backend.dao.language.GrammarDaoImpl
import com.lavenderlang.backend.dao.language.LanguageDaoImpl
import com.lavenderlang.backend.dao.language.PunctuationDaoImpl
import com.lavenderlang.backend.dao.language.TranslatorDaoImpl
import com.lavenderlang.backend.dao.language.WritingDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.Attributes
import com.lavenderlang.backend.entity.help.CharacteristicEntity
import com.lavenderlang.backend.entity.help.MascEntity
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.help.TransformationEntity
import com.lavenderlang.backend.entity.language.LanguageEntity
import com.lavenderlang.backend.entity.rule.GrammarRuleEntity
import com.lavenderlang.backend.entity.word.AdjectiveEntity
import com.lavenderlang.backend.entity.word.AdverbEntity
import com.lavenderlang.backend.entity.word.NounEntity
import com.lavenderlang.backend.entity.word.VerbEntity
import com.lavenderlang.backend.service.Serializer

var languages : MutableMap<Int, LanguageEntity> = mutableMapOf()
var nextLanguageId : Int = 0


class MainActivity : AppCompatActivity() {
    private val createJSONLauncher = registerForActivityResult(CreateDocument("todo/todo")) { uri ->
        if (uri != null) {
            LanguageDaoImpl().writeToJSON(uri)
        }
    }
    private val createPDFLauncher = registerForActivityResult(CreateDocument("todo/todo")) { uri ->
        if (uri != null) {
            LanguageDaoImpl().writeToPDF(uri)
        }
    }

    private lateinit var storageHelper: SimpleStorageHelper

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
        storageHelper = SimpleStorageHelper(this)
        // fixme: will be in another activity

        /*if (DocumentFileCompat.getAccessibleAbsolutePaths(this).isEmpty()) {
            val REQUEST_CODE = 123123
            storageHelper.requestStorageAccess(REQUEST_CODE, null, StorageType.EXTERNAL)
        }*/

        Log.d("meowmeow", languages.keys.toString())


        if (languages.isEmpty()) LanguageDaoImpl().getLanguagesFromDB()

        if (!Python.isStarted()) Python.start(AndroidPlatform(this))








        /*if (languages.isEmpty()) {
            LanguageDaoImpl().createLanguage("Пример языка", "Пример :>")
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
            val word3 = AdjectiveEntity(
                0,
                "ccc",
                "красивый",
                partOfSpeech = PartOfSpeech.ADJECTIVE
            )
            languages[0]!!.vowels = "a"
            languages[0]!!.consonants = "b c d"
            dict.addWord(languages[0]!!.dictionary, word1)
            dict.addWord(languages[0]!!.dictionary, word2)
            dict.addWord(languages[0]!!.dictionary, word3)
            dict.addWord(
                languages[0]!!.dictionary, AdverbEntity(
                    0,
                    "ddd",
                    "красиво"
                )
            )

            val grammarHandler = GrammarDaoImpl()
            val rule = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.NOUN, mutableMapOf(Attributes.GENDER to arrayListOf(1))
                ), mutableMapOf(Attributes.NUMBER to 1, Attributes.CASE to 0),
                TransformationEntity(0, 1, "", "b")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule)
            val rule1 = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.VERB, mutableMapOf()
                ), mutableMapOf(Attributes.NUMBER to 1,
                    Attributes.PERSON to 0,
                    Attributes.TIME to 0,
                    Attributes.GENDER to 0,
                    Attributes.MOOD to 0),
                TransformationEntity(0, 1, "", "d")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule1)
            val rule2 = GrammarRuleEntity(
                0, MascEntity(
                    PartOfSpeech.ADVERB, mutableMapOf()
                ), mutableMapOf(Attributes.DEGREE_OF_COMPARISON to 1),
                TransformationEntity(0, 0, "", "d")
            )
            grammarHandler.addGrammarRule(languages[0]!!.grammar, rule2)

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
            WritingDaoImpl().addCapitalizedPartOfSpeech(languages[0]!!, PartOfSpeech.NOUN)
            PunctuationDaoImpl().updatePunctuationSymbol(languages[0]!!, 0, "MEOW");
        }*/









        if (languages.isNotEmpty()) Log.d("meowmeow",
            languages[0]!!.capitalizedPartsOfSpeech.toString())
        if (languages.isNotEmpty()) Log.d("meowmeow", languages.keys.toString())

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
                //LanguageDaoImpl().downloadLanguagePDF(languages.values.toList()[position], storageHelper, createPDFLauncher)
                //LanguageDaoImpl().downloadLanguageJSON(languages.values.toList()[position], storageHelper, createJSONLauncher)
                //LanguageDaoImpl().getLanguageFromFile(DocumentFileCompat.getAccessibleAbsolutePaths(this).values.toList()[0].toList()[0]+"/1.json", this)
                startActivity(intent)
            }
    }
}