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
        var idGenderInf=2;
        var attributesGender=mutableListOf<Attribute>(
            Attribute("female", TypeOfAttribute.Gender, true, 1),
            Attribute("male", TypeOfAttribute.Gender, false, 0),
            Attribute("striped trees", TypeOfAttribute.Gender, false, -1)
        )
        var attributesNumber=mutableListOf<Attribute>(
            Attribute("one", TypeOfAttribute.Gender, true, 0),
            Attribute("many", TypeOfAttribute.Gender, false, 1)
        )
        var attributesCase=mutableListOf<Attribute>(
            Attribute("именительный", TypeOfAttribute.Gender, true, 0),
            Attribute("ну и всякие другие", TypeOfAttribute.Gender, false, 1)
        )
        var attributesTime=mutableListOf<Attribute>(
            Attribute("past simple", TypeOfAttribute.Gender, true, 0),
            Attribute("present perfect", TypeOfAttribute.Gender, false, 1)
        )
        var attributesPerson=mutableListOf<Attribute>(
            Attribute("1", TypeOfAttribute.Gender, true, 0),
            Attribute("2", TypeOfAttribute.Gender, false, 1),
            Attribute("3", TypeOfAttribute.Gender, false, 1)
        )
        var attributesMood=mutableListOf<Attribute>(
            Attribute("good", TypeOfAttribute.Gender, true, 0),
            Attribute("bad", TypeOfAttribute.Gender, false, 1),
        )
        var attributesType=mutableListOf<Attribute>(
            Attribute("eins", TypeOfAttribute.Gender, true, 0),
            Attribute("zwei", TypeOfAttribute.Gender, false, 1),
        )
        var attributesVoice=mutableListOf<Attribute>(
            Attribute("действительный", TypeOfAttribute.Gender, true, 0),
            Attribute("страдательный", TypeOfAttribute.Gender, false, 1),
        )
        var attributesDegreeOfComparison=mutableListOf<Attribute>(
            Attribute("1", TypeOfAttribute.Gender, true, 0),
            Attribute("11", TypeOfAttribute.Gender, false, 1),
        )
        fun changeOthers(ind: Int, idListAttribute: Int) {
            if(idListAttribute==0) {
                for (el in attributesGender.indices) {
                    if (el != ind) attributesGender[el].isInf = false
                }
            }
        }

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