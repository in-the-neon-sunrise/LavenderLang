package com.lavenderlang.ui.fragments

import androidx.lifecycle.ViewModel
import com.lavenderlang.data.UserRepositoryImpl
import com.lavenderlang.domain.usecase.LoginUseCase
import com.lavenderlang.domain.auth.State
import com.lavenderlang.domain.auth.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginViewModel : ViewModel() {

    suspend fun login(
        email: String,
        password: String
    ) : Flow<State> = flow {

        emit(State.LOADING)

        try {
            val repo = UserRepositoryImpl()

            LoginUseCase.execute(
                UserModel(email, password),
                repo
            )

            emit(State.SUCCESS)
        } catch (e: Exception) {
            emit(State.ERROR)
        }
    }

}
