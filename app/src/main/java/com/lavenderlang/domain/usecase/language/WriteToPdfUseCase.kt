package com.lavenderlang.domain.usecase.language

import android.content.Context
import android.net.Uri
import com.lavenderlang.domain.PdfWriter
import com.lavenderlang.domain.Serializer
import com.lavenderlang.domain.model.language.LanguageEntity
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class WriteToPdfUseCase {
    companion object {
        fun execute(uri: Uri, language: LanguageEntity, context: Context, pdfWriter: PdfWriter) {
            pdfWriter.fullWriteToPdf(context, language, uri)
        }
    }
  }