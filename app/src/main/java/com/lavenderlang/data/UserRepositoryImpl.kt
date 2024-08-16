package com.lavenderlang.data

import com.google.firebase.auth.FirebaseAuth
import com.lavenderlang.domain.auth.UserRepository
import com.lavenderlang.domain.auth.UserModel
import kotlinx.coroutines.tasks.await


class UserRepositoryImpl : UserRepository {

    override suspend fun register(user: UserModel) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            user.email,
            user.password
        ).await()
    }

    override suspend fun login(user: UserModel) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            user.email,
            user.password
        ).await()
    }

}