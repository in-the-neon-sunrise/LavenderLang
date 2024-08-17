package com.lavenderlang.ui.fragments

import androidx.lifecycle.ViewModel
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.db.LanguageIdAndName
import com.lavenderlang.domain.usecase.language.GetShortLanguagesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {
    private val _langs = MutableStateFlow(emptyList<LanguageIdAndName>())
    val langs: Flow<List<LanguageIdAndName>> = _langs

    suspend fun getShortLanguagesFromDB() : Flow<Boolean> = flow {
        emit(false)
        _langs.value = GetShortLanguagesUseCase.execute(LanguageRepositoryImpl())
        emit(true)
    }
}