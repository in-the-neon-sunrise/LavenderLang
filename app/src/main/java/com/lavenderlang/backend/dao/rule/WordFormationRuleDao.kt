package com.lavenderlang.backend.dao.rule

import com.lavenderlang.backend.dao.help.TransformationDaoImpl
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.entity.word.AdjectiveEntity
import com.lavenderlang.backend.entity.word.AdverbEntity
import com.lavenderlang.backend.entity.word.FuncPartEntity
import com.lavenderlang.backend.entity.word.IWordEntity
import com.lavenderlang.backend.entity.word.NounEntity
import com.lavenderlang.backend.entity.word.NumeralEntity
import com.lavenderlang.backend.entity.word.ParticipleEntity
import com.lavenderlang.backend.entity.word.PronounEntity
import com.lavenderlang.backend.entity.word.VerbEntity
import com.lavenderlang.backend.entity.word.VerbParticipleEntity

interface WordFormationRuleDao : RuleDao {
    fun updateTransformation(rule : WordFormationRuleEntity, newTransformation: TransformationEntity)
    fun updateDescription(rule : WordFormationRuleEntity, newDescription: String)
    fun updateImmutableAttrs(rule : WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun wordFormationTransformByRule(word : IWordEntity, rule : WordFormationRuleEntity) : IWordEntity

}
class WordFormationRuleDaoImpl : WordFormationRuleDao {
    override fun updateMasc(rule: IRuleEntity, newMasc: MascEntity) {
        rule.masc = newMasc
    }

    override fun updateTransformation(rule: WordFormationRuleEntity, newTransformation: TransformationEntity) {
        rule.transformation = newTransformation
    }

    override fun updateDescription(rule: WordFormationRuleEntity, newDescription: String) {
        rule.description = newDescription
    }

    override fun updateImmutableAttrs(rule: WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>) {
        rule.immutableAttrs = newAttrs
    }
    override fun wordFormationTransformByRule(word: IWordEntity, rule: WordFormationRuleEntity) : IWordEntity {
        val transformedWord : IWordEntity = when (word) {
            is NounEntity -> word.copy()
            is VerbEntity -> word.copy()
            is AdjectiveEntity -> word.copy()
            is AdverbEntity -> word.copy()
            is ParticipleEntity -> word.copy()
            is VerbParticipleEntity -> word.copy()
            is PronounEntity -> word.copy()
            is NumeralEntity -> word.copy()
            is FuncPartEntity -> word.copy()
            else -> throw Error() // какую-нибудь красивую
        }
        val newWord = TransformationDaoImpl().transform(rule.transformation, word.word)
        transformedWord.word = newWord
        transformedWord.translation = "Введите перевод" // мы сами "кошечка" из "кошка" не образуем
        transformedWord.immutableAttrs = rule.immutableAttrs
        return transformedWord
    }
}