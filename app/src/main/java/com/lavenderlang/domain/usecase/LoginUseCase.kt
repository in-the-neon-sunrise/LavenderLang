package com.lavenderlang.domain.usecase

import com.lavenderlang.domain.auth.UserRepository
import com.lavenderlang.domain.auth.UserModel

class LoginUseCase {
    companion object {
        suspend fun execute(
            user: UserModel,
            repo: UserRepository
        ) {
            repo.login(user)
        }
    }
}