package com.lavenderlang.domain.usecase

import com.lavenderlang.domain.auth.UserRepository
import com.lavenderlang.domain.auth.UserModel
import com.lavenderlang.domain.db.LanguageRepository

class RegisterUseCase {
    companion object {
        suspend fun execute(
            user: UserModel,
            userRepo: UserRepository,
            langRepo: LanguageRepository
        ) {
            userRepo.register(user)
            langRepo.createUser()
        }
    }
}