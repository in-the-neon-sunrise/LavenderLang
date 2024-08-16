package com.lavenderlang.ui.fragments.signup

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.lavenderlang.data.UserRepositoryImpl
import com.lavenderlang.domain.usecase.RegisterUseCase
import com.lavenderlang.domain.auth.State
import com.lavenderlang.domain.auth.UserModel
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

        } catch (e: FirebaseAuthUserCollisionException) {
            emit(State.ERROR_USER_ALREADY_EXISTS)
        } catch (e: FirebaseAuthWeakPasswordException) {
            emit(State.ERROR_WEAK_PASSWORD)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(State.ERROR_INVALID_CREDENTIALS)
        } catch (e: Exception) {
            emit(State.ERROR)
        }
    }

}
