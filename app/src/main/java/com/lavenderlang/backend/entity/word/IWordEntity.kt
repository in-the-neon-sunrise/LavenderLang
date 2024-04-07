package com.lavenderlang.backend.entity.word

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.lavenderlang.backend.entity.help.*

class IWordEntityKeySerializer : JsonSerializer<IWordEntity>() {
    override fun serialize(value: IWordEntity, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeFieldName(value.word)
    }
}

class IWordEntityKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext): IWordEntity {
        return NounEntity(word = key)
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = NounEntity::class,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = NounEntity::class, name = "noun"),
    JsonSubTypes.Type(value = VerbEntity::class, name = "verb"),
    JsonSubTypes.Type(value = AdjectiveEntity::class, name = "adjective"),
    JsonSubTypes.Type(value = AdverbEntity::class, name = "adverb"),
    JsonSubTypes.Type(value = ParticipleEntity::class, name = "participle"),
    JsonSubTypes.Type(value = VerbParticipleEntity::class, name = "verbparticiple"),
    JsonSubTypes.Type(value = PronounEntity::class, name = "pronoun"),
    JsonSubTypes.Type(value = NumeralEntity::class, name = "numeral"),
    JsonSubTypes.Type(value = FuncPartEntity::class, name = "funcpart")
)
interface IWordEntity {
    var languageId : Int
    var word : String
    var translation : String
    var mutableAttrs : MutableMap<Attributes, Int>
    var immutableAttrs : MutableMap<Attributes, Int>
    var partOfSpeech : PartOfSpeech
}