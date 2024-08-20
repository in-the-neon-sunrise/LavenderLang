package com.lavenderlang.domain.auth

interface UserRepository {

    suspend fun register(user: UserModel)
    suspend fun login(user: UserModel)

}