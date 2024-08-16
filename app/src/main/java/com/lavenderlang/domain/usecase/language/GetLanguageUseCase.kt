package com.lavenderlang.domain.usecase.language

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageItem
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity
import com.lavenderlang.ui.MyApp

class GetLanguageUseCase {
    companion object {
        suspend fun execute(id: Int, repo: LanguageRepository): LanguageEntity? {
            val item = repo.getLanguage(id)
            if (item != null)
                return Serializer.getInstance().getLanguageEntityFromLanguageItem(item)
            return null
        }
    }
}