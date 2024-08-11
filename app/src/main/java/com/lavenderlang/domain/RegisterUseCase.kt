package com.lavenderlang.domain

import com.lavenderlang.domain.model.UserModel

class RegisterUseCase {
    companion object {
        suspend fun execute(
            user: UserModel,
            repo: UserRepository
        ) {
            repo.register(user)
        }
    }
}