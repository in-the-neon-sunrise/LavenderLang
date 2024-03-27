package com.lavenderlang

class Languages {
    companion object {
        var languages = mutableListOf<Language>(Language("Бес ты или не бес?"),
            Language("Порождение ада или небес?"),
            Language("С тобою я, но ты меня без"),
            Language("Без меня и я"),
            Language("Рай?"),
            Language("Нет, это не рай"),
            Language("Инферно?", "aaaaaaaaaaaaa aaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaa aa aaaaaaaaaa aaaaaaaaaaaaaaaaaaaaa aaaaaa"),
            Language("Это не инферно"),
            Language("Не привет и не прощай"),
            Language("Ты неверна"),
            Language("Я наверное", "зомби"))
        fun add(name: String) {
            languages.add(Language(name))
        }
        var attributesGender=mutableListOf<Attribute>(
            Attribute("female", TypeOfAttribute.Gender, true, 1),
            Attribute("male", TypeOfAttribute.Gender, false, 0),
            Attribute("striped trees", TypeOfAttribute.Gender, false, -1)
        )
    }
}
class Language(var name: String, var description: String = " ", var letters:Set<Char> = setOf()){
    override fun toString(): String {
        return name
    }
    public fun stringLetters(): String {
        var str:String="";
        for(el in letters){
            str+=el
            str+=" "
        }
        return str
    }
    public fun setLetters(str: String){

    }
}