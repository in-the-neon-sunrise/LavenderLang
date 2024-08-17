package com.lavenderlang.domain.usecase.language

import android.content.Context
import android.net.Uri
import com.lavenderlang.domain.PdfWriterDaoImpl
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.model.language.LanguageEntity
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class WriteToPdfUseCase {
    companion object {
        fun execute(uri: Uri, language: LanguageEntity, context: Context) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = BufferedWriter(OutputStreamWriter(outputStream))
                writer.use { it.write(Serializer.getInstance().serializeLanguage(language)) }
            }
            PdfWriterDaoImpl().fullWriteToPdf(context, language, uri)
        }
    }

  }