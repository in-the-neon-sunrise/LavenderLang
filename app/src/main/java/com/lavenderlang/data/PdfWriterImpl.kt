package com.lavenderlang.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.openOutputStream
import com.lavenderlang.domain.PdfWriter
import com.lavenderlang.domain.getOrigInfo
import com.lavenderlang.domain.getResultInfo
import com.lavenderlang.domain.model.help.Attributes
import com.lavenderlang.domain.model.help.PartOfSpeech
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.domain.model.word.IWordEntity
import com.lavenderlang.domain.rusCase
import com.lavenderlang.domain.rusDegreeOfComparison
import com.lavenderlang.domain.rusGender
import com.lavenderlang.domain.rusMood
import com.lavenderlang.domain.rusNumber
import com.lavenderlang.domain.rusPerson
import com.lavenderlang.domain.rusTime
import com.lavenderlang.domain.rusType
import com.lavenderlang.domain.rusVoice

class PdfWriterImpl : PdfWriter {
    fun translitName(name: String): String {
        val dict = mutableMapOf(
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "д" to "d",
            "е" to "e",
            "ё" to "yo",
            "ж" to "zh",
            "з" to "z",
            "и" to "i",
            "й" to "y",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "ф" to "f",
            "х" to "kh",
            "ц" to "ts",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "shch",
            "ы" to "y",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya",
            "А" to "A",
            "Б" to "B",
            "В" to "V",
            "Г" to "G",
            "Д" to "D",
            "Е" to "E",
            "Ё" to "Yo",
            "Ж" to "Zh",
            "З" to "Z",
            "И" to "I",
            "Й" to "Y",
            "К" to "K",
            "Л" to "L",
            "М" to "M",
            "Н" to "N",
            "О" to "O",
            "П" to "P",
            "Р" to "R",
            "С" to "S",
            "Т" to "T",
            "У" to "U",
            "Ф" to "F",
            "Х" to "Kh",
            "Ц" to "Ts",
            "Ч" to "Ch",
            "Ш" to "Sh",
            "Щ" to "Shch",
            "Ы" to "Y",
            "Э" to "E",
            "Ю" to "Yu",
            "Я" to "Ya",
            " " to "_"
        )
        var res = ""
        for (c in name) {
            if (c in '0'..'9' || c in 'a'..'z' || c in 'A'..'Z') res += c
            else if (c.toString() in dict.keys) {
                res += dict[c.toString()]!!
            }
        }
        return res
    }

    private fun Float.drawMultipleLines(
        canvas: Canvas,
        paint: Paint,
        text: String,
        pageInfo: PdfDocument.PageInfo,
        startY: Float,
        lineStart: Float,
        lineSpacing: Float
    ) : Float {
        val words = text.split(" ")
        var line = ""
        var y = startY

        for (word in words) {
            if (y > pageInfo.pageHeight - lineStart) {
                return -1F // end of page
            }
            if (paint.measureText("$line $word") > this) {
                canvas.drawText(line, lineStart, y, paint)
                line = word
                y += lineSpacing
            } else {
                line = if (line.isEmpty()) word else "$line $word"
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, lineStart, y, paint)
            y += lineSpacing
        }
        return y - lineSpacing
    }

    override fun fullWriteToPdf(context: Context, language: LanguageEntity, uri: Uri) {
        val file: DocumentFile = DocumentFileCompat.fromUri(context, uri)!!
        val output = file.openOutputStream(context)
        val document = PdfDocument()
        val width = 595
        val height = 842
        val pagesNum = 50 // с запасом
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text,
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        if (text.length > 2) y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0..<text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        if (text.length > 2) y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text,
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        text = "Варианты признаков:"
        canvas.drawText(text, x, y, paint)

        paint.setColor(Color.BLACK)
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        text = "В скобках указана соответствующий признак в русском языке."
        canvas.drawText(text, x, y, paint)

        // gender
        y += lineSpacing
        text = "РОД: "
        for (characteristic in language.grammar.varsGender.values) text += "${characteristic.name} (${rusGender[characteristic.russianId]}), "
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        y = lineEnd.drawMultipleLines(
            canvas,
            paint,
            text.slice(0 until text.length - 2),
            pageInfo,
            y,
            lineStart,
            lineSpacing
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
        var grammarRuleNumber = 1
        for (rule in language.grammar.grammarRules) {
            text =
                "${grammarRuleNumber++}. НАЧАЛЬНОЕ СЛОВО: ${getOrigInfo(rule)}. "
            text += "Слово должно удовлетворять регулярному выражению: \"${rule.masc.regex}\""
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
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
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "ИТОГОВОЕ СЛОВО: ${getResultInfo(rule)}"
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
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
        var wordFormationRuleNumber = 1
        for (rule in language.grammar.wordFormationRules) {
            text = "${wordFormationRuleNumber++}. ОПИСАНИЕ ПРАВИЛА: ${rule.description}"
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "НАЧАЛЬНОЕ СЛОВО: ${getOrigInfo(rule)}. "
            text += "Слово должно удовлетворять регулярному выражению: \"${rule.masc.regex}\""
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
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
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
            }
            y += lineSpacing
            text = "ИТОГОВОЕ СЛОВО: ${getResultInfo(rule)}"
            y = lineEnd.drawMultipleLines(
                canvas,
                paint,
                text,
                pageInfo,
                y,
                lineStart,
                lineSpacing
            )
            if (y < 0) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = -lineSpacing // чтобы работала строчка на 2 ниже, нам не нужно делать лишний отсуп от верха страницы
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
        var tableTop = y
        val rowHeight = (normalTextSize + lineSpacing) * 1.5F
        val columnWidths = arrayListOf(
            (lineEnd - lineStart) / 4, (lineEnd - lineStart) / 4,
            (lineEnd - lineStart) / 4 / 1.5F, (lineEnd - lineStart) / 3
        ) // different length so that the last column with characteristics is wider
        val columnCount = 4
        val padding = rowHeight / 10F

        // working with multiple pages
        var rowsLeft = language.dictionary.dict.size + 1 // +1 for the heading
        var rowCount = rowsLeft % ((height - y) / rowHeight).toInt()
        var curWordId = 0

        // table heading
        y = tableTop + padding + normalTextSize
        var textArray = arrayListOf("Слово", "Перевод", "Часть речи", "Пост. признаки")
        var prev = lineStart
        for (i in 0 until columnCount) {
            text = textArray[i]
            x = prev + (columnWidths[i] - paint.measureText(text)) / 2
            canvas.drawText(text, x, y, paint)
            prev += columnWidths[i]
        }
        y += rowHeight
        var isPageFinished = false

        while (rowCount > 0) {

            // all vertical lines
            canvas.drawLine(lineStart, tableTop, lineStart, tableTop + rowHeight * rowCount, paint)
            prev = lineStart
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
                if (word.partOfSpeech == PartOfSpeech.VERB ||
                    word.partOfSpeech == PartOfSpeech.PARTICIPLE) {
                    val textList = getImmutableAttrsInfo(word, language).split(", ")
                    text = textList[0]
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight / 2
                    text = textList[1]
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight / 2
                } else {
                    text = getImmutableAttrsInfo(word, language)
                    if (text.isEmpty()) text = "-"
                    x = prev + (columnWidths[3] - paint.measureText(text)) / 2
                    canvas.drawText(text, x, y, paint)
                    y += rowHeight
                }
            }
            document.finishPage(page)
            rowsLeft -= rowCount
            rowCount = rowsLeft % ((height - lineStart) / rowHeight).toInt()
            isPageFinished = true
            if (rowCount > 0) {
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = lineStart
                tableTop = y
                isPageFinished = false
            }
        }
        if (!isPageFinished) {
            document.finishPage(page)
        }
        document.writeTo(output)
    }

    private fun getImmutableAttrsInfo(word: IWordEntity, language: LanguageEntity): String {
        var res = ""
        for (attr in word.immutableAttrs.keys) {
            res += when (attr) {
                Attributes.GENDER -> "род: ${language.grammar.varsGender[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.TYPE -> "вид: ${language.grammar.varsType[word.immutableAttrs[attr]!!]?.name}, "
                Attributes.VOICE -> "залог: ${language.grammar.varsVoice[word.immutableAttrs[attr]!!]?.name}, "
                else -> ""
            }
        }
        if (res.length < 2) return ""
        return res.slice(0 until res.length - 2)
    }

}