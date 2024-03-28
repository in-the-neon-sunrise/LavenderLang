package com.lavenderlang.backend.entity.help

data class Characteristic(
    val languageId: Int = 0,
    val characteristicId : Int = 0,
    var type: Attributes = Attributes.TYPE,
    var name: String = "",
    var russianId: Int = 0, // соотв. с хар-ками русского - надо для переводчика. 0 - infinitive
    var isInfinitive: Boolean = false)