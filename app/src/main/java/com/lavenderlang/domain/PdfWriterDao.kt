package com.lavenderlang.domain

import android.content.Context
import android.net.Uri
import com.lavenderlang.domain.model.language.LanguageEntity

interface PdfWriter {
    fun fullWriteToPdf(context: Context, language: LanguageEntity, uri: Uri)
}

