package com.lavenderlang.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lavenderlang.backend.service.Serializer
import com.lavenderlang.domain.db.LanguageIdAndName
import com.lavenderlang.domain.db.LanguageItem
import com.lavenderlang.domain.db.LanguageRepository
import com.lavenderlang.domain.model.language.LanguageEntity
import kotlinx.coroutines.tasks.await

class LanguageRepositoryImpl : LanguageRepository {
    override suspend fun insertLanguage(id: Int, language: LanguageEntity) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        val languageData = hashMapOf(
            "id" to language.languageId,
            "name" to language.name,
            "description" to language.description,
            "dictionary" to Serializer.getInstance().serializeDictionary(language.dictionary),
            "grammar" to Serializer.getInstance().serializeGrammar(language.grammar),
            "vowels" to language.vowels,
            "consonants" to language.consonants,
            "puncSymbols" to Serializer.getInstance().serializePuncSymbols(language.puncSymbols),
            "capitalizedPartsOfSpeech" to Serializer.getInstance().serializeCapitalizedPartsOfSpeech(language.capitalizedPartsOfSpeech)
        )

        userDocumentRef.collection("languages").document(id.toString()).set(languageData)
            .addOnSuccessListener {
                Log.d("firebase", "language added with ID: $id")
            }
            .addOnFailureListener { e ->
                Log.d("firebase", "Error adding language", e)
            }
    }

    override suspend fun deleteLanguage(id: Int) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).delete()
            .addOnSuccessListener {
                Log.d("firebase", "language deleted")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error deleting language", e)
            }
    }

    override suspend fun updateName(id: Int, name: String) {
        // change language name in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("name", name)
            .addOnSuccessListener {
                Log.d("firebase", "name updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating name", e)
            }
    }

    override suspend fun updateDescription(id: Int, description: String) {
        // change language description in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("description", description)
            .addOnSuccessListener {
                Log.d("firebase", "description updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating description", e)
            }
    }

    override suspend fun updateDictionary(id: Int, dictionary: String) {
        // change language dictionary in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("dictionary", dictionary)
            .addOnSuccessListener {
                Log.d("firebase", "dictionary updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating dictionary", e)
            }
    }

    override suspend fun updateGrammar(id: Int, grammar: String) {
        // change language grammar in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("grammar", grammar)
            .addOnSuccessListener {
                Log.d("firebase", "grammar updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating grammar", e)
            }
    }

    override suspend fun updateVowels(id: Int, vowels: String) {
        // change language vowels in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("vowels", vowels)
            .addOnSuccessListener {
                Log.d("firebase", "vowels updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating vowels", e)
            }
    }

    override suspend fun updateConsonants(id: Int, consonants: String) {
        // change language consonants in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("consonants", consonants)
            .addOnSuccessListener {
                Log.d("firebase", "consonants updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating consonants", e)
            }
    }

    override suspend fun updatePuncSymbols(id: Int, puncSymbols: String) {
        // change language punctuation symbols in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("puncSymbols", puncSymbols)
            .addOnSuccessListener {
                Log.d("firebase", "punctuation symbols updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating punctuation symbols", e)
            }
    }

    override suspend fun updateCapitalizedPartsOfSpeech(id: Int, capitalizedPartsOfSpeech: String) {
        // change language capitalized parts of speech in database
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        // find language with this id in firebase
        userDocumentRef.collection("languages").document(id.toString()).update("capitalizedPartsOfSpeech", capitalizedPartsOfSpeech)
            .addOnSuccessListener {
                Log.d("firebase", "capitalized parts of speech updated")
            }
            .addOnFailureListener {e ->
                Log.d("firebase", "Error updating capitalized parts of speech", e)
            }
    }

    override suspend fun getLanguage(id: Int): LanguageItem? {
        return try {
            var languageItem: LanguageItem? = null
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userDocumentRef =
                FirebaseFirestore.getInstance().collection("users").document(userId)
            // find language with this id in firebase
            val document =
                userDocumentRef.collection("languages").document(id.toString()).get().await()
            if (document != null) {
                languageItem = LanguageItem(
                    (document.data?.get("id") as Long).toInt(),
                    document.data?.get("name") as String,
                    document.data?.get("description") as String,
                    document.data?.get("dictionary") as String,
                    document.data?.get("grammar") as String,
                    document.data?.get("vowels") as String,
                    document.data?.get("consonants") as String,
                    document.data?.get("puncSymbols") as String,
                    document.data?.get("capitalizedPartsOfSpeech") as String
                )
                Log.d("firebase", "success getting")
            } else {
                Log.d("firebase", "No such document")
            }
            languageItem
        } catch (e: Exception) {
            Log.d("firebase", "Error getting language", e)
            null
        }
    }

    override suspend fun exists(id: Int): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        return try {
            val document =
                userDocumentRef.collection("languages").document(id.toString()).get().await()
            document.exists()
        } catch (e: Exception) {
            Log.d("firebase", "Error getting language", e)
            false
        }
    }

    override suspend fun getShortLanguageItems(): List<LanguageIdAndName> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userDocumentRef =
                FirebaseFirestore.getInstance().collection("users").document(userId)
            val languageItems = mutableListOf<LanguageIdAndName>()
            // get all languages from the collection
            val result = userDocumentRef.collection("languages").get().await()
            for (document in result) {
                languageItems.add(
                    LanguageIdAndName(
                        (document.data["id"] as Long).toInt(),
                        document.data["name"] as String
                    )
                )
            }
            Log.d("LanguageRepositoryImpl", languageItems.toString())
            languageItems
        } catch (e: Exception) {
            Log.d("LanguageRepositoryImpl", "Error getting languages")
            emptyList()
        }
    }

    override suspend fun getMaxId(): Int {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        var maxId = 0
        return try {
            val result = userDocumentRef.collection("languages").get().await()
            for (document in result) {
                val id = (document.data["id"] as Long).toInt()
                if (id > maxId) {
                    maxId = id
                }
            }
            maxId
        }
        catch (e: Exception) {
            Log.d("firebase", "Error getting max id", e)
            0
        }
    }

    override suspend fun createUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        // create user empty document in users collection
        val userCollection: MutableMap<String, Any> = HashMap()
        FirebaseFirestore.getInstance().collection("users").document(userId).set(userCollection)
            .addOnSuccessListener {
                Log.d("firebase", "DocumentSnapshot added with ID: $userId")
            }
            .addOnFailureListener { e ->
                Log.d("firebase", "Error adding document", e)
            }
    }
}