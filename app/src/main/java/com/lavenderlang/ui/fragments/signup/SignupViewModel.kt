package com.lavenderlang.ui.fragments.signup

import androidx.lifecycle.ViewModel
import com.lavenderlang.data.UserRepositoryImpl
import com.lavenderlang.domain.RegisterUseCase
import com.lavenderlang.domain.State
import com.lavenderlang.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignupViewModel : ViewModel() {

    suspend fun register(
        email: String,
        password: String
    ) : Flow<State> = flow {

        emit(State.LOADING)

        try {
            val repo = UserRepositoryImpl()

            RegisterUseCase.execute(
                UserModel(email, password),
                repo
            )

            emit(State.SUCCESS)
        } catch (e: Exception) {
            emit(State.ERROR)
        }
    }

}
