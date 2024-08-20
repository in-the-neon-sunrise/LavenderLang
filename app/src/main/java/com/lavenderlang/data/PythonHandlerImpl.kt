package com.lavenderlang.data

import com.chaquo.python.Python
import com.lavenderlang.domain.db.PythonHandler

class PythonHandlerImpl : PythonHandler {
    override fun inflectAttrs(word: String, partOfSpeech: String, attrs: String): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        return module.callAttr("inflect_attrs", word, partOfSpeech, attrs).toString()
    }

    override fun getNormalForm(word: String): String {
        val py = Python.getInstance()
        val module = py.getModule("pm3")
        return module.callAttr("get_normal_form", word).toString()
    }
}