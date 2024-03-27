package com.lavenderlang

class GrammarRules {
}
class GrammarRule(){

}
class Attribute(var name: String, var type: TypeOfAttribute, var isInf: Boolean, var rusId: Int){
    override fun toString(): String {
        return name
    }
    companion object {
        fun changeOthers(ind: Int, list: MutableList<Attribute>) {
            for (el in list.indices) {
                if (el != ind) list[el].isInf = false
            }
        }
    }
}
enum class TypeOfAttribute{
    Gender, Number, Case, Time
}