package com.lavenderlang.backend.dao.language

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.rule.GrammarRuleDaoImpl
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.data.LanguageItem
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.PartOfSpeech
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.service.*
import com.lavenderlang.languages
import com.lavenderlang.nextLanguageId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

interface LanguageDao {
    fun changeName(language: LanguageEntity, newName: String)
    fun changeDescription(language: LanguageEntity, newDescription: String)
    fun copyLanguage(language: LanguageEntity)
    fun createLanguage(name: String, description: String)
    fun deleteLanguage(id: Int)
    fun getLanguagesFromDB()
    fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>)
    fun getLanguageFromFile(path: String, context: AppCompatActivity)
}
class LanguageDaoImpl(private val languageRepository: LanguageRepository = LanguageRepository()) : LanguageDao {
    companion object {
        var curLanguage: LanguageEntity? = null
    }
    override fun getLanguagesFromDB() {
            languageRepository.languages.observe(MainActivity.getInstance()
            ) { languageItemList: List<LanguageItem> ->
                run {
                    languages = mutableMapOf()
                    nextLanguageId = 0
                    for (e in languageItemList) {
                        languages[e.id] = Serializer.getInstance().deserializeLanguage(e.lang)
                        Log.d("woof", "loaded ${languages[e.id]}")
                        if (nextLanguageId <= e.id) nextLanguageId = e.id + 1
                    }
                }
            }
            languageRepository.loadAllLanguages(MainActivity.getInstance(), MainActivity.getInstance())
        }
    override fun changeName(language : LanguageEntity, newName : String) {
        language.name = newName
        if (language.languageId !in languages) return
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(MainActivity.getInstance(), language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }
    override fun changeDescription(language : LanguageEntity, newDescription: String) {
        language.description = newDescription
        if (language.languageId !in languages) return
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(MainActivity.getInstance(), language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
    }

    override fun copyLanguage(language: LanguageEntity) {
        val newLang = language.copy(languageId = nextLanguageId, name = language.name + " копия")
        languages[nextLanguageId++] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }
        return
    }
    override fun createLanguage(name: String, description: String) {
        val newLang = LanguageEntity(nextLanguageId, name, description)
        Log.d("woof", "new $newLang")
        languages[nextLanguageId] = newLang
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(MainActivity.getInstance(), newLang.languageId, Serializer.getInstance().serializeLanguage(newLang))
        }
        ++nextLanguageId
        return
    }
    override fun deleteLanguage(id: Int) {
        languages.remove(id)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.deleteLanguage(MainActivity.getInstance(), id)
        }
    }
    override fun getLanguageFromFile(path: String, context: AppCompatActivity) {
        val origFile = File(path)
        val file = DocumentFileCompat.fromFile(context, origFile)
        if (file == null) {
            Toast.makeText(context, "Не удалось загрузить язык", Toast.LENGTH_LONG).show()
            Log.d("woof", "no file")
            return
        }
        val inputStream = file.openInputStream(context)
        if (inputStream == null) {
            Toast.makeText(context, "Не удалось загрузить язык", Toast.LENGTH_LONG).show()
            Log.d("woof", "no input stream")
            return
        }
        val inputString = inputStream.bufferedReader().use { it.readText() }
        val language = Serializer.getInstance().deserializeLanguage(inputString)
        language.languageId = nextLanguageId

        language.grammar.languageId = nextLanguageId
        for (rule in language.grammar.grammarRules) {
            rule.languageId = nextLanguageId
        }
        for (rule in language.grammar.wordFormationRules) {
                rule.languageId = nextLanguageId
        }
        // fixme: characteristics
        for (word in language.dictionary.dict) {
            word.languageId = nextLanguageId
        }
        for (key in language.dictionary.fullDict.keys) {
            for (word in language.dictionary.fullDict[key]!!) {
                word.languageId = nextLanguageId
            }
        }

        language.dictionary.languageId = nextLanguageId

        languages[nextLanguageId] = language
        ++nextLanguageId
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.insertLanguage(context, language.languageId, Serializer.getInstance().serializeLanguage(language))
        }
        Toast.makeText(context, "Язык успешно загружен", Toast.LENGTH_LONG).show()
        Log.d("woof", "loaded ${language.name}")
    }

    override fun downloadLanguageJSON(language: LanguageEntity, storageHelper: SimpleStorageHelper,
                                      createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            Toast.makeText(MainActivity.getInstance(),
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно :(",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDao().translitName(language.name)}.json")
        Log.d("woof", "json done i hope")
    }

    override fun downloadLanguagePDF(language: LanguageEntity, storageHelper: SimpleStorageHelper, createDocumentResultLauncher: ActivityResultLauncher<String>) {
        val accessible = DocumentFileCompat.getAccessibleAbsolutePaths(MainActivity.getInstance())
        if (accessible.isEmpty()) {
            Toast.makeText(MainActivity.getInstance(),
                "Вы не дали приложению доступ к памяти телефона, сохранение невозможно",
                Toast.LENGTH_LONG).show()
            Log.d("woof", "no access")
            return
        }
        Log.d("woof", "path:"+accessible.values.toList()[0].toList()[0])
        curLanguage = language
        createDocumentResultLauncher.launch("${PdfWriterDao().translitName(language.name)}.pdf")
        Log.d("woof", "pdf done i hope")
    }

    fun writeToJSON(uri: Uri) {
        Log.d("woof", "writing json")
        val context = MainActivity.getInstance()
        if (curLanguage == null) {
            Log.d("woof", "no language")
            Toast.makeText(context, "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
            return
        }
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use {
                it.write(
                    Serializer.getInstance().serializeLanguage(curLanguage!!)
                )
            }
        }
    }

    fun writeToPDF(uri: Uri) {
        val context = MainActivity.getInstance()
        if (curLanguage == null) {
            Log.d("woof", "no language")
            Toast.makeText(context, "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
            return
        }
        val language = curLanguage!!
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
        }
        val file: DocumentFile = DocumentFileCompat.fromUri(context, uri)!!
        val output = file.openOutputStream(context)
        val document = PdfDocument()
        val width = 595
        val height = 842
        val pagesNum = 20 // fixme: count pages
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, pagesNum).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()

        // constants
        val heading1Size = 16F
        val heading2Size = 14F
        val normalTextSize = 12F
        val lineStart = width.toFloat() / 20
        val lineLength = width.toFloat() * 18 / 20
        val lineEnd = +lineLength
        val lineSpacing = 18F
        val purple = Color.rgb(102, 0, 102)


        // name
        paint.setColor(purple)
        paint.textSize = heading1Size
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        var text = language.name
        var x = (pageInfo.pageWidth.toFloat() / 2) - (paint.measureText(text) / 2)
        var y = height.toFloat() / 50F
        canvas.drawText(text, x, y, paint)

        // description
        paint.textSize = normalTextSize
        text = "Описание: "
        x = lineStart
        y += lineSpacing * 2 // different space between first line and the heading
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = language.description
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text,
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )


        // vowels
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "Гласные: "
        y += lineSpacing * 2
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = language.vowels
        canvas.drawText(text, x, y, paint) // definitely fits in one line


        // consonants
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "Согласные: "
        x = lineStart
        y += lineSpacing * 2
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = language.consonants
        canvas.drawText(text, x, y, paint) // definitely fits in one line


        // punctuation symbols
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "Нестандартные знаки препинания: "
        y += lineSpacing * 2
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = ""
        for (key in language.puncSymbols.keys) {
            if (key != language.puncSymbols[key]!!) text += "$key - ${language.puncSymbols[key]!!}, "
        }
        if (text.length > 2) y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0..<text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        else canvas.drawText("-", x, y, paint)
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }


        // capitalized parts of speech
        val rusPartsOfSpeech = mutableMapOf(
            PartOfSpeech.NOUN to "существительное",
            PartOfSpeech.VERB to "глагол",
            PartOfSpeech.ADJECTIVE to "прилагательное",
            PartOfSpeech.ADVERB to "наречие",
            PartOfSpeech.PARTICIPLE to "причастие",
            PartOfSpeech.VERB_PARTICIPLE to "деепричастие",
            PartOfSpeech.PRONOUN to "местоимение",
            PartOfSpeech.NUMERAL to "числительное",
            PartOfSpeech.FUNC_PART to "служебное слово"
        )

        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "Части речи с заглавной буквы:"
        y += lineSpacing * 2
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val res = arrayListOf<String>()
        for (pos in language.capitalizedPartsOfSpeech) res.add(rusPartsOfSpeech[pos]!!)
        text = res.joinToString(", ")
        if (text.length > 2) y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text,
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        else canvas.drawText("-", x, y, paint)
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }


        // grammar
        y += lineSpacing * 2
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = heading2Size
        text = "Грамматика"
        x = (pageInfo.pageWidth.toFloat() / 2) - (paint.measureText(text) / 2)
        canvas.drawText(text, x, y, paint)


        // characteristics
        y += lineSpacing * 2 // different space between the heading and the next line
        paint.textSize = normalTextSize
        x = lineStart
        text = "Варианты характеристик:"
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = "В скобках указана соответствующая характеристика в русском языке."
        canvas.drawText(text, x, y, paint)

        // gender
        y += lineSpacing
        text = "РОД: "
        for (characteristic in language.grammar.varsGender.values) text += "${characteristic.name} (${rusGender[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // number
        y += lineSpacing
        text = "ЧИСЛО: "
        for (characteristic in language.grammar.varsNumber.values) text += "${characteristic.name} (${rusNumber[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // case
        y += lineSpacing
        text = "ПАДЕЖ: "
        for (characteristic in language.grammar.varsCase.values) text += "${characteristic.name} (${rusCase[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // time
        y += lineSpacing
        text = "ВРЕМЯ: "
        for (characteristic in language.grammar.varsTime.values) text += "${characteristic.name} (${rusTime[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // person
        y += lineSpacing
        text = "ЛИЦО: "
        for (characteristic in language.grammar.varsPerson.values) text += "${characteristic.name} (${rusPerson[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // mood
        y += lineSpacing
        text = "НАКЛОНЕНИЕ: "
        for (characteristic in language.grammar.varsMood.values) text += "${characteristic.name} (${rusMood[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // type
        y += lineSpacing
        text = "ВИД: "
        for (characteristic in language.grammar.varsType.values) text += "${characteristic.name} (${rusType[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // voice
        y += lineSpacing
        text = "ЗАЛОГ: "
        for (characteristic in language.grammar.varsVoice.values) text += "${characteristic.name} (${rusVoice[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }

        // degree of comparison
        y += lineSpacing
        text = "СТЕПЕНЬ СРАВНЕНИЯ: "
        for (characteristic in language.grammar.varsDegreeOfComparison.values) text += "${characteristic.name} (${rusDegreeOfComparison[characteristic.russianId]}), "
        y = PdfWriterDao().drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing,
            lineEnd
        )
        if (y < 0) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = lineStart
        }


        // grammar rules
        y += lineSpacing * 2 // different space between the heading and the next line
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        x = lineStart
        text = "Грамматические правила:"
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val grammarRuleHandler = GrammarRuleDaoImpl()
        var grammarRuleNumber = 1
        for (rule in language.grammar.grammarRules) {
            text =
                "${grammarRuleNumber++}. НАЧАЛЬНОЕ СЛОВО: ${grammarRuleHandler.getOrigInfo(rule)}. "
            text += "Слово должно удовлетворять регулярному выражению: \"${rule.masc.regex}\""
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text =
                "ПРЕОБРАЗОВАНИЕ: убрать из начала слова ${rule.transformation.delFromBeginning} букв, из конца - ${rule.transformation.delFromEnd} букв;"
            text += " добавить в начало \"${rule.transformation.addToBeginning}\", в конец - \"${rule.transformation.addToEnd}\""
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "ИТОГОВОЕ СЛОВО: ${grammarRuleHandler.getResultInfo(rule)}"
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y =
                    0F // чтобы работала строчка на 2 ниже, нам не нужно делать лишний отсуп от верха страницы
            }
            y += lineSpacing * 2
        }
        if (grammarRuleNumber == 1) {
            canvas.drawText("-", x, y, paint)
            y += lineSpacing
        }


        // word formation rules
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        text = "Правила словообразования:"
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val wordFormationRuleHandler = WordFormationRuleDaoImpl()
        var wordFormationRuleNumber = 1
        for (rule in language.grammar.wordFormationRules) {
            text = "${wordFormationRuleNumber++}. ОПИСАНИЕ ПРАВИЛА: ${rule.description}"
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "НАЧАЛЬНОЕ СЛОВО: ${wordFormationRuleHandler.getOrigInfo(rule)}. "
            text += "Слово должно удовлетворять регулярному выражению: \"${rule.masc.regex}\""
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text =
                "ПРЕОБРАЗОВАНИЕ: убрать из начала слова ${rule.transformation.delFromBeginning} букв, из конца - ${rule.transformation.delFromEnd} букв;"
            text += " добавить в начало \"${rule.transformation.addToBeginning}\", в конец - \"${rule.transformation.addToEnd}\""
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "ИТОГОВОЕ СЛОВО: ${wordFormationRuleHandler.getResultInfo(rule)}"
            y = PdfWriterDao().drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing,
                lineEnd
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y =
                    0F // чтобы работала строчка на 2 ниже, нам не нужно делать лишний отсуп от верха страницы
            }
            y += lineSpacing * 2
        }
        if (wordFormationRuleNumber == 1) {
            canvas.drawText("-", x, y, paint)
            y += lineSpacing
        }


        // dictionary
        paint.setColor(purple)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = heading2Size
        text = "Словарь"
        y += lineSpacing * 2
        x = (pageInfo.pageWidth.toFloat() / 2) - (paint.measureText(text) / 2)
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = normalTextSize

        // table constants
        val tableTop = y
        val rowHeight = (normalTextSize + lineSpacing) * 1.5F
        val columnWidths = arrayListOf(
            (lineEnd - lineStart) / 4, (lineEnd - lineStart) / 4,
            (lineEnd - lineStart) / 4 / 1.5F, (lineEnd - lineStart) / 3
        ) // different length so that the last column with characteristics is wider
        val columnCount = 4
        val padding = rowHeight / 10F

        // working with multiple pages
        var rowsLeft = language.dictionary.dict.size + 1 // +1 for the heading
        var rowCount = rowsLeft % (height / rowHeight).toInt()
        var curWordId = 0

        while (rowCount > 0) {

            // all vertical lines
            canvas.drawLine(lineStart, tableTop, lineStart, tableTop + rowHeight * rowCount, paint)
            var prev = lineStart
            for (w in columnWidths) {
                canvas.drawLine(
                    prev + w,
                    tableTop,
                    prev + w,
                    tableTop + rowHeight * rowCount,
                    paint
                )
                prev += w
            }

            // all horizontal lines
            for (i in 0..rowCount) {
                canvas.drawLine(
                    lineStart,
                    tableTop + rowHeight * i,
                    lineEnd,
                    tableTop + rowHeight * i,
                    paint
                )
            }


            // table heading
            y = tableTop + padding + normalTextSize
            var textArray = arrayListOf("Слово", "Перевод", "Часть речи", "Неизм. характеристики")
            prev = lineStart
            for (i in 0 until columnCount) {
                text = textArray[i]
                x = prev + (columnWidths[i] - paint.measureText(text)) / 2
                canvas.drawText(text, x, y, paint)
                prev += columnWidths[i]
            }
            y += rowHeight

            // words
            val shortRusPartsOfSpeech = mutableMapOf(
                PartOfSpeech.NOUN to "сущ.",
                PartOfSpeech.VERB to "гл.",
                PartOfSpeech.ADJECTIVE to "прил.",
                PartOfSpeech.ADVERB to "нар.",
                PartOfSpeech.PARTICIPLE to "прич.",
                PartOfSpeech.VERB_PARTICIPLE to "деепр.",
                PartOfSpeech.PRONOUN to "мест.",
                PartOfSpeech.NUMERAL to "числ.",
                PartOfSpeech.FUNC_PART to "служ."
            )


            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val wordHandler = WordDaoImpl()
            for (word in language.dictionary.dict.slice(curWordId..<curWordId + rowCount - 1)) {
                ++curWordId
                textArray = arrayListOf(
                    word.word,
                    word.translation,
                    shortRusPartsOfSpeech[word.partOfSpeech]!!
                )
                prev = lineStart
                for (i in 0 until textArray.size) {
                    text = textArray[i]
                    x = prev + (columnWidths[i] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    prev += columnWidths[i]
                }
                // separately for the last column with characteristics
                if (word.partOfSpeech == PartOfSpeech.VERB) {
                    val textList = wordHandler.getImmutableAttrsInfo(word).split(", ")
                    text = textList[0]
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight / 2
                    text = textList[1]
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight / 2
                } else {
                    text = wordHandler.getImmutableAttrsInfo(word)
                    if (text.isEmpty()) text = "-"
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight
                }
            }
            document.finishPage(page)
            rowsLeft -= rowCount
            rowCount = rowsLeft % (height / rowHeight).toInt()
            if (rowCount > 0) {
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
        }
        document.writeTo(output)
    }
}