package com.lavenderlang.domain

import com.lavenderlang.domain.model.UserModel

interface UserRepository {

    suspend fun register(user: UserModel)
    suspend fun login(user: UserModel)

}