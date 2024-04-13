package com.lavenderlang.backend.dao.language

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument

class PdfWriterDao {
    fun translitName(name: String) : String {
        val dict = mutableMapOf("а" to "a", "б" to "b", "в" to "v", "г" to "g", "д" to "d",
            "е" to "e", "ё" to "yo", "ж" to "zh", "з" to "z", "и" to "i", "й" to "y", "к" to "k",
            "л" to "l", "м" to "m", "н" to "n", "о" to "o", "п" to "p", "р" to "r", "с" to "s",
            "т" to "t", "у" to "u", "ф" to "f", "х" to "kh", "ц" to "ts", "ч" to "ch", "ш" to "sh",
            "щ" to "shch", "ы" to "y", "э" to "e", "ю" to "yu", "я" to "ya", "А" to "A", "Б" to "B",
            "В" to "V", "Г" to "G", "Д" to "D", "Е" to "E", "Ё" to "Yo", "Ж" to "Zh", "З" to "Z",
            "И" to "I", "Й" to "Y", "К" to "K", "Л" to "L", "М" to "M", "Н" to "N", "О" to "O",
            "П" to "P", "Р" to "R", "С" to "S", "Т" to "T", "У" to "U", "Ф" to "F", "Х" to "Kh",
            "Ц" to "Ts", "Ч" to "Ch", "Ш" to "Sh", "Щ" to "Shch", "Ы" to "Y", "Э" to "E",
            "Ю" to "Yu", "Я" to "Ya", " " to "_", "1" to "1", "2" to "2", "3" to "3", "4" to "4",
            "5" to "5", "6" to "6", "7" to "7", "8" to "8", "9" to "9", "0" to "0")
        var res = ""
        for (c in name) {
            if (c.toString() in dict.keys) {
                res += dict[c.toString()]!!
            }
        }
        return res
    }

    fun drawMultipleLines(canvas: Canvas,
                                  paint: Paint,
                                  text: String,
                                  pageInfo: PdfDocument.PageInfo,
                                  startY: Float,
                                  lineStart: Float,
                                  lineSpacing: Float,
                                  lineEnd: Float) : Float {
        val words = text.split(" ")
        var line = ""
        var y = startY

        for (word in words) {
            if (y > pageInfo.pageHeight - lineStart) {
                return -1F // end of page
            }
            if (paint.measureText("$line $word") > lineEnd) {
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
}