package com.lavenderlang.domain.auth

enum class State {
    LOADING,
    SUCCESS,
    ERROR_USER_ALREADY_EXISTS,
    ERROR_WEAK_PASSWORD,
    ERROR_INVALID_CREDENTIALS,
    ERROR
}