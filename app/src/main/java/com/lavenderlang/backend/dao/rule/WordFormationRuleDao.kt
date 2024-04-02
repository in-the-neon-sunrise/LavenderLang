package com.lavenderlang.backend.dao.rule

import android.content.Context
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
import com.lavenderlang.backend.service.ForbiddenSymbolsException
import com.lavenderlang.languages

interface WordFormationRuleDao : RuleDao {
    fun updateTransformation(rule : WordFormationRuleEntity, newTransformation: TransformationEntity)
    fun updateDescription(rule : WordFormationRuleEntity, newDescription: String)
    fun updateImmutableAttrs(rule : WordFormationRuleEntity, newAttrs: MutableMap<Attributes, Int>)
    fun wordFormationTransformByRule(word : IWordEntity, rule : WordFormationRuleEntity) : IWordEntity

}
class WordFormationRuleDaoImpl : WordFormationRuleDao {
    override fun updateMasc(rule: IRuleEntity, newMasc: MascEntity, context: Context) {
        rule.masc = newMasc
    }

    override fun updateTransformation(rule: WordFormationRuleEntity, newTransformation: TransformationEntity) {
        //check if rule is correct (letters in transformation are in language)
        for (letter in newTransformation.addToBeginning) {
            if (!languages[rule.languageId]!!.vowels.contains(letter) &&
                !languages[rule.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        for (letter in newTransformation.addToEnd) {
            if (!languages[rule.languageId]!!.vowels.contains(letter) &&
                !languages[rule.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
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