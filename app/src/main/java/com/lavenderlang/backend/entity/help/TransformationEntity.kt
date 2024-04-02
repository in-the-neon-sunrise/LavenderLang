package com.lavenderlang.backend.entity.help

data class TransformationEntity (
    var delFromBeginning : Int = 0,
    var delFromEnd : Int = 0,
    var addToBeginning : String = "",
    var addToEnd : String = ""
)