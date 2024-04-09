package com.lavenderlang.backend.dao.language

import androidx.lifecycle.lifecycleScope
import com.lavenderlang.MainActivity
import com.lavenderlang.backend.dao.word.WordDaoImpl
import com.lavenderlang.backend.data.LanguageRepository
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.language.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.service.exception.ForbiddenSymbolsException
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface GrammarDao {
    fun addOption(grammar : GrammarEntity, option : CharacteristicEntity)
    fun deleteOption(grammar : GrammarEntity, option : CharacteristicEntity)
    fun updateOption(grammar : GrammarEntity, optionId: Int, newOption: CharacteristicEntity)
    fun addGrammarRule(grammar : GrammarEntity, rule: GrammarRuleEntity)
    fun deleteGrammarRule(grammar : GrammarEntity, rule : GrammarRuleEntity)
    fun addWordFormationRule(grammar : GrammarEntity, rule: WordFormationRuleEntity)
    fun deleteWordFormationRule(grammar : GrammarEntity, rule : WordFormationRuleEntity)
}

class GrammarDaoImpl(private val helper : DictionaryHelperDaoImpl = DictionaryHelperDaoImpl(),
                     private val languageRepository: LanguageRepository = LanguageRepository()
) : GrammarDao {
    override fun addOption(grammar: GrammarEntity, option: CharacteristicEntity) {
        when (option.type) {
            Attributes.GENDER -> grammar.varsGender[grammar.nextIds[option.type]!!] = option
            Attributes.NUMBER -> grammar.varsNumber[grammar.nextIds[option.type]!!] = option
            Attributes.CASE -> grammar.varsCase[grammar.nextIds[option.type]!!] = option
            Attributes.TIME -> grammar.varsTime[grammar.nextIds[option.type]!!] = option
            Attributes.PERSON -> grammar.varsPerson[grammar.nextIds[option.type]!!] = option
            Attributes.MOOD -> grammar.varsMood[grammar.nextIds[option.type]!!] = option
            Attributes.TYPE -> grammar.varsType[grammar.nextIds[option.type]!!] = option
            Attributes.VOICE -> grammar.varsVoice[grammar.nextIds[option.type]!!] = option
            Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison[grammar.nextIds[option.type]!!] = option
            Attributes.IS_INFINITIVE -> return
        }
        grammar.nextIds[option.type] = grammar.nextIds[option.type]!! + 1
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(),
                grammar.languageId,
                Serializer.getInstance().serializeLanguage(languages[grammar.languageId]!!)
            )
        }
    }

    override fun deleteOption(grammar: GrammarEntity, option: CharacteristicEntity) {
        when (option.type) {
            Attributes.GENDER -> grammar.varsGender.remove(option.characteristicId)
            Attributes.NUMBER -> grammar.varsNumber.remove(option.characteristicId)
            Attributes.CASE -> grammar.varsCase.remove(option.characteristicId)
            Attributes.TIME -> grammar.varsTime.remove(option.characteristicId)
            Attributes.PERSON -> grammar.varsPerson.remove(option.characteristicId)
            Attributes.MOOD -> grammar.varsMood.remove(option.characteristicId)
            Attributes.TYPE -> grammar.varsType.remove(option.characteristicId)
            Attributes.VOICE -> grammar.varsVoice.remove(option.characteristicId)
            Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison.remove(option.characteristicId)
            Attributes.IS_INFINITIVE -> return
        }
        val wordDao = WordDaoImpl()
        for (word in languages[grammar.languageId]!!.dictionary.dict) {
            if (word.mutableAttrs.contains(option.type) && word.mutableAttrs[option.type] == option.characteristicId) {
                val immutAttrs = word.immutableAttrs
                immutAttrs[option.type] = 0 // инфинитив
                wordDao.updateImmutableAttrs(word, immutAttrs)
            }
        }
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(),
                grammar.languageId,
                Serializer.getInstance().serializeLanguage(languages[grammar.languageId]!!)
            )
        }
    }

    override fun updateOption(
        grammar: GrammarEntity,
        optionId: Int,
        newOption: CharacteristicEntity
    ) {
        val map: MutableMap<Int, CharacteristicEntity> = when (newOption.type) {
            Attributes.GENDER -> grammar.varsGender
            Attributes.NUMBER -> grammar.varsNumber
            Attributes.CASE -> grammar.varsCase
            Attributes.TIME -> grammar.varsTime
            Attributes.PERSON -> grammar.varsPerson
            Attributes.MOOD -> grammar.varsMood
            Attributes.TYPE -> grammar.varsType
            Attributes.VOICE -> grammar.varsVoice
            Attributes.DEGREE_OF_COMPARISON -> grammar.varsDegreeOfComparison
            Attributes.IS_INFINITIVE -> return
        }
        if (!map.contains(optionId)) return
        map[optionId] = newOption
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            languageRepository.updateLanguage(
                MainActivity.getInstance(),
                grammar.languageId,
                Serializer.getInstance().serializeLanguage(languages[grammar.languageId]!!)
            )
        }
        return
    }

    override fun addGrammarRule(grammar: GrammarEntity, rule: GrammarRuleEntity) {
        //check if rule is correct (letters in transformation are in language)
        for (letter in rule.transformation.addToBeginning) {
            if (!languages[grammar.languageId]!!.vowels.contains(letter) &&
                !languages[grammar.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }
        for (letter in rule.transformation.addToEnd) {
            if (!languages[grammar.languageId]!!.vowels.contains(letter) &&
                !languages[grammar.languageId]!!.consonants.contains(letter)) {
                throw ForbiddenSymbolsException("Letter $letter is not in language")
            }
        }

        grammar.grammarRules.add(rule)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.addMadeByRule(languages[grammar.languageId]!!.dictionary, rule)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), grammar.languageId,
                Serializer.getInstance().serializeLanguage(languages[grammar.languageId]!!)
            )
        }
    }

    override fun deleteGrammarRule(grammar: GrammarEntity, rule: GrammarRuleEntity) {
        grammar.grammarRules.remove(rule)
        MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            helper.delMadeByRule(languages[grammar.languageId]!!.dictionary, rule)
            languageRepository.updateLanguage(
                MainActivity.getInstance(), grammar.languageId,
                Serializer.getInstance().serializeLanguage(languages[grammar.languageId]!!)
            )
        }
    }

    override fun addWordFormationRule(grammar: GrammarEntity, rule: WordFormationRuleEntity) {
        grammar.wordFormationRules.add(rule)
    }

    override fun deleteWordFormationRule(grammar: GrammarEntity, rule: WordFormationRuleEntity) {
        grammar.wordFormationRules.remove(rule)
    }
}