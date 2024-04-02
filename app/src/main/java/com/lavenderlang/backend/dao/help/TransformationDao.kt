package com.lavenderlang.backend.dao.help

import com.lavenderlang.backend.entity.help.TransformationEntity

interface TransformationDao {
    fun updateDelFromBeginning(transformation: TransformationEntity, newNum : Int)
    fun updateDelFromEnd(transformation: TransformationEntity, newNum : Int)
    fun updateAddToBeginning(transformation: TransformationEntity, newString : String)
    fun updateAddToEnd(transformation: TransformationEntity, newString : String)
    fun transform(transformation: TransformationEntity, word : String) : String
}
class TransformationDaoImpl : TransformationDao {
    override fun updateDelFromBeginning(transformation: TransformationEntity, newNum : Int) {
        transformation.delFromBeginning = newNum
    }
    override fun updateDelFromEnd(transformation: TransformationEntity, newNum : Int) {
        transformation.delFromEnd = newNum
    }
    override fun updateAddToBeginning(transformation: TransformationEntity, newString : String) {
        transformation.addToBeginning = newString
    }
    override fun updateAddToEnd(transformation: TransformationEntity, newString : String) {
        transformation.addToEnd = newString
    }

    override fun transform(transformation: TransformationEntity, word: String): String {
        return transformation.addToBeginning + word.slice(
            IntRange(transformation.delFromBeginning,
                word.length - transformation.delFromEnd - 1)) +
                transformation.addToEnd
    }
}