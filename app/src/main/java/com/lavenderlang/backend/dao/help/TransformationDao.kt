package com.lavenderlang.backend.dao.help

import com.lavenderlang.backend.entity.help.TransformationEntity

interface TransformationDao {
    fun updateDelFromBeginning(transformation: TransformationEntity, newNum : Int);
    fun updateDelFromEnd(transformation: TransformationEntity, newNum : Int);
    fun updateAddToBeginning(transformation: TransformationEntity, newString : String);
    fun updateAddToEnd(transformation: TransformationEntity, newString : String);
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
}