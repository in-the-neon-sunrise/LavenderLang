package com.lavenderlang.backend.dao.word

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.lavenderlang.backend.dao.help.MascDaoImpl
import com.lavenderlang.backend.entity.help.*
import com.lavenderlang.backend.entity.rule.*
import com.lavenderlang.backend.entity.word.*
import com.lavenderlang.languages

interface WordDao {
    fun grammarTransformByAttrs(word : IWordEntity, args : MutableMap<Attributes, Int>) : IWordEntity;
    fun grammarTransformByRule(word : IWordEntity, rule: GrammarRuleEntity) : IWordEntity;
    fun wordFormationTransform(word : IWordEntity, args : MutableMap<Attributes, Int>) : IWordEntity;
    fun wordFormationTransformByRule(word : IWordEntity, rule : WordFormationRuleEntity) : IWordEntity;
    fun updateWord(word : IWordEntity, newWord : String);
    fun updateTranslation(word : IWordEntity, newTranslation : String);
    fun updateImmutableArg(word : IWordEntity, attribute: Attributes, newId : Int) : Boolean;
}

class WordDaoImpl : WordDao {
    override fun grammarTransformByRule(word: IWordEntity, rule: GrammarRuleEntity): IWordEntity {
        val transformedWord: IWordEntity = when (word) {
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
        val newWord = rule.transformation.addToBeginning + word.word.slice(
            IntRange(
                rule.transformation.delFromBeginning,
                word.word.length - rule.transformation.delFromEnd - 1
            )
        ) +
                rule.transformation.addToEnd
        transformedWord.word = newWord
        for (attr in rule.mutableAttrs.keys) {
            transformedWord.mutableAttrs[attr] = rule.mutableAttrs[attr]!!
        }

        val py = Python.getInstance()
        val module = py.getModule("pm3")
        val res = module.callAttr(
            "inflectAttrs", word.word,
            word.partOfSpeech.toString(),
            transformedWord.mutableAttrs.values.toString()
        ).toString()
        transformedWord.translation = res

        return transformedWord
    }
    override fun grammarTransformByAttrs(word: IWordEntity, args: MutableMap<Attributes, Int>): IWordEntity {
        val transformedWord: IWordEntity = when (word) {
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
        val mascHandler = MascDaoImpl();
        val grammar = languages[word.languageId]!!.grammar
        for (rule in grammar.grammarRules) {
            if (mascHandler.fits(rule.masc, word)) {
                var check = true
                for (attr in args.keys) {
                    if (!rule.mutableAttrs.contains(attr) || rule.mutableAttrs[attr]!! != args[attr]) {
                        check = false
                        break
                    }
                }
                if (!check) continue
                return grammarTransformByRule(word, rule)
            }
        }
        return transformedWord
    }

    override fun wordFormationTransform(word: IWordEntity, args: MutableMap<Attributes, Int>): IWordEntity {
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
        val mascHandler = MascDaoImpl();
        val grammar = languages[word.languageId]!!.grammar
        for (rule in grammar.wordFormationRules) {
            if (mascHandler.fits(rule.masc, word)) {
                var check = true
                for (attr in args.keys) {
                    if (!rule.immutableAttrs.contains(attr) || rule.immutableAttrs[attr]!! != args[attr]) {
                        check = false
                        break
                    }
                }
                if (!check) continue
                val newWord = rule.transformation.addToBeginning + word.word.slice(
                    IntRange(rule.transformation.delFromBeginning,
                        word.word.length - rule.transformation.delFromEnd - 1)) +
                        rule.transformation.addToEnd
                transformedWord.word = newWord
                transformedWord.translation = "Введите перевод" // мы сами "кошечка" из "кошка" не образуем
                for (attr in args.keys) {
                    transformedWord.immutableAttrs[attr] = args[attr]!!
                }
                return transformedWord
            }
        }
        throw Error() // but like pretty error... Not Found??
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
        val newWord = rule.transformation.addToBeginning + word.word.slice(
            IntRange(rule.transformation.delFromBeginning,
                word.word.length - rule.transformation.delFromEnd - 1)) +
                rule.transformation.addToEnd
        transformedWord.word = newWord
        transformedWord.translation = "Введите перевод" // мы сами "кошечка" из "кошка" не образуем
        transformedWord.immutableAttrs = rule.immutableAttrs
        return transformedWord
    }

    override fun updateWord(word: IWordEntity, newWord: String) {
        word.word = newWord
    }

    override fun updateTranslation(word: IWordEntity, newTranslation: String) {
        word.translation = newTranslation
    }

    override fun updateImmutableArg(word: IWordEntity, attribute: Attributes, newId: Int): Boolean {
        if (!word.immutableAttrs.contains(attribute)) return false;
        word.immutableAttrs[attribute] = newId
        return true
    }
}