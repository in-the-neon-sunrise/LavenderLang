package com.lavenderlang.backend.entity.help

data class CharacteristicEntity(
    val characteristicId : Int = 0,
    var type: Attributes = Attributes.TYPE,
    var name: String = "",
    var russianId: Int = 0 // соотв. с признаками русского - надо для переводчика. 0 - infinitive
)