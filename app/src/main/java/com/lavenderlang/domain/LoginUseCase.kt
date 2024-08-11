package com.lavenderlang.domain

import com.lavenderlang.domain.model.UserModel

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